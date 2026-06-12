package server;

import java.util.*;
import model.EncryptedRecord;

// Responsable to handle JSON (converts/extracts)
public class JsonUtils {
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
            + "\"idDet\":\"" + r.getIdDet() + "\","
            + "\"fullNameDet\":\"" + r.getNameDet() + "\","
            + "\"deptDet\":\"" + r.getDeptDet() + "\","
            + "\"bonusDet\":\"" + r.getBonusDet() + "\","
            + "\"salaryOpe\":" + r.getSalaryOpe() + ","
            + "\"birthDateOpe\":" + r.getBirthDateOpe() + ","
            + "\"salarySum\":\"" + (r.getSalarySum() != null ? r.getSalarySum().toString() : "") + "\","
            + "\"salaryMulC1\":\"" + (r.getSalaryMulC1() != null ? r.getSalaryMulC1().toString() : "") + "\","
            + "\"salaryMulC2\":\"" + (r.getSalaryMulC2() != null ? r.getSalaryMulC2().toString() : "") + "\","
            + "\"secureEncBlock\":\"" + r.getSecureEncBlock() + "\""
            + "}";
    }

    // Converts list<EncryptedRecord>  to JSON
    public static String recordListToJson(List<EncryptedRecord> list) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (EncryptedRecord r : list) { sj.add(recordToJson(r)); }
        return sj.toString();
    }
}