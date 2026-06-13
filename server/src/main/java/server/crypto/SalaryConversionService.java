package server.crypto;

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
            BigInteger elGamalP) {

        // Rebuild ElGamal ciphertext
        ElGamalCiphertext salaryCt = new ElGamalCiphertext(
            record.getSalaryMulC1(),
            record.getSalaryMulC2()
        );

        // Multiply by exchange rate
        return ElGamal.multiplyByScalar(salaryCt, rateNumerator, elGamalP);
    }
}