package crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

// Responsable for the behaviour of ElGamal
public class ElGamal {
    // Formula: c1 = g^k mod p 
    //          c2 = (m * y^k) mod p
    public static ElGamalCiphertext encrypt(BigInteger m, BigInteger p, BigInteger g, BigInteger y) {
        // Create random gen
        SecureRandom sr = new SecureRandom();

        // p - 1
        BigInteger pMinusOne = p.subtract(BigInteger.ONE);
        
        // Random k: 2<r<(p-2)
        BigInteger k;
        do {
            k = new BigInteger(p.bitLength(), sr);
        } while (k.compareTo(BigInteger.ONE) <= 0 || k.compareTo(pMinusOne) >= 0);

        // c1 = g^k mod p 
        BigInteger c1 = g.modPow(k, p);
        // c2 = (m * y^k) mod p
        BigInteger c2 = m.multiply(y.modPow(k, p)).mod(p);
        
        return new ElGamalCiphertext(c1, c2);
    }

    // Fórmula: (c1 * c1') mod p 
    //          (c2 * c2') mod p
    public static ElGamalCiphertext multiply(ElGamalCiphertext ctx1, ElGamalCiphertext ctx2, BigInteger p) {
        // (c1 * c1') mod p
        BigInteger resC1 = ctx1.c1.multiply(ctx2.c1).mod(p);
        
        // (c2 * c2') mod p
        BigInteger resC2 = ctx1.c2.multiply(ctx2.c2).mod(p);
        
        return new ElGamalCiphertext(resC1, resC2);
    }
}