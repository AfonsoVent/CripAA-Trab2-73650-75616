package crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

// Responsable to take care of DET ciphers: AES/CTR
public class DetCipher {
    // Creates a fixed IV from a column name.
    private static IvParameterSpec getIvForColumn(String columnName) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty to deterministic IV derivation.");
        }

        try {
            // Hash column name
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(columnName.getBytes(StandardCharsets.UTF_8));

            // Get first 16 bytes as IV
            byte[] iv = new byte[16];
            System.arraycopy(hash, 0, iv, 0, 16);

            return new IvParameterSpec(iv);
        } catch (Exception e) {
            throw new RuntimeException("Error, deriving IV for column: " + columnName, e);
        }
    }

    // Deterministic AES/CTR encryption
    public static byte[] encrypt(byte[] plaintext, SecretKey key, String columnName) {
        if (plaintext == null || key == null) {
            throw new IllegalArgumentException("PlainText and key cannot be null input!");
        }

        try {
            // Get cipher
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            // Use fixed IV
            IvParameterSpec ivSpec = getIvForColumn(columnName);

            // Encrypts
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            return cipher.doFinal(plaintext);

        } catch (Exception e) {
            throw new RuntimeException("Error, AES deterministic encrypt: ", e);
        }
    }

    // Deterministic AES/CTR decryption
    public static byte[] decrypt(byte[] ciphertext, SecretKey key, String columnName) {
        if (ciphertext == null || key == null) {
            throw new IllegalArgumentException("CipherText and key cannot be null input!");
        }

        try {
            // Get cipher
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            // Same fixed IV
            IvParameterSpec ivSpec = getIvForColumn(columnName);

            // Decrypts
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return cipher.doFinal(ciphertext);

        } catch (Exception e) {
            throw new RuntimeException("Error, AES deterministic decrypt: ", e);
        }
    }
}
