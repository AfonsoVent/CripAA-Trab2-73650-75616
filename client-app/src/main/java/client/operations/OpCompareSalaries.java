package client.operations;

import java.util.Scanner;

import client.connection.JsonHelper;
import client.crypto.DetTokens;

//op6-> compara salarios de dois colaboradores
public class OpCompareSalaries implements ClientOperation {
    @Override
    public String label() { return "6. Compare salaries (two names)"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Full name employee A: ");
        String nameA = scanner.nextLine().trim();
        System.out.print("Full name employee B: ");
        String nameB = scanner.nextLine().trim();

        String body = JsonHelper.object(
            JsonHelper.field("fullNameDetA", DetTokens.fullName(ctx.keys(), nameA)),
            JsonHelper.field("fullNameDetB", DetTokens.fullName(ctx.keys(), nameB))
        );

        String response = ctx.server().post("/employees/compare-salaries", body);
        int result = Integer.parseInt(JsonHelper.extractField(response, "result"));

        if (result > 0) {
            System.out.println(nameA + " has a HIGHER salary than " + nameB);
        } else if (result < 0) {
            System.out.println(nameA + " has a LOWER salary than " + nameB);
        } else {
            System.out.println("Salaries are equal (OPE order)");
        }
    }
}
