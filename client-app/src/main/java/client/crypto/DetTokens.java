package client.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import crypto.DetCipher;
import crypto.KeyManager;

// Gera tokens DET para pesquisas
public final class DetTokens {
    private DetTokens() {}

    public static final String COL_EMPLOYEE_ID = "employeeID";
    public static final String COL_FULL_NAME = "FullName";
    public static final String COL_DEPARTMENT = "DepartmentID";
    public static final String COL_BONUS = "BonusEligibiity";

    public static String employeeId(KeyManager km, String employeeId) {
        return encrypt(km, employeeId, COL_EMPLOYEE_ID);
    }

    public static String fullName(KeyManager km, String fullName) {
        return encrypt(km, fullName, COL_FULL_NAME);
    }

    public static String department(KeyManager km, String departmentId) {
        return encrypt(km, departmentId, COL_DEPARTMENT);
    }

    public static String bonusEligibleFlag(KeyManager km, boolean eligible) {
        String value = eligible ? "TRUE" : "FALSE";
        return encrypt(km, value, COL_BONUS);
    }

    public static String bonusEligibleTrue(KeyManager km) {
        return bonusEligibleFlag(km, true);
    }

    private static String encrypt(KeyManager km, String plaintext, String column) {
        byte[] enc = DetCipher.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), km.getDetKey(), column);
        return Base64.getEncoder().encodeToString(enc);
    }
}
