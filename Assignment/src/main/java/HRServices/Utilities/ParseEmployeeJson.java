package HRServices.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/*
This FILE IS COMPLETELY FINE!  DO NOT ALTER
 */

/** Reads a flat JSON array into maps; record construction belongs in Main. */
public class ParseEmployeeJson {
    private final Path filePath;

    public ParseEmployeeJson(String filePath) {
        this.filePath = Path.of(filePath);
    }

    public HashMap<String, HashMap<String, String>> parse() throws IOException {
        Gson gson = new GsonBuilder().setStrictness(Strictness.STRICT).create();
        HashMap<String, HashMap<String, String>> employees = new HashMap<>();

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            JsonElement document = gson.fromJson(reader, JsonElement.class);
            if (document == null || !document.isJsonArray()) {
                throw new IllegalArgumentException("Expected a JSON array of employee entries.");
            }

            for (JsonElement entry : document.getAsJsonArray()) {
                if (!entry.isJsonObject()) {
                    throw new IllegalArgumentException("Every employee entry must be an object.");
                }

                HashMap<String, String> fields = new HashMap<>();
                for (Map.Entry<String, JsonElement> field : entry.getAsJsonObject().entrySet()) {
                    JsonElement value = field.getValue();
                    if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
                        throw new IllegalArgumentException(
                                "Every field must be a string: " + field.getKey());
                    }
                    fields.put(field.getKey(), value.getAsString());
                }

                String employeeId = fields.get("employeeId");
                if (employeeId == null || employeeId.isBlank()) {
                    throw new IllegalArgumentException("Every entry needs a nonblank employeeId.");
                }
                if (employees.containsKey(employeeId)) {
                    throw new IllegalArgumentException("Duplicate employeeId: " + employeeId);
                }
                employees.put(employeeId, fields);
            }
        } catch (JsonParseException exception) {
            throw new IllegalArgumentException("Invalid employee JSON: " + filePath, exception);
        }

        return employees;
    }
}
