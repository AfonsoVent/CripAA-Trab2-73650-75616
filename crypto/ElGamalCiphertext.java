package crypto;

import java.math.BigInteger;

public class ElGamalCiphertext {
    public final BigInteger c1;
    public final BigInteger c2;

    public ElGamalCiphertext(BigInteger c1, BigInteger c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    // Convert ciphertext to String (easy DB storage)
    @Override
    public String toString() {
        return c1.toString() + "," + c2.toString();
    }

    // Rebuilds ciphertext from DB
    public static ElGamalCiphertext fromString(String str) {
        if (str == null || !str.contains(",")) {
            throw new IllegalArgumentException("Invalid ElGamal ciphertext format.");
        }

        String[] parts = str.split(",");

        return new ElGamalCiphertext(
            new BigInteger(parts[0]), 
            new BigInteger(parts[1])
        );
    }
}