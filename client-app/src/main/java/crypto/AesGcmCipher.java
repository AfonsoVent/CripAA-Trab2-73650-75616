package crypto;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

// Responsable to take care of AES/GCM
public class AesGcmCipher {
    // AES/GCM parameters
    private static final int IV_SIZE_BYTES = 12;
    private static final int TAG_BIT_LENGTH = 128;

    // Encrypt with AES/GCM
    public static byte[] encrypt(byte[] plaintext, SecretKey key) {
        if (plaintext == null || key == null) {
            throw new IllegalArgumentException("Can't have null text and null key");
        }

        try {
            // Generate random IV
            byte[] iv = new byte[IV_SIZE_BYTES];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);

            // Initialization AES/GCM cipher
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            // Encrypt data
            byte[] ciphertextWithTag = cipher.doFinal(plaintext);

            // Buffer(IV || ciphertextWithTag)
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertextWithTag.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertextWithTag);

            return byteBuffer.array();
        } catch (Exception e) {
            throw new RuntimeException("Error AES encrypt: ", e);
        }
    }

    // Decrypt with AES/GCM
    public static byte[] decrypt(byte[] ivAndCiphertext, SecretKey key) {
        if (ivAndCiphertext == null || key == null) {
            throw new IllegalArgumentException("Can't have cipher and key null");
        }
        if (ivAndCiphertext.length < IV_SIZE_BYTES) {
            throw new IllegalArgumentException("Invalid data");
        }

        try {
            // Get IV and ciphertext
            ByteBuffer byteBuffer = ByteBuffer.wrap(ivAndCiphertext);
            byte[] iv = new byte[IV_SIZE_BYTES];
            byteBuffer.get(iv);
            byte[] ciphertextWithTag = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertextWithTag);

            // Initialization AES/GCM decrypt
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            // Decrypt and verify integrity
            return cipher.doFinal(ciphertextWithTag);
        } catch (Exception e) {
            throw new RuntimeException("Error AES decrypt: ", e);
        }
    }
}
