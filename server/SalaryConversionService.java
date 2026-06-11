package server;

import java.math.BigInteger;
import crypto.ElGamal;
import crypto.ElGamalCiphertext;
import model.EncryptedRecord;

// Responsable to applie the salary conversion on encrypted values
public class SalaryConversionService {
    // Convert encrypted EUR salary to USD
    public static ElGamalCiphertext convertEurToUsdEncrypted(
            EncryptedRecord record,
            BigInteger rateNumerator,
            BigInteger elGamalP,
            BigInteger p, 
            BigInteger g, 
            BigInteger y) {

        // Rebuild ElGamal ciphertext
        ElGamalCiphertext salaryCt = new ElGamalCiphertext(
            record.getSalaryMulC1(),
            record.getSalaryMulC2()
        );

        // Encrypt rate with a new random k
        ElGamalCiphertext rateCt = ElGamal.encrypt(rateNumerator, p, g, y);

        // Multiply by exchange rate
        return ElGamal.multiply(salaryCt, rateCt, p);
    }
}