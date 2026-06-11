package bootstrap;

import java.math.BigInteger;
import java.util.Base64;

import crypto.AesGcmCipher;
import crypto.DetCipher;
import crypto.EcdsaSigner;
import crypto.HmacSha;
import crypto.KeyManager;
import crypto.Paillier;
import crypto.ElGamal;
import crypto.ElGamalCiphertext;
import model.Employee;
import model.EncryptedRecord;

// Responsable to apply the down schema of cipher on each row
public class EncryptedRecordBuilder {
    private static final int SHA_SIZE = 256;

    public static EncryptedRecord build(Employee emp, KeyManager km) {
        EncryptedRecord record = new EncryptedRecord();

        // For Determinist encryption:
        // DET(EmployeeId)
        byte[] idBytes = String.valueOf(emp.getId()).getBytes();
        byte[] encId = DetCipher.encrypt(idBytes, km.getDetKey(), "employeeID");
        record.setIdDet(Base64.getEncoder().encodeToString(encId));
        // DET(FullName)
        byte[] nameBytes = emp.getFullName().getBytes();
        byte[] encName = DetCipher.encrypt(nameBytes, km.getDetKey(), "FullName");
        record.setNameDet(Base64.getEncoder().encodeToString(encName));
        // DET(DepartmentID)
        byte[] deptBytes = emp.getDepartment().getBytes();
        byte[] encDept = DetCipher.encrypt(deptBytes, km.getDetKey(), "DepartmentID");
        record.setDeptDet(Base64.getEncoder().encodeToString(encDept));
        
        // For mOPE:
        // mOPE(Salary)
        record.setSalaryOpe(km.getMOpeSalaryTree().getMetricFor(emp.getSalary()));
        // mOPE(age)
        record.setAgeOpe(km.getMOpeAgeTree().getMetricFor(emp.getAge()));
        
        // For Pailler
        BigInteger salaryBI = BigInteger.valueOf((long) emp.getSalary());

        // HOM-ADD(Salary):
        record.setSalarySum(Paillier.encrypt(salaryBI, km.getPaillierN()));
        
        // HOM-MUL(Salary):
        ElGamalCiphertext ct = ElGamal.encrypt(
            salaryBI,
            km.getElGamalP(),
            km.getElGamalG(),
            km.getElGamalPub()
        );
        record.setSalaryMulC1(ct.c1);
        record.setSalaryMulC2(ct.c2);
        
        // For non-operation: AES/GCM 
        byte[] jsonBytes = emp.toJsonString().getBytes();
        byte[] encBlock = AesGcmCipher.encrypt(jsonBytes, km.getGcmKey());
        record.setSecureEncBlock(Base64.getEncoder().encodeToString(encBlock));
        
        // Integrity and Authenticity
        // Convert to bytes
        byte[] contentToSign = record.getBytesForSigning();
        // Hmac(record)
        record.setHmac(HmacSha.compute(contentToSign, km.getHmacKey(), SHA_SIZE));
        // Sign(record)
        record.setSignature(EcdsaSigner.sign(contentToSign, km.getEcdsaPrivateKey()));
        
        // (record || hmac(record) || sign(record))
        return record;
    }
}
