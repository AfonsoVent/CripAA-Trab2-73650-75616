package crypto;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

// Responsable to take care of: sha-256 (or highter[384/512])
public class HmacSha {
    // Compute HMAC from data and SecretKey
    public static byte[] compute(byte[] data, SecretKey key, int bitLength) {
        if (data == null || key == null) {
            throw new IllegalArgumentException("Data and key cannot be null.");
        }

        String algorithm = getAlgorithmName(bitLength);

        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("HMAC error: " + algorithm, e);
        }
    }

    // Compute HMAC using raw key bytes
    public static byte[] compute(byte[] data, byte[] keyBytes, int bitLength) {
        if (keyBytes == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        String algorithm = getAlgorithmName(bitLength);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, algorithm);

        return compute(data, secretKey, bitLength);
    }

    // Get HMAC algorithm from bit size 
    private static String getAlgorithmName(int bitLength) {
        if (bitLength < 256) {
            throw new IllegalArgumentException("Min is 256 bits required!");
        }

        switch (bitLength) {
            case 256:
                return "HmacSHA256";
            case 384:
                return "HmacSHA384";
            case 512:
                return "HmacSHA512";
            default:
                throw new IllegalArgumentException("Invalid HMAC size; Valid sizes: 256, 384, 512");
        }
    }
}
