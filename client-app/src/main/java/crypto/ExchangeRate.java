package crypto;

import java.io.Serializable;
import java.math.BigInteger;

// Responsable to handle the currency rate exchange
public class ExchangeRate implements Serializable {
    private static final long serialVersionUID = 1L;
    private final BigInteger numerator;
    private final BigInteger denominator;

    // Create rate from decimal
    public ExchangeRate(double rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Rate need to be positive");
        }

        long factor = 10000L; // Tolerates 4 decimals
        this.numerator = BigInteger.valueOf(Math.round(rate * factor));
        this.denominator = BigInteger.valueOf(factor);
    }

    // Create rate from fraction
    public ExchangeRate(long numerator, long denominator) {
        if (denominator <= 0) {
            throw new IllegalArgumentException("Denominator need to be positive");
        }

        this.numerator = BigInteger.valueOf(numerator);
        this.denominator = BigInteger.valueOf(denominator);
    }

    // Convert rate to Double
    public double rateAsDouble() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    // Formula: (salary * numerator) / denominator
    public BigInteger applyTo(BigInteger salaryEur) {
        return salaryEur.multiply(numerator)
            .add(denominator.divide(BigInteger.valueOf(2)))
            .divide(denominator);
    }

    // Getters
    public BigInteger getNumerator() { return numerator; }
    public BigInteger getDenominator() { return denominator; }    
}