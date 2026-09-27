package instructor;

import HRServices.Records.EmergencyContact;
import HRServices.Records.EmployeeInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class EmergencyContactArrayTest {
    private EmployeeInfo employeeWith(EmergencyContact[] contacts) {
        return new EmployeeInfo("EMP0001", Fixtures.employeeContact(), Fixtures.supervisorContact(),
                Fixtures.employmentInfo(), contacts);
    }

    private EmployeeInfo.Builder builder() {
        return new EmployeeInfo.Builder()
                .employeeId("EMP0001")
                .employeeContact(Fixtures.employeeContact())
                .supervisorContact(Fixtures.supervisorContact())
                .employmentInfo(Fixtures.employmentInfo());
    }

    @Test
    void recordStoresAnInstanceArrayAndBuilderStoresAnInstanceArrayList() throws Exception {
        var recordField = EmployeeInfo.class.getDeclaredField("emergencyContacts");
        assertEquals(EmergencyContact[].class, recordField.getType());
        assertFalse(Modifier.isStatic(recordField.getModifiers()));
        assertTrue(Modifier.isFinal(recordField.getModifiers()));

        var builderField = EmployeeInfo.Builder.class.getDeclaredField("emergencyContacts");
        assertEquals(ArrayList.class, builderField.getType());
        assertFalse(Modifier.isStatic(builderField.getModifiers()));
        assertEquals("java.util.ArrayList<HRServices.Records.EmergencyContact>",
                builderField.getGenericType().getTypeName());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 10, 100})
    void buildAllocatesExactlyTheListSizeAndPreservesContactOrder(int count) {
        ArrayList<EmergencyContact> contacts = Fixtures.emergencyContacts(count);
        EmployeeInfo employee = builder().emergencyContacts(contacts).build();
        EmergencyContact[] result = employee.emergencyContacts();
        assertEquals(count, result.length);
        assertArrayEquals(Fixtures.emergencyContactArray(count), result);
        for (int i = 0; i < count; i++) {
            assertSame(contacts.get(i), result[i], "Immutable contact records can be shared.");
        }
        assertEquals(Fixtures.employee(count), employee);
    }

    @Test
    void emptyListIsTheBuilderDefaultAndProducesAnEmptyArray() {
        EmployeeInfo employee = builder().build();
        assertEquals(Fixtures.employee(0), employee);
        assertEquals(0, employee.emergencyContacts().length);
    }

    @Test
    void unusedListCapacityDoesNotProduceExtraArraySlots() {
        ArrayList<EmergencyContact> contacts = new ArrayList<>(100);
        contacts.addAll(Fixtures.emergencyContacts(2));
        EmployeeInfo employee = builder().emergencyContacts(contacts).build();
        assertEquals(2, employee.emergencyContacts().length);
        assertArrayEquals(Fixtures.emergencyContactArray(2), employee.emergencyContacts());
    }

    @Test
    void constructorCopiesItsInputArray() {
        EmergencyContact[] contacts = Fixtures.emergencyContactArray(2);
        EmployeeInfo employee = employeeWith(contacts);
        contacts[0] = Fixtures.emergencyContactArray(3)[2];
        assertEquals(Fixtures.employee(), employee);
        assertArrayEquals(Fixtures.emergencyContactArray(2), employee.emergencyContacts());
    }

    @Test
    void builderTakesASnapshotWhenBuildIsCalled() {
        ArrayList<EmergencyContact> contacts = Fixtures.emergencyContacts(2);
        EmployeeInfo.Builder builder = builder().emergencyContacts(contacts);
        EmployeeInfo first = builder.build();
        contacts.add(Fixtures.emergencyContactArray(3)[2]);
        EmployeeInfo second = builder.build();
        contacts.clear();
        EmployeeInfo empty = builder.build();

        assertEquals(Fixtures.employee(2), first);
        assertEquals(Fixtures.employee(3), second);
        assertEquals(Fixtures.employee(0), empty);
        assertEquals(2, first.emergencyContacts().length);
        assertEquals(3, second.emergencyContacts().length);
        assertEquals(0, empty.emergencyContacts().length);
    }

    @Test
    void replacingAListElementBeforeBuildChangesOnlySubsequentRecords() {
        ArrayList<EmergencyContact> contacts = Fixtures.emergencyContacts(2);
        EmployeeInfo.Builder builder = builder().emergencyContacts(contacts);
        EmployeeInfo first = builder.build();
        contacts.set(0, Fixtures.emergencyContactArray(3)[2]);
        EmployeeInfo second = builder.build();
        assertEquals(Fixtures.employee(), first);
        assertNotEquals(first, second);
        assertArrayEquals(contacts.toArray(new EmergencyContact[contacts.size()]),
                second.emergencyContacts());
    }

    @Test
    void everyAccessorCallReturnsAnIndependentArray() {
        EmployeeInfo employee = Fixtures.employee();
        EmergencyContact[] first = employee.emergencyContacts();
        EmergencyContact[] second = employee.emergencyContacts();
        assertNotSame(first, second);
        assertArrayEquals(first, second);
        first[0] = Fixtures.emergencyContactArray(3)[2];
        second[1] = second[0];
        assertEquals(Fixtures.employee(), employee);
        assertArrayEquals(Fixtures.emergencyContactArray(2), employee.emergencyContacts());
    }

    @Test
    void externalArrayEditsDoNotChangeEqualityHashCodesOrPrintedText() {
        EmergencyContact[] input = Fixtures.emergencyContactArray(2);
        EmployeeInfo employee = employeeWith(input);
        int hash = employee.hashCode();
        String text = employee.toString();
        HashSet<EmployeeInfo> set = new HashSet<>(List.of(employee));
        Arrays.fill(input, Fixtures.emergencyContactArray(3)[2]);
        Arrays.fill(employee.emergencyContacts(), Fixtures.emergencyContactArray(3)[2]);
        assertEquals(Fixtures.employee(), employee);
        assertEquals(hash, employee.hashCode());
        assertEquals(text, employee.toString());
        assertTrue(set.contains(Fixtures.employee()));
    }

    @Test
    void contactOrderContributesToEmployeeEqualityAndPrintedOrder() {
        ArrayList<EmergencyContact> contacts = Fixtures.emergencyContacts(2);
        Collections.reverse(contacts);
        EmployeeInfo reversed = builder().emergencyContacts(contacts).build();
        assertNotEquals(Fixtures.employee(), reversed);
        String text = reversed.toString();
        assertTrue(text.indexOf("First name: Marco") < text.indexOf("First name: Léa"));
        assertArrayEquals(contacts.toArray(new EmergencyContact[contacts.size()]),
                reversed.emergencyContacts());
    }

    @Test
    void repeatedContactsArePreservedRatherThanDeduplicated() {
        EmergencyContact contact = Fixtures.emergencyContactArray(1)[0];
        ArrayList<EmergencyContact> contacts = new ArrayList<>(List.of(contact, contact, contact));
        EmployeeInfo employee = builder().emergencyContacts(contacts).build();
        assertArrayEquals(new EmergencyContact[]{contact, contact, contact}, employee.emergencyContacts());
        assertEquals(3, employee.toString().lines()
                .filter(line -> line.stripLeading().equals("First name: Léa")).count());
    }

    @Test
    void repeatedBuildsProduceSeparateValueEqualRecords() {
        EmployeeInfo.Builder builder = builder().emergencyContacts(Fixtures.emergencyContacts(2));
        EmployeeInfo first = builder.build();
        EmployeeInfo second = builder.build();
        assertNotSame(first, second);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        first.emergencyContacts()[0] = Fixtures.emergencyContactArray(3)[2];
        assertEquals(Fixtures.employee(), first);
        assertEquals(Fixtures.employee(), second);
    }

    @Test
    void separateBuildersDoNotShareEmployeeContactStorage() {
        EmployeeInfo first = builder().emergencyContacts(Fixtures.emergencyContacts(1)).build();
        EmployeeInfo second = builder().emergencyContacts(Fixtures.emergencyContacts(3)).build();
        EmployeeInfo empty = builder().build();
        assertEquals(Fixtures.employee(1), first);
        assertEquals(Fixtures.employee(3), second);
        assertEquals(Fixtures.employee(0), empty);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 10, 100})
    void independentArraysHaveValueEqualityAndConsistentHashCodes(int count) {
        EmployeeInfo first = employeeWith(Fixtures.emergencyContactArray(count));
        EmployeeInfo second = employeeWith(Fixtures.emergencyContactArray(count));
        EmployeeInfo third = builder().emergencyContacts(Fixtures.emergencyContacts(count)).build();
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(second, third);
        assertEquals(first, third);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(second.hashCode(), third.hashCode());
    }

    @Test
    void differentElementsOfSameLengthChangeEquality() {
        EmergencyContact[] contacts = Fixtures.emergencyContactArray(2);
        contacts[1] = Fixtures.emergencyContactArray(3)[2];
        assertNotEquals(Fixtures.employee(), employeeWith(contacts));
    }

    @Test
    void rebuildingFromAccessorsPreservesRecordEquality() {
        EmployeeInfo original = Fixtures.employee();
        EmployeeInfo copy = new EmployeeInfo(original.employeeId(), original.employeeContact(),
                original.supervisorContact(), original.employmentInfo(), original.emergencyContacts());
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.toString(), copy.toString());
    }
}
