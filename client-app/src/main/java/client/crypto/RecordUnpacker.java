package client.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import client.connection.JsonHelper;
import crypto.AesGcmCipher;
import crypto.EcdsaSigner;
import crypto.HmacSha;
import crypto.KeyManager;
import model.EncryptedRecord;

// Desencripta e valida registos devolvidos pelo servidor
public final class RecordUnpacker {
    private static final int HMAC_BITS = 256;

    private RecordUnpacker() {}

    // Converte JSON do servidor → registo + plaintext JSON do colaborador
    public static String decryptEmployeeJson(String recordJson, KeyManager km) {
        EncryptedRecord record = parseRecord(recordJson);
        verifyIntegrity(record, km);
        return decryptSecureBlock(record, km);
    }
    //imprime o registro decriptado
    public static void printDecryptedRecord(String recordJson, KeyManager km) {
        System.out.println(decryptEmployeeJson(recordJson, km));
    }
    //imprime a lista de registros decriptados
    public static void printDecryptedList(String arrayOrSingleJson, KeyManager km) {
        for (String item : JsonHelper.splitJsonObjects(arrayOrSingleJson)) {
            printDecryptedRecord(item, km);
        }
    }
    
    public static EncryptedRecord parseRecord(String json) {
        EncryptedRecord r = new EncryptedRecord();
        r.setIdDet(JsonHelper.extractField(json, "idDet"));
        r.setNameDet(JsonHelper.extractField(json, "fullNameDet"));
        r.setDeptDet(JsonHelper.extractField(json, "deptDet"));
        r.setBonusDet(JsonHelper.extractField(json, "bonusDet"));
        r.setSalaryOpe(Long.parseLong(JsonHelper.extractField(json, "salaryOpe")));
        r.setBirthDateOpe(Long.parseLong(JsonHelper.extractField(json, "birthDateOpe")));

        String salarySum = JsonHelper.extractField(json, "salarySum");
        if (!salarySum.isEmpty()) {
            r.setSalarySum(new java.math.BigInteger(salarySum));
        }

        String c1 = JsonHelper.extractField(json, "salaryMulC1"); 
        String c2 = JsonHelper.extractField(json, "salaryMulC2");
        if (!c1.isEmpty()) r.setSalaryMulC1(new java.math.BigInteger(c1));
        if (!c2.isEmpty()) r.setSalaryMulC2(new java.math.BigInteger(c2));

        r.setSecureEncBlock(JsonHelper.extractField(json, "secureEncBlock"));
        //verifica se o hmac está presente no JSON
        if (json.contains("\"hmac\"")) {
            r.setHmac(Base64.getDecoder().decode(JsonHelper.extractField(json, "hmac")));
        }
        //verifica se a assinatura está presente no JSON
        if (json.contains("\"signature\"")) {
            r.setSignature(Base64.getDecoder().decode(JsonHelper.extractField(json, "signature")));
        }
        return r;
    }

    private static void verifyIntegrity(EncryptedRecord record, KeyManager km) {
        byte[] content = record.getBytesForSigning();
        byte[] expectedHmac = HmacSha.compute(content, km.getHmacKey(), HMAC_BITS);
        if (record.getHmac() == null || !java.security.MessageDigest.isEqual(expectedHmac, record.getHmac())) {
            throw new SecurityException("Invalid HMAC — record may have been altered.");
        }
        if (record.getSignature() == null
                || !EcdsaSigner.verify(content, record.getSignature(), km.getEcdsaPublicKey())) {
            throw new SecurityException("Invalid ECDSA signature");
        }
    }

    private static String decryptSecureBlock(EncryptedRecord record, KeyManager km) {
        byte[] enc = Base64.getDecoder().decode(record.getSecureEncBlock()); //decripta o secure block 
        byte[] plain = AesGcmCipher.decrypt(enc, km.getGcmKey()); 
        return new String(plain, StandardCharsets.UTF_8); //converte o plaintext para string
    }
}
