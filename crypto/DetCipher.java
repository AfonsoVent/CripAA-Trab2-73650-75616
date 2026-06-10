package crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

// Responsable to take care of DET ciphers: AES/CTR
public class DetCipher {
    private static final byte[] FIXED_IV = new byte[16]; // 16 bytes, with only 0

    // Deterministic AES/CTR encryption
    public static byte[] encrypt(byte[] plaintext, SecretKey key) {
        if (plaintext == null || key == null) {
            throw new IllegalArgumentException("PlainText and key cannot be null input!");
        }

        try {
            // Get cipher
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            // Use fixed IV
            IvParameterSpec ivSpec = new IvParameterSpec(FIXED_IV);

            // Encrypts
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            return cipher.doFinal(plaintext);

        } catch (Exception e) {
            throw new RuntimeException("Error, AES deterministic encrypt: ", e);
        }
    }

    // Deterministic AES/CTR decryption
    public static byte[] decrypt(byte[] ciphertext, SecretKey key) {
        if (ciphertext == null || key == null) {
            throw new IllegalArgumentException("CipherText and key cannot be null input!");
        }

        try {
            // Get cipher
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            // Same fixed IV
            IvParameterSpec ivSpec = new IvParameterSpec(FIXED_IV);

            // Decrypts
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return cipher.doFinal(ciphertext);

        } catch (Exception e) {
            throw new RuntimeException("Error, AES deterministic decrypt: ", e);
        }
    }
}
