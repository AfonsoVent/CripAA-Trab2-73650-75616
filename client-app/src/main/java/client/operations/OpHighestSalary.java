package client.operations;

import java.util.Scanner;

import client.crypto.RecordUnpacker;

//op5-> search do funcionario com o maior salario
public class OpHighestSalary implements ClientOperation {
    @Override
    public String label() { return "5. Highest salary"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        String response = ctx.server().post("/employees/highest-salary", "{}");
        System.out.println("Highest-paid employee (decrypted):");
        RecordUnpacker.printDecryptedRecord(response, ctx.keys());
    }
}
