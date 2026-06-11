package model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class EncryptedRecord {
    // For Determinist encryption:
    private String idDet;
    private String nameDet;
    private String deptDet;
    private String bonusDet;

    // For mOPE:
    private long salaryOpe;
    private long BirthDateOpe;

    // For HOM-...
    private BigInteger salarySum; // ...-SUM (Paillier)
    private BigInteger salaryMulC1; // ...-MUL (ElGamal) value 1
    private BigInteger salaryMulC2; // ...-MUL (ElGamal) value 2

    // For non-operation:
    private String secureEncBlock; // (AES/GCM)

    // Integrity and Authenticity
    private byte[] hmac;
    private byte[] signature;

    public EncryptedRecord() {}

    // Converts (encrypted data) to bytes
    public byte[] getBytesForSigning() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Write string fields
            if (idDet != null) baos.write(idDet.getBytes(StandardCharsets.UTF_8));
            if (nameDet != null) baos.write(nameDet.getBytes(StandardCharsets.UTF_8));
            if (deptDet != null) baos.write(deptDet.getBytes(StandardCharsets.UTF_8));
            if (bonusDet != null) baos.write(bonusDet.getBytes(StandardCharsets.UTF_8));
            
            // Write OPE values
            baos.write(longToBytes(salaryOpe));
            baos.write(longToBytes(BirthDateOpe));
            
            // Write homomorphic values
            if (salarySum != null) baos.write(salarySum.toByteArray());
            if (salaryMulC1 != null) baos.write(salaryMulC1.toByteArray());
            if (salaryMulC2 != null) baos.write(salaryMulC2.toByteArray());
            
            // Write AES/GCM
            if (secureEncBlock != null) baos.write(secureEncBlock.getBytes(StandardCharsets.UTF_8));
            
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error creating the byte for the signature: ", e);
        }
    }

    // Convert long to byte
    private byte[] longToBytes(long val) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (val & 0xFF);
            val >>= 8;
        }
        return bytes;
    }

    // Getters
    public String getIdDet() { return idDet; }
    public String getNameDet() { return nameDet; }
    public String getDeptDet() { return deptDet; }
    public String getBonusDet() { return bonusDet; }
    public long getSalaryOpe() { return salaryOpe; }
    public long getBirthDateOpe() { return BirthDateOpe; }
    public BigInteger getSalarySum() { return salarySum; }
    public BigInteger getSalaryMulC1() { return salaryMulC1; }
    public BigInteger getSalaryMulC2() { return salaryMulC2; }
    public String getSecureEncBlock() { return secureEncBlock; }
    public byte[] getHmac() { return hmac; }
    public byte[] getSignature() { return signature; } 

    // Setters
    public void setIdDet(String idDet) { this.idDet = idDet; }
    public void setNameDet(String nameDet) { this.nameDet = nameDet; }
    public void setDeptDet(String deptDet) { this.deptDet = deptDet; }
    public void setBonusDet(String bonusDet) { this.bonusDet = bonusDet; }
    public void setSalaryOpe(long salaryOpe) { this.salaryOpe = salaryOpe; }
    public void setBirthDateOpe(long BirthDateOpe) { this.BirthDateOpe = BirthDateOpe; }
    public void setSalarySum(BigInteger salarySum) { this.salarySum = salarySum; }
    public void setSalaryMulC1(BigInteger salaryMulC1) { this.salaryMulC1 = salaryMulC1; }
    public void setSalaryMulC2(BigInteger salaryMulC2) { this.salaryMulC2 = salaryMulC2; }
    public void setSecureEncBlock(String secureEncBlock) { this.secureEncBlock = secureEncBlock; }
    public void setHmac(byte[] hmac) { this.hmac = hmac; }
    public void setSignature(byte[] signature) { this.signature = signature; }
}