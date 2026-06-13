package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.DetTokens;
import client.crypto.RecordUnpacker;
import crypto.Paillier;

//op9-> soma da folha e salarial do departamento
public class OpPayrollSum implements ClientOperation {
    @Override
    public String label() { return "9. Department payroll total"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Department ID: ");
        String dept = scanner.nextLine().trim();

        String deptDet = DetTokens.department(ctx.keys(), dept);
        String body = JsonHelper.object(
            JsonHelper.field("deptDet", deptDet),
            JsonHelper.field("paillierNSquare", ctx.keys().getPaillierN().toString())
        );

        String response = ctx.server().post("/employees/payroll-sum", body);
        String cipherText = JsonHelper.extractField(response, "encryptedPayrollSum");

        java.math.BigInteger total = Paillier.decrypt(
            new java.math.BigInteger(cipherText),
            ctx.keys().getPaillierN(),
            ctx.keys().getPaillierLambda(),
            ctx.keys().getPaillierMu()
        );
        
        System.out.println("Employees in department " + dept + ":");
        RecordUnpacker.printDecryptedList(
            ctx.server().post("/employees/by-dept", JsonHelper.object(JsonHelper.field("deptDet", deptDet))),
            ctx.keys()
        );
        System.out.println("Total payroll (decrypted): " + total + " EUR");
    }
}
