package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.DetTokens;
import client.crypto.RecordUnpacker;

//op2-> search por nome completo
public class OpFindByFullName implements ClientOperation {
    @Override
    public String label() { return "2. Search by Full Name"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Full name: ");
        String name = scanner.nextLine().trim();

        String nameDet = DetTokens.fullName(ctx.keys(), name);
        String body = JsonHelper.object(JsonHelper.field("fullNameDet", nameDet));
        String response = ctx.server().post("/employees/by-name", body);

        System.out.println("Result (decrypted):");
        RecordUnpacker.printDecryptedRecord(response, ctx.keys());
    }
}
