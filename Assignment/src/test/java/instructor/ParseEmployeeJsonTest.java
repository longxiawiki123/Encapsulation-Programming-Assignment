package instructor;

import com.google.gson.Gson;
import HRServices.Utilities.ParseEmployeeJson;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("instructor")
class ParseEmployeeJsonTest {
    @TempDir
    Path directory;

    private ParseEmployeeJson parserFor(String json) throws IOException {
        Path file = directory.resolve("employees.json");
        Files.writeString(file, json, StandardCharsets.UTF_8);
        return new ParseEmployeeJson(file.toString());
    }

    @Test
    void returnsConcreteHashMapsAndRetainsAllFieldsIncludingTheId() throws IOException {
        LinkedHashMap<String, String> entry = Fixtures.entry();
        HashMap<String, HashMap<String, String>> parsed =
                parserFor(new Gson().toJson(List.of(entry))).parse();
        assertInstanceOf(HashMap.class, parsed);
        assertEquals(Set.of("EMP0001"), parsed.keySet());
        assertInstanceOf(HashMap.class, parsed.get("EMP0001"));
        assertEquals(entry, parsed.get("EMP0001"));
        assertEquals(41, parsed.get("EMP0001").size());
    }

    @Test
    void acceptsAnEmptyArray() throws IOException {
        assertTrue(parserFor(" \n [ ] \t ").parse().isEmpty());
    }

    @Test
    void entriesHaveIndependentInnerMapsAndNoRequiredInputOrder() throws IOException {
        HashMap<String, HashMap<String, String>> parsed = parserFor("""
                [
                  {"note":"second", "employeeId":"EMP0002"},
                  {"employeeId":"EMP0001", "note":"first"}
                ]
                """).parse();
        assertEquals(Set.of("EMP0001", "EMP0002"), parsed.keySet());
        assertNotSame(parsed.get("EMP0001"), parsed.get("EMP0002"));
        parsed.get("EMP0001").put("note", "changed");
        assertEquals("second", parsed.get("EMP0002").get("note"));
    }

    @Test
    void preservesUnicodeWhitespaceEscapesAndLeadingZeroes() throws IOException {
        Map<String, String> entry = Map.of(
                "employeeId", "0007",
                "firstName", " Léa ",
                "lastName", "O'Neil",
                "street", "12 \"Oak\" Lane\\West\nSuite\t2",
                "zipCode", "04101-0123",
                "telephone", "+1 (207) 555-0131",
                "email", "Lea+work@example.com",
                "extra", "");
        HashMap<String, HashMap<String, String>> parsed =
                parserFor(new Gson().toJson(List.of(entry))).parse();
        assertEquals(entry, parsed.get("0007"));
    }

    @Test
    void keepsIdsExactlyAsWrittenAndDoesNotCoerceThemToNumbers() throws IOException {
        HashMap<String, HashMap<String, String>> parsed = parserFor("""
                [{"employeeId":"0001"}, {"employeeId":"1"}, {"employeeId":" A "},
                 {"employeeId":"a"}, {"employeeId":"A"}]
                """).parse();
        assertEquals(Set.of("0001", "1", " A ", "a", "A"), parsed.keySet());
        parsed.forEach((id, fields) -> assertEquals(id, fields.get("employeeId")));
    }

    @Test
    void acceptsAdditionalStringFieldsWithoutNeedingTheFullContactSchema() throws IOException {
        assertEquals(Map.of("employeeId", "EMP0001", "nickname", "AJ"),
                parserFor("""
                        [{"employeeId":"EMP0001", "nickname":"AJ"}]
                        """).parse().get("EMP0001"));
    }

    @Test
    void parsesPathsContainingSpacesAndUnicode() throws IOException {
        Path folder = Files.createDirectories(directory.resolve("employee files é"));
        Path file = folder.resolve("contact records.json");
        Files.writeString(file, "[{\"employeeId\":\"EMP0001\"}]", StandardCharsets.UTF_8);
        assertTrue(new ParseEmployeeJson(file.toString()).parse().containsKey("EMP0001"));
    }

    @Test
    void constructorStoresThePathAndParseReadsItLater() throws IOException {
        Path file = directory.resolve("created-later.json");
        ParseEmployeeJson parser = new ParseEmployeeJson(file.toString());
        Files.writeString(file, "[{\"employeeId\":\"EMP0001\"}]", StandardCharsets.UTF_8);
        assertEquals(1, parser.parse().size());
    }

    @Test
    void repeatedCallsReadFreshDataAndDoNotShareResults() throws IOException {
        ParseEmployeeJson parser = parserFor("[{\"employeeId\":\"EMP0001\"}]");
        HashMap<String, HashMap<String, String>> first = parser.parse();
        HashMap<String, HashMap<String, String>> second = parser.parse();
        assertNotSame(first, second);
        assertNotSame(first.get("EMP0001"), second.get("EMP0001"));
        first.get("EMP0001").put("extra", "changed");
        assertFalse(second.get("EMP0001").containsKey("extra"));
        Files.writeString(directory.resolve("employees.json"),
                "[{\"employeeId\":\"EMP0002\"}]", StandardCharsets.UTF_8);
        assertEquals(Set.of("EMP0002"), parser.parse().keySet());
    }

    @Test
    void reportsMissingFilesAsIoErrors() {
        ParseEmployeeJson parser = new ParseEmployeeJson(directory.resolve("missing.json").toString());
        assertThrows(IOException.class, parser::parse);
    }

    static Stream<String> invalidDocuments() {
        return Stream.of(
                "", " ", "null", "{}", "\"text\"", "42", "true",
                "[null]", "[false]", "[123]", "[\"text\"]", "[[]]",
                "[{}]", "[{\"employeeID\":\"EMP0001\"}]",
                "[{\"employeeId\":\"\"}]", "[{\"employeeId\":\"   \"}]",
                "[{\"employeeId\":\"\\t\\n\"}]", "[{\"employeeId\":null}]",
                "[{\"employeeId\":123}]", "[{\"employeeId\":true}]",
                "[{\"employeeId\":{}}]", "[{\"employeeId\":[]}]",
                "[{\"employeeId\":\"EMP0001\",\"zipCode\":4101}]",
                "[{\"employeeId\":\"EMP0001\",\"value\":1.25}]",
                "[{\"employeeId\":\"EMP0001\",\"value\":false}]",
                "[{\"employeeId\":\"EMP0001\",\"value\":null}]",
                "[{\"employeeId\":\"EMP0001\",\"contact\":{\"firstName\":\"Ava\"}}]",
                "[{\"employeeId\":\"EMP0001\",\"contacts\":[]}]",
                "[{\"employeeId\":\"EMP0001\"}, {\"employeeId\":\"EMP0001\"}]",
                "[{\"employeeId\":\"EMP0001\"}, {\"employeeId\":\"EMP0002\"},"
                        + " {\"employeeId\":\"EMP0001\"}]",
                "[{\"employeeId\":\"EMP0001\"}, {}]",
                "[", "[{", "[{\"employeeId\":\"EMP0001\"}",
                "[{\"employeeId\":\"EMP0001\",}]", "[{\"employeeId\":\"EMP0001\"},]",
                "[{employeeId:\"EMP0001\"}]", "[{'employeeId':'EMP0001'}]",
                "[{\"employeeId\" \"EMP0001\"}]", "[{\"employeeId\":\"EMP0001\"}"
                        + "{\"employeeId\":\"EMP0002\"}]",
                "[{\"employeeId\":\"EMP0001\",\"extra\":NaN}]",
                "/* comment */ []", "[] // comment", "[] []", "[] trailing",
                "[{\"employeeId\":\"bad\nid\"}]");
    }

    @ParameterizedTest
    @MethodSource("invalidDocuments")
    void rejectsMalformedNonFlatNonStringAndAmbiguousEmployeeEntries(String json)
            throws IOException {
        ParseEmployeeJson parser = parserFor(json);
        assertThrows(IllegalArgumentException.class, parser::parse);
    }

    @Test
    void invalidInputCanBeCorrectedAndParsedAgain() throws IOException {
        ParseEmployeeJson parser = parserFor("[{\"employeeId\":\"same\"},{\"employeeId\":\"same\"}]");
        assertThrows(IllegalArgumentException.class, parser::parse);
        Files.writeString(directory.resolve("employees.json"), "[]", StandardCharsets.UTF_8);
        assertTrue(parser.parse().isEmpty());
    }
}
