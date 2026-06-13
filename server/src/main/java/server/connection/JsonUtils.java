package server.connection;

import java.util.*;
import model.EncryptedRecord;

// Responsable to handle JSON (converts/extracts)
public class JsonUtils {
    // Returns the field value or a default value if not found
    public static String extractFieldOrDefault(String json, String field, String defaultValue) {
        try {
            return extractField(json, field);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
    
    // Get a single field value of a JSON string
    public static String extractField(String json, String field) {
        String target = "\"" + field + "\":";
        int index = json.indexOf(target);
        if (index == -1) throw new IllegalArgumentException("Missing field: " + field);
        int start = index + target.length();
        String remainder = json.substring(start).trim();
        if (remainder.startsWith("\"")) {
            return remainder.split("\"")[1];
        } else {
            return remainder.split("[,}]")[0].trim();
        }
    }

    // Extracts an array of a JSON field
    public static List<String> extractArray(String json, String field) {
        String target = "\"" + field + "\":[";
        int index = json.indexOf(target);
        if (index == -1) throw new IllegalArgumentException("Missing array field: " + field);
        int start = index + target.length();
        int end = json.indexOf("]", start);
        if (end == -1) throw new IllegalArgumentException("Malformed JSON array");
        String arrayContent = json.substring(start, end).replace("\"", "").trim();
        if (arrayContent.isEmpty()) return Collections.emptyList();
        return Arrays.asList(arrayContent.split(","));
    }

    // Converts EncryptedRecord to JSON
    public static String recordToJson(EncryptedRecord r) {
        return "{"
            + "\"idDet\":\"" + escape(r.getIdDet()) + "\","
            + "\"fullNameDet\":\"" + escape(r.getNameDet()) + "\","
            + "\"deptDet\":\"" + escape(r.getDeptDet()) + "\","
            + "\"bonusDet\":\"" + escape(r.getBonusDet()) + "\","
            + "\"salaryOpe\":" + r.getSalaryOpe() + ","
            + "\"birthDateOpe\":" + r.getBirthDateOpe() + ","
            + "\"salarySum\":\"" + safe(r.getSalarySum()) + "\","
            + "\"salaryMulC1\":\"" + safe(r.getSalaryMulC1()) + "\","
            + "\"salaryMulC2\":\"" + safe(r.getSalaryMulC2()) + "\","
            + "\"secureEncBlock\":\"" + escape(r.getSecureEncBlock()) + "\","
            + "\"hmac\":\"" + Base64.getEncoder().encodeToString(r.getHmac()) + "\","
            + "\"signature\":\"" + Base64.getEncoder().encodeToString(r.getSignature()) + "\""
            + "}";
    }

    // Convert special JSON characters to String
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Returns an empty string if the value is null
    private static String safe(Object value) {
        return value != null ? value.toString() : "";
    }

    // Converts list<EncryptedRecord>  to JSON
    public static String recordListToJson(List<EncryptedRecord> list) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (EncryptedRecord r : list) { sj.add(recordToJson(r)); }
        return sj.toString();
    }
}