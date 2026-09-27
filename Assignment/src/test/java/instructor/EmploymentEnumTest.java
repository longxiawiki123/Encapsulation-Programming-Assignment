package instructor;

import HRServices.Records.Address;
import HRServices.Enums.EmploymentType;
import HRServices.Records.LocationInfo;
import HRServices.Enums.State;
import HRServices.Enums.WorkLocation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class EmploymentEnumTest {
    static Stream<Arguments> locations() {
        return Stream.of(
                Arguments.of(WorkLocation.LEXINGTON_OFFICE, "Lexington Office",
                        "Administrative and facilities office.", "Building A", "201",
                        "100 Bluegrass Meadow Drive", "Lexington", State.KY, "40507"),
                Arguments.of(WorkLocation.CHICAGO_OFFICE, "Chicago Office",
                        "Regional sales office.", "Building C", "310",
                        "240 Lakeshore Grove Avenue", "Chicago", State.IL, "60601"),
                Arguments.of(WorkLocation.DENVER_OFFICE, "Denver Office",
                        "Engineering and product office.", "Building D", "405",
                        "360 Aspen Meadow Road", "Denver", State.CO, "80202"),
                Arguments.of(WorkLocation.PHOENIX_WAREHOUSE, "Phoenix Warehouse",
                        "Inventory and order fulfillment center.", "Warehouse P", "12",
                        "480 Desert Willow Way", "Phoenix", State.AZ, "85004"),
                Arguments.of(WorkLocation.ATLANTA_DEPOT, "Atlanta Depot",
                        "Transportation planning and dispatch depot.", "Depot T", "108",
                        "520 Peach Grove Lane", "Atlanta", State.GA, "30303"),
                Arguments.of(WorkLocation.SEATTLE_OFFICE, "Seattle Office",
                        "Marketing and communications office.", "Building S", "220",
                        "640 Evergreen Meadow Street", "Seattle", State.WA, "98101"));
    }

    static Stream<Arguments> types() {
        return Stream.of(
                Arguments.of(EmploymentType.FULL_TIME, "Full-time",
                        "An employee who works a full-time schedule."),
                Arguments.of(EmploymentType.PART_TIME, "Part-time",
                        "An employee who works a part-time schedule."),
                Arguments.of(EmploymentType.SEASONAL, "Seasonal",
                        "An employee hired for a particular season."),
                Arguments.of(EmploymentType.CONTRACTOR, "Contractor",
                        "A worker engaged for a defined contract."));
    }

    @Test
    void exactWorkLocationsAreAvailable() {
        assertEquals(Set.of("LEXINGTON_OFFICE", "CHICAGO_OFFICE", "DENVER_OFFICE",
                        "PHOENIX_WAREHOUSE", "ATLANTA_DEPOT", "SEATTLE_OFFICE"),
                Arrays.stream(WorkLocation.values()).map(Enum::name).collect(Collectors.toSet()));
    }

    @Test
    void exactEmploymentTypesAreAvailable() {
        assertArrayEquals(new EmploymentType[]{EmploymentType.FULL_TIME, EmploymentType.PART_TIME,
                EmploymentType.SEASONAL, EmploymentType.CONTRACTOR}, EmploymentType.values());
    }

    @ParameterizedTest
    @MethodSource("locations")
    void locationsOwnTheirExpectedImmutableDetails(WorkLocation location, String name,
            String description, String building, String room, String street, String city,
            State state, String zip) {
        assertEquals(name, location.getName());
        assertEquals(description, location.getDescription());
        assertEquals(name, location.toString());
        LocationInfo info = location.getLocationInfo();
        assertEquals(new LocationInfo(building, room, new Address(street, city, state, zip)), info);
        assertSame(info, location.getLocationInfo());
        assertTrue(info.getClass().isRecord());
        assertTrue(info.address().getClass().isRecord());
        // Building a different value does not change the enum's shared immutable record.
        new LocationInfo.Builder().building("Other building").roomNumber("999")
                .address(info.address()).build();
        assertEquals(building, location.getLocationInfo().building());
    }

    @ParameterizedTest
    @MethodSource("locations")
    void workLocationsAcceptConstantOrDisplayNamesWithCaseAndWhitespace(WorkLocation location,
            String name, String description, String building, String room, String street,
            String city, State state, String zip) {
        assertSame(location, WorkLocation.fromString(location.name()));
        assertSame(location, WorkLocation.fromString(" \t" + location.name().toLowerCase(Locale.ROOT) + "\n"));
        assertSame(location, WorkLocation.fromString(name));
        assertSame(location, WorkLocation.fromString(" " + name.toUpperCase(Locale.ROOT) + " "));
    }

    @ParameterizedTest
    @MethodSource("types")
    void employmentTypesHaveMetadataAndAcceptBothNames(EmploymentType type, String name,
                                                       String description) {
        assertEquals(name, type.getName());
        assertEquals(description, type.getDescription());
        assertEquals(name, type.toString());
        assertSame(type, EmploymentType.fromString(type.name()));
        assertSame(type, EmploymentType.fromString(" \t" + type.name().toLowerCase(Locale.ROOT) + "\n"));
        assertSame(type, EmploymentType.fromString(name));
        assertSame(type, EmploymentType.fromString(" " + name.toUpperCase(Locale.ROOT) + " "));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t\n", "remote", "DENVER", "Denver  Office", "123",
            "Denver Office extra", "OFFICE_DENVER"})
    void workLocationsRejectUnknownNonNullText(String text) {
        assertThrows(IllegalArgumentException.class, () -> WorkLocation.fromString(text));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t\n", "intern", "Full time", "Fulltime", "FULL_TIME extra", "123"})
    void employmentTypesRejectUnknownNonNullText(String text) {
        assertThrows(IllegalArgumentException.class, () -> EmploymentType.fromString(text));
    }
}
