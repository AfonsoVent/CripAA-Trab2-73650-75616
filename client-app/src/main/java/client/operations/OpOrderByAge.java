package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.RecordUnpacker;

//op7-> lista ordenada por idades
public class OpOrderByAge implements ClientOperation {
    @Override
    public String label() { return "7. List ordered by age"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Ascending age (youngest first)? (y/n): ");
        boolean ascending = scanner.nextLine().trim().equalsIgnoreCase("y");

        String body = JsonHelper.object(JsonHelper.field("ascending", ascending));
        String response = ctx.server().post("/employees/order-by-age", body);

        System.out.println("Employees (decrypted):");
        RecordUnpacker.printDecryptedList(response, ctx.keys());
    }
}
