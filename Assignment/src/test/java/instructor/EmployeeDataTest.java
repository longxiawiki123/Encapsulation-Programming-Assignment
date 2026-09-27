package instructor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import HRServices.Enums.EmployeeDivision;
import HRServices.Enums.EmploymentType;
import HRServices.Enums.WorkLocation;
import HRServices.Utilities.ParseEmployeeJson;
import HRServices.Enums.Relationship;
import HRServices.Enums.State;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class EmployeeDataTest {
    private static final Path DATA = Path.of("..", "employee_records.json");
    private static final String[] CONTACT_FIELDS =
            {"FirstName", "LastName", "Street", "City", "State", "ZipCode", "Telephone", "Email"};

    static JsonArray data() throws IOException {
        return JsonParser.parseString(Files.readString(DATA, StandardCharsets.UTF_8)).getAsJsonArray();
    }

    static Stream<Arguments> entries() throws IOException {
        return data().asList().stream().map(JsonElement::getAsJsonObject)
                .map(entry -> Arguments.of(entry.get("employeeId").getAsString(), entry));
    }

    private static List<String> prefixes(int count) {
        List<String> result = new ArrayList<>(List.of("employee", "supervisor"));
        for (int number = 1; number <= count; number++) {
            result.add("emergencyContact" + number);
        }
        return result;
    }

    private static Set<String> expectedKeys(int count) {
        Set<String> keys = new HashSet<>(Set.of("employeeId", "employeeDivision",
                "jobTitle", "hireDate", "employmentType", "workLocation", "emergencyContactCount"));
        for (int number = 1; number <= count; number++) {
            keys.add("emergencyContact" + number + "Relation");
        }
        for (String prefix : prefixes(count)) {
            for (String field : CONTACT_FIELDS) {
                keys.add(prefix + field);
            }
        }
        return keys;
    }

    @Test
    void containsExactlyOneHundredUniqueEmployeeIds() throws IOException {
        JsonArray source = data();
        HashMap<String, HashMap<String, String>> parsed = new ParseEmployeeJson(DATA.toString()).parse();
        Set<String> expectedIds = IntStream.rangeClosed(1, 100)
                .mapToObj(id -> "EMP%04d".formatted(id)).collect(Collectors.toSet());
        assertEquals(100, source.size());
        assertEquals(100, parsed.size());
        assertEquals(expectedIds, parsed.keySet());
        Set<String> employeeEmails = new HashSet<>();
        parsed.values().forEach(fields -> employeeEmails.add(fields.get("employeeEmail")));
        assertEquals(100, employeeEmails.size());
    }

    @ParameterizedTest(name = "{0}: full flat employee entry")
    @MethodSource("entries")
    void everyEntryHasExactlyTheFlatFieldsForItsCountAndSafeFictionalContacts(String id, JsonObject entry)
            throws IOException {
        int count = Integer.parseInt(entry.get("emergencyContactCount").getAsString());
        assertTrue(count >= 1 && count <= 3);
        assertEquals(expectedKeys(count), entry.keySet());
        assertEquals(23 + 9 * count, entry.size());
        HashMap<String, String> parsed = new ParseEmployeeJson(DATA.toString()).parse().get(id);
        for (Map.Entry<String, JsonElement> field : entry.entrySet()) {
            assertTrue(field.getValue().isJsonPrimitive(), field.getKey());
            assertTrue(field.getValue().getAsJsonPrimitive().isString(), field.getKey());
            assertFalse(field.getValue().getAsString().isBlank(), field.getKey());
            assertEquals(field.getValue().getAsString(), parsed.get(field.getKey()), field.getKey());
        }
        assertNotNull(EmployeeDivision.fromString(parsed.get("employeeDivision")));
        assertNotNull(EmploymentType.fromString(parsed.get("employmentType")));
        assertNotNull(WorkLocation.fromString(parsed.get("workLocation")));
        LocalDate hireDate = LocalDate.parse(parsed.get("hireDate"));
        assertTrue(hireDate.isAfter(LocalDate.of(2015, 12, 31)));
        assertTrue(hireDate.isBefore(LocalDate.of(2026, 1, 1)));
        for (int number = 1; number <= count; number++) {
            assertNotNull(Relationship.fromString(parsed.get("emergencyContact" + number + "Relation")));
        }
        for (String prefix : prefixes(count)) {
            assertNotNull(State.fromString(parsed.get(prefix + "State")));
            assertTrue(parsed.get(prefix + "ZipCode").matches("\\d{5}"));
            assertTrue(parsed.get(prefix + "Telephone").matches("[2-9]\\d{2}-555-01\\d{2}"));
            assertTrue(parsed.get(prefix + "Email").matches("[^\\s@]+@example\\.com"));
            assertTrue(parsed.get(prefix + "Street").matches("\\d+ .+"));
        }
    }

    @Test
    void sampleExercisesEveryEnumVariableContactCountsAndLeadingZeroZipCodes() throws IOException {
        EnumSet<State> states = EnumSet.noneOf(State.class);
        EnumSet<EmployeeDivision> divisions = EnumSet.noneOf(EmployeeDivision.class);
        EnumSet<Relationship> relationships = EnumSet.noneOf(Relationship.class);
        EnumSet<EmploymentType> types = EnumSet.noneOf(EmploymentType.class);
        EnumSet<WorkLocation> locations = EnumSet.noneOf(WorkLocation.class);
        Set<Integer> counts = new HashSet<>();
        boolean hasLeadingZero = false;
        for (HashMap<String, String> entry : new ParseEmployeeJson(DATA.toString()).parse().values()) {
            states.add(State.fromString(entry.get("employeeState")));
            divisions.add(EmployeeDivision.fromString(entry.get("employeeDivision")));
            types.add(EmploymentType.fromString(entry.get("employmentType")));
            locations.add(WorkLocation.fromString(entry.get("workLocation")));
            int count = Integer.parseInt(entry.get("emergencyContactCount"));
            counts.add(count);
            for (int number = 1; number <= count; number++) {
                relationships.add(Relationship.fromString(entry.get("emergencyContact" + number + "Relation")));
            }
            hasLeadingZero |= entry.get("employeeZipCode").startsWith("0");
        }
        assertEquals(EnumSet.allOf(State.class), states);
        assertEquals(EnumSet.allOf(EmployeeDivision.class), divisions);
        assertEquals(EnumSet.allOf(Relationship.class), relationships);
        assertEquals(EnumSet.allOf(EmploymentType.class), types);
        assertEquals(EnumSet.allOf(WorkLocation.class), locations);
        assertEquals(Set.of(1, 2, 3), counts);
        assertTrue(hasLeadingZero);
    }

    @Test
    void everyDivisionUsesAConsistentSupervisor() throws IOException {
        Map<String, Map<String, String>> supervisors = new HashMap<>();
        for (HashMap<String, String> entry : new ParseEmployeeJson(DATA.toString()).parse().values()) {
            Map<String, String> supervisor = Arrays.stream(CONTACT_FIELDS).collect(Collectors.toMap(
                    field -> field, field -> entry.get("supervisor" + field)));
            Map<String, String> previous = supervisors.putIfAbsent(entry.get("employeeDivision"), supervisor);
            if (previous != null) {
                assertEquals(previous, supervisor);
            }
        }
        assertEquals(6, supervisors.size());
    }
}
