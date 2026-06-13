package client.operations;

import java.util.Scanner;

import client.crypto.RecordUnpacker;

//op10-> search do funcionario mais velho
public class OpOldestEmployee implements ClientOperation {
    @Override
    public String label() { return "10. Oldest employee"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        String response = ctx.server().post("/employees/oldest", "{}");
        System.out.println("Oldest employee (decrypted):");
        RecordUnpacker.printDecryptedRecord(response, ctx.keys());
    }
}
