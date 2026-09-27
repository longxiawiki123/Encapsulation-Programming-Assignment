package instructor;

import HRServices.Records.EmergencyContact;
import HRServices.Records.EmployeeInfo;
import HRServices.Records.LocationInfo;
import HRServices.Enums.WorkLocation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.IntFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class FormattingTest {
    private static final String ADDRESS_TEXT = """
            Street: 120 Juniper Lane
            City: Lexington
            State: Kentucky
            ZIP code: 40507""";
    private static final String CONTACT_TEXT = "First name: Ava\nLast name: Bennett\nAddress:\n"
            + indented(ADDRESS_TEXT, 1)
            + "\nTelephone: 859-555-0100\nEmail: ava.bennett@example.com";
    private static final String LOCATION_TEXT = """
            Building: Building D
            Room: 405
            Address:
            \tStreet: 360 Aspen Meadow Road
            \tCity: Denver
            \tState: Colorado
            \tZIP code: 80202""";
    private static final String EMPLOYMENT_TEXT = """
            Job title: Software Engineer
            Hire date: 2021-06-14
            Employment type: Full-time
            Division: Engineering
            Work location: Denver Office
            """ + indented(LOCATION_TEXT, 1);

    record FormatCase(Object record, IntFunction<String> format, String expected) {
        @Override
        public String toString() {
            return record.getClass().getSimpleName();
        }
    }

    private static String indented(String text, int level) {
        return "\t".repeat(level) + text.replace("\n", "\n" + "\t".repeat(level));
    }

    private static String employeeText(EmployeeInfo employee) {
        String text = "Employee ID: EMP0001\nEmployment:\n"
                + indented(EMPLOYMENT_TEXT, 1)
                + "\nEmployee contact:\n" + indented(CONTACT_TEXT, 1)
                + "\nSupervisor contact:\n" + employee.supervisorContact().toString(1)
                + "\nEmergency contacts: " + employee.emergencyContacts().length;
        EmergencyContact[] contacts = employee.emergencyContacts();
        for (int i = 0; i < contacts.length; i++) {
            text += "\n\tEmergency contact " + (i + 1) + ":\n"
                    + contacts[i].toString(2);
        }
        return text;
    }

    static Stream<FormatCase> cases() {
        LocationInfo location = WorkLocation.DENVER_OFFICE.getLocationInfo();
        EmergencyContact emergency = Fixtures.emergencyContacts(1).getFirst();
        return Stream.of(
                new FormatCase(Fixtures.employeeContact().address(),
                        Fixtures.employeeContact().address()::toString, ADDRESS_TEXT),
                new FormatCase(Fixtures.employeeContact(),
                        Fixtures.employeeContact()::toString, CONTACT_TEXT),
                new FormatCase(location, location::toString, LOCATION_TEXT),
                new FormatCase(Fixtures.employmentInfo(),
                        Fixtures.employmentInfo()::toString, EMPLOYMENT_TEXT),
                new FormatCase(emergency, emergency::toString,
                        "Relationship: Friend\nContact:\n" + emergency.contactInfo().toString(1)),
                new FormatCase(Fixtures.employee(), Fixtures.employee()::toString,
                        employeeText(Fixtures.employee())));
    }

    static Stream<Arguments> formats() {
        return cases().flatMap(test -> Stream.of(0, 1, 2, 3, 5, 10)
                .map(level -> Arguments.of(test, level)));
    }

    static Stream<Arguments> negativeLevels() {
        return cases().flatMap(test -> Stream.of(-1, -2, Integer.MIN_VALUE)
                .map(level -> Arguments.of(test, level)));
    }

    @ParameterizedTest(name = "{0}: exact formatting at tab level {1}")
    @MethodSource("formats")
    void everyRecordFormatsAllFieldsWithExactNestedTabs(FormatCase test, int level) {
        String actual = test.format().apply(level);
        assertEquals(indented(test.expected(), level), actual);
        assertEquals(test.expected().lines().count(), actual.lines().count());
        assertFalse(actual.endsWith("\n"));
        assertFalse(actual.contains("\r"));
    }

    @ParameterizedTest
    @MethodSource("cases")
    void noArgumentToStringUsesZeroTabs(FormatCase test) {
        assertEquals(test.expected(), test.record().toString());
        assertEquals(test.format().apply(0), test.record().toString());
    }

    @ParameterizedTest
    @MethodSource("negativeLevels")
    void everyRecordRejectsNegativeTabLevels(FormatCase test, int level) {
        assertThrows(IllegalArgumentException.class, () -> test.format().apply(level));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 5, 10})
    void everyEmergencyContactGetsItsOwnNumberedSection(int count) {
        EmployeeInfo employee = Fixtures.employee(count);
        String actual = employee.toString();
        assertEquals(employeeText(employee), actual);
        assertEquals(35 + 12L * count, actual.lines().count());
        assertEquals(count, actual.lines()
                .filter(line -> line.matches("\tEmergency contact \\d+:")).count());
        if (count == 0) {
            assertTrue(actual.endsWith("Emergency contacts: 0"));
        }
    }

    @Test
    void formattingPreservesUnicodeApostrophesAndLeadingZeroes() {
        String text = Fixtures.emergencyContact1().toString(2);
        assertTrue(text.contains("\t\tFirst name: Léa\n"));
        assertTrue(text.contains("\t\tLast name: O'Neil\n"));
        assertTrue(text.contains("\t\t\tZIP code: 04101\n"));
    }
}
