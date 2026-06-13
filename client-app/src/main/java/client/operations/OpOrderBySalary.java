package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.RecordUnpacker;

//op3-> lista ordenada por salarios
public class OpOrderBySalary implements ClientOperation {
    @Override
    public String label() { return "3. List ordered by salary"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Ascending order? (y/n): ");
        boolean ascending = scanner.nextLine().trim().equalsIgnoreCase("y");

        String body = JsonHelper.object(JsonHelper.field("ascending", ascending));
        String response = ctx.server().post("/employees/order-by-salary", body);

        System.out.println("Employees (decrypted):");
        RecordUnpacker.printDecryptedList(response, ctx.keys());
    }
}
