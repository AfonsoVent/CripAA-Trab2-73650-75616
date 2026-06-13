package client.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Utilitário JSON simples (espelha server.connection.JsonUtils)
public final class JsonHelper {
    private JsonHelper() {}

    public static String field(String field, String value) {
        return "\"" + field + "\":\"" + escape(value) + "\"";
    }

    public static String field(String field, boolean value) {
        return "\"" + field + "\":" + value;
    }

    public static String field(String field, long value) {
        return "\"" + field + "\":" + value;
    }

    public static String object(String... keyValuePairs) {
        return "{" + String.join(",", keyValuePairs) + "}";
    }

    public static String array(String... values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append('"').append(escape(values[i])).append('"');
        }
        sb.append(']');
        return sb.toString();
    }

    public static String extractField(String json, String field) {
        String target = "\"" + field + "\":";
        int index = json.indexOf(target);
        if (index == -1) throw new IllegalArgumentException("Missing field: " + field);
        int start = index + target.length();
        String remainder = json.substring(start).trim();
        if (remainder.startsWith("\"")) {
            int end = remainder.indexOf('"', 1);
            return remainder.substring(1, end);
        }
        return remainder.split("[,}]")[0].trim();
    }

    public static List<String> extractArray(String json, String field) {
        String target = "\"" + field + "\":[";
        int index = json.indexOf(target);
        if (index == -1) throw new IllegalArgumentException("Missing array: " + field);
        int start = index + target.length();
        int end = json.indexOf("]", start);
        String content = json.substring(start, end).replace("\"", "").trim();
        if (content.isEmpty()) return Collections.emptyList();
        return Arrays.asList(content.split("\\s*,\\s*"));
    }

    // Separa objetos JSON de um array [{...},{...}]
    public static List<String> splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        String trimmed = json.trim();
        if (trimmed.equals("[]")) return objects;

        int startArray = trimmed.indexOf('[');
        int endArray = trimmed.lastIndexOf(']');
        if (startArray < 0 || endArray < 0) {
            if (trimmed.startsWith("{")) {
                objects.add(trimmed);
                return objects;
            }
            throw new IllegalArgumentException("JSON invalid");
        }

        int depth = 0;
        int start = -1;
        for (int i = startArray + 1; i < endArray; i++) {
            char c = trimmed.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(trimmed.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
