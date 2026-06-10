package crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class EcdsaSigner {
    // ECDSA signature algorithm
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    // Create digital signature
    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        if (data == null || privateKey == null) {
            throw new IllegalArgumentException("Data and PrivKey cannot be null!");
        }

        try {
            // Initialize ECDSA signing with private key
            Signature ecdsaSign = Signature.getInstance(SIGNATURE_ALGORITHM);
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(data);

            // Generate signature bytes
            return ecdsaSign.sign();
        } catch (Exception e) {
            throw new RuntimeException("Error, signing: ", e);
        }
    }

    // Verify digital signature
    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {
        if (data == null || signature == null || publicKey == null) {
            throw new IllegalArgumentException("Data, signature and pubKey cannot be null!");
        }

        try {
            // Initialize ECDSA verification with public key
            Signature ecdsaVerify = Signature.getInstance(SIGNATURE_ALGORITHM);
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);

            // Check if signature matches data
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Error, verify: ", e);
        }
    }
}
