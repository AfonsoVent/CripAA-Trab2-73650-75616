package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.DetTokens;
import client.crypto.RecordUnpacker;

//op4-> search por departamento
public class OpFindByDepartment implements ClientOperation {
    @Override
    public String label() { return "4. Search by Department"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        
        System.out.print("Department ID: ");
        String dept = scanner.nextLine().trim();

        String deptDet = DetTokens.department(ctx.keys(), dept);
        String body = JsonHelper.object(JsonHelper.field("deptDet", deptDet));
        String response = ctx.server().post("/employees/by-dept", body);

        System.out.println("Department employees (decrypted):");
        RecordUnpacker.printDecryptedList(response, ctx.keys());
    }
}
