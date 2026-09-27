package instructor;

import HRServices.Enums.EmployeeDivision;
import HRServices.Enums.Relationship;
import HRServices.Enums.State;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class EnumTest {
    private static final String STATES = """
            AL|Alabama
            AK|Alaska
            AZ|Arizona
            AR|Arkansas
            CA|California
            CO|Colorado
            CT|Connecticut
            DE|Delaware
            FL|Florida
            GA|Georgia
            HI|Hawaii
            ID|Idaho
            IL|Illinois
            IN|Indiana
            IA|Iowa
            KS|Kansas
            KY|Kentucky
            LA|Louisiana
            ME|Maine
            MD|Maryland
            MA|Massachusetts
            MI|Michigan
            MN|Minnesota
            MS|Mississippi
            MO|Missouri
            MT|Montana
            NE|Nebraska
            NV|Nevada
            NH|New Hampshire
            NJ|New Jersey
            NM|New Mexico
            NY|New York
            NC|North Carolina
            ND|North Dakota
            OH|Ohio
            OK|Oklahoma
            OR|Oregon
            PA|Pennsylvania
            RI|Rhode Island
            SC|South Carolina
            SD|South Dakota
            TN|Tennessee
            TX|Texas
            UT|Utah
            VT|Vermont
            VA|Virginia
            WA|Washington
            WV|West Virginia
            WI|Wisconsin
            WY|Wyoming
            """;

    static Stream<String> states() {
        return STATES.lines().filter(line -> !line.isBlank());
    }

    @Test
    void containsExactlyTheFiftyStatesAndNoTerritories() {
        String[] expected = states().map(line -> line.split("\\|")[0]).sorted()
                .toArray(String[]::new);
        String[] actual = Arrays.stream(State.values()).map(State::name).sorted()
                .toArray(String[]::new);
        assertEquals(50, actual.length);
        assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("states")
    void eachStateHasTheCorrectDisplayNameAndDescription(String line) {
        String[] pair = line.split("\\|");
        State state = State.valueOf(pair[0]);
        assertEquals(pair[1], state.getName());
        assertEquals("The U.S. state of " + pair[1] + ".", state.getDescription());
        assertEquals(pair[1], state.toString());
    }

    @ParameterizedTest
    @EnumSource(State.class)
    void parsesEveryStateNameAndAbbreviationRegardlessOfCase(State state) {
        assertSame(state, State.fromString(state.name()));
        assertSame(state, State.fromString(" \t" + state.name().toLowerCase(Locale.ROOT) + "\n"));
        assertSame(state, State.fromString(state.getName()));
        assertSame(state, State.fromString(" " + state.getName().toUpperCase(Locale.ROOT) + " "));
        assertSame(state, State.fromString(state.getName().toLowerCase(Locale.ROOT)));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t\n", "DC", "District of Columbia", "PR",
            "Puerto Rico", "GU", "AS", "VI", "MP", "Ontario", "XX", "NewYork",
            "North  Carolina", "12", "California!"})
    void rejectsUnknownStates(String value) {
        assertThrows(IllegalArgumentException.class, () -> State.fromString(value));
    }

    @Test
    void hasExactlyTheRequestedRelationshipsAndDivisions() {
        assertArrayEquals(new String[]{"FRIEND", "SPOUSE", "PARTNER", "NEIGHBOR", "OTHER"},
                Arrays.stream(Relationship.values()).map(Relationship::name).toArray(String[]::new));
        assertArrayEquals(new String[]{"WAREHOUSE", "TRANSPORTATION", "SALES", "MARKETING",
                        "ENGINEERING", "MAINTENANCE"},
                Arrays.stream(EmployeeDivision.values()).map(EmployeeDivision::name)
                        .toArray(String[]::new));
    }

    @ParameterizedTest
    @CsvSource({
            "FRIEND, Friend, A personal friend.",
            "SPOUSE, Spouse, A husband or wife.",
            "PARTNER, Partner, A life partner.",
            "NEIGHBOR, Neighbor, A person who lives nearby.",
            "OTHER, Other, Another trusted emergency contact."
    })
    void relationshipsHaveMeaningfulMetadata(Relationship value, String name, String description) {
        assertEquals(name, value.getName());
        assertEquals(description, value.getDescription());
        assertEquals(name, value.toString());
    }

    @ParameterizedTest
    @EnumSource(Relationship.class)
    void parsesEveryRelationship(Relationship value) {
        assertSame(value, Relationship.fromString(value.name()));
        assertSame(value, Relationship.fromString(value.getName()));
        assertSame(value, Relationship.fromString(" \t"
                + value.name().toLowerCase(Locale.ROOT) + "\n"));
    }

    @ParameterizedTest
    @EnumSource(EmployeeDivision.class)
    void parsesEveryDivisionAndProvidesMetadata(EmployeeDivision value) {
        assertSame(value, EmployeeDivision.fromString(value.name()));
        assertSame(value, EmployeeDivision.fromString(value.getName()));
        assertSame(value, EmployeeDivision.fromString(" \t"
                + value.name().toLowerCase(Locale.ROOT) + "\n"));
        String expectedName = value.name().substring(0, 1)
                + value.name().substring(1).toLowerCase(Locale.ROOT);
        assertEquals(expectedName, value.getName());
        assertFalse(value.getDescription().isBlank());
        assertEquals(value.getName(), value.toString());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t\n", "Coworker", "Parent", "FRIENDS", "1", "Spouse!"})
    void rejectsUnknownRelationships(String value) {
        assertThrows(IllegalArgumentException.class, () -> Relationship.fromString(value));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t\n", "Finance", "Human Resources", "Salesperson", "1", "Sales!"})
    void rejectsUnknownDivisions(String value) {
        assertThrows(IllegalArgumentException.class, () -> EmployeeDivision.fromString(value));
    }
}
