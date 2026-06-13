package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.DetTokens;
import client.crypto.RecordUnpacker;

//op1-> search por id do funcionario
public class OpFindById implements ClientOperation {
    @Override
    public String label() { return "1. Search by Employee ID"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Employee ID: ");
        String id = scanner.nextLine().trim();

        String idDet = DetTokens.employeeId(ctx.keys(), id);
        String body = JsonHelper.object(JsonHelper.field("idDet", idDet));
        String response = ctx.server().post("/employees/by-id", body);

        System.out.println("Result (decrypted):");
        RecordUnpacker.printDecryptedRecord(response, ctx.keys());
    }
}
