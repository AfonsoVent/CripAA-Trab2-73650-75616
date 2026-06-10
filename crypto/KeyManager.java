package crypto;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// Responsable to create all keys at startup
public class KeyManager {
    // Symmetrical Keys
    private SecretKey detKey;   // AES-CTR
    private SecretKey gcmKey;   // AES/GCM
    private SecretKey hmacKey;  // HMAC-SHA

    // Asymmetrical keys
    private PrivateKey ecdsaPrivateKey; // To sign
    private PublicKey ecdsaPublicKey;   // To verify sign

    // Parameters Paillier HOM-ADD
    private BigInteger paillierN;
    private BigInteger paillierLambda;

    // Parameters ElGamal HOM-MUL
    private BigInteger elGamalP; // Big (P)rimal
    private BigInteger elGamalG; // (G)enerator
    private BigInteger elGamalPub; // Public Key
    private BigInteger elGamalPriv; // Private Key

    // Tree mOPE
    private MOpeTree mOpeSalaryTree; // Tree for Salary
    private MOpeTree mOpeAgeTree; // Tree for Age

    public KeyManager() {
        try {
            // Create mOPE trees
            this.mOpeSalaryTree = new MOpeTree();
            this.mOpeAgeTree = new MOpeTree();

            // Create symmetric keys
            this.detKey = generateSymmetricKey("AES", 256);
            this.gcmKey = generateSymmetricKey("AES", 256);
            this.hmacKey = generateSymmetricKey("HmacSHA256", 256);
            
            // Generate ECDSA keys
            generateEcdsaKeys();

            // Generate ElGamal and Paillier keys
            generateElGamalKeys();
            generatePaillierKeys();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing KeyManager: ", e);
        }
    }

    // Creating Keys 
    // Create symmetric key
    private SecretKey generateSymmetricKey(String algorithm, int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(keySize, new SecureRandom());
        return keyGen.generateKey();
    }

    // Generate ECDSA key pair
    private void generateEcdsaKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        KeyPair pair = keyGen.generateKeyPair();

        this.ecdsaPrivateKey = pair.getPrivate();
        this.ecdsaPublicKey = pair.getPublic();
    }

    // Generate ElGamal keys
    private void generateElGamalKeys() {
        SecureRandom random = new SecureRandom();

        this.elGamalP = BigInteger.probablePrime(512, random);
        this.elGamalG = new BigInteger("2");
        this.elGamalPub = new BigInteger(256, random);
        this.elGamalPriv = elGamalG.modPow(elGamalPub, elGamalP);
    }

    // Create Paillier keys
    private void generatePaillierKeys() {
        SecureRandom random = new SecureRandom();

        // Generate two large (probable) primes
        BigInteger p = BigInteger.probablePrime(512, random);
        BigInteger q = BigInteger.probablePrime(512, random);

        // Public key n = p * q
        this.paillierN = p.multiply(q);

        // Compute (p-1)
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);
        // Compute (q-1)
        BigInteger qMinus1 = q.subtract(BigInteger.ONE);

        // lambda = Lower Comum Multiple(p-1, q-1):
        this.paillierLambda =
            pMinus1.multiply(qMinus1)
                .divide(pMinus1.gcd(qMinus1));
    }

    // Getters
    public SecretKey getDetKey() { return detKey; }
    public SecretKey getGcmKey() { return gcmKey; }
    public SecretKey getHmacKey() { return hmacKey; }
    public PrivateKey getEcdsaPrivateKey() { return ecdsaPrivateKey; }
    public PublicKey getEcdsaPublicKey() { return ecdsaPublicKey; }
    public BigInteger getPaillierN() { return paillierN; }
    public BigInteger getPaillierLambda() { return paillierLambda; }    
    public BigInteger getElGamalP() { return elGamalP; }
    public BigInteger getElGamalG() { return elGamalG; }
    public BigInteger getElGamalPriv() { return elGamalPriv; }
    public BigInteger getElGamalPub() { return elGamalPub; }
    public MOpeTree getMOpeSalaryTree() { return mOpeSalaryTree; }
    public MOpeTree getMOpeAgeTree() { return mOpeAgeTree; }
}
