package crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

// Responsable for the behaviour of Paillier 
public class Paillier {
    // Formula: c = (g^m * r^n) mod n^2
    // But with g = n + 1 => For safety reasons
    // New Formula: c = (1 + (m * n)) * r^n mod n^2
    public static BigInteger encrypt(BigInteger m, BigInteger n) {
        // nSquare = n^2
        BigInteger nSquare = n.multiply(n);
        
        // Create random gen
        SecureRandom sr = new SecureRandom();
        
        // Random r: 0<r<n and GreatestCommonDivisor(r, n) = 1
        BigInteger r;
        do {
            r = new BigInteger(n.bitLength(), sr);
        } while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);
        
        // Compute (1 + m * n) mod n^2
        BigInteger term1 = BigInteger.ONE.add(m.multiply(n)).mod(nSquare);
        
        // Compute r^n mod n^2
        BigInteger term2 = r.modPow(n, nSquare);
        
        // Final ciphertext
        return term1.multiply(term2).mod(nSquare);
    }

    // Op HOM-ADD
    public static BigInteger add(BigInteger c1, BigInteger c2, BigInteger n) {
        // nSquare = n^2
        BigInteger nSquare = n.multiply(n);
        
        // c_sum = (c1 * c2) mod nSquare 
        return c1.multiply(c2).mod(nSquare);
    }

    // Formula: m = L(c^lambda mod n^2) * mu mod n
    public static BigInteger decrypt(BigInteger c, BigInteger n, BigInteger lambda, BigInteger mu) {
        if (c == null || n == null || lambda == null || mu == null) {
            throw new IllegalArgumentException("Parameters cannot be null for Paillier decryption!");
        }

        // Compute n^2
        BigInteger nSquare = n.multiply(n);

        // c^lambda mod n^2
        BigInteger cToLambda = c.modPow(lambda, nSquare);

        // L(x) = (x - 1) / n
        BigInteger u = cToLambda.subtract(BigInteger.ONE).divide(n);

        // m = L(c^lambda mod n^2) * mu mod n
        return u.multiply(mu).mod(n);
    }
}