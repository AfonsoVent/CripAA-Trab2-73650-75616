package client;

import java.util.Scanner;

import client.operations.ClientContext;
import client.operations.ClientOperation;
import client.operations.OpCalculateBonus;
import client.operations.OpCompareSalaries;
import client.operations.OpConvertUsd;
import client.operations.OpFindByDepartment;
import client.operations.OpFindByFullName;
import client.operations.OpFindById;
import client.operations.OpHighestSalary;
import client.operations.OpOldestEmployee;
import client.operations.OpOrderByAge;
import client.operations.OpOrderBySalary;
import client.operations.OpPayrollSum;

// Menu principal — pede operações ao servidor HTTPS e desencripta no cliente
public class Client {

    private static final ClientOperation[] OPERATIONS = {
        new OpFindById(),
        new OpFindByFullName(),
        new OpOrderBySalary(),
        new OpFindByDepartment(),
        new OpHighestSalary(),
        new OpCompareSalaries(),
        new OpOrderByAge(),
        new OpConvertUsd(),
        new OpPayrollSum(),
        new OpOldestEmployee(),
        new OpCalculateBonus()
    };

    public static void main(String[] args) {
        try {
            ClientContext ctx = ClientContext.load();
            System.out.println("Index loaded. HTTPS connection ready");

            // Verificar servidor
            System.out.println("Health: " + ctx.server().get("/health"));

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n=== PA2 Client — Encrypted operations ===");
                for (ClientOperation op : OPERATIONS) {
                    System.out.println(op.label());
                }
                System.out.println("0. Exit");
                System.out.print("> ");

                String choice = scanner.nextLine().trim();
                if ("0".equals(choice)) {
                    running = false;
                    continue;
                }

                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index < 0 || index >= OPERATIONS.length) {
                        System.out.println("Invalid option");
                        continue;
                    }
                    OPERATIONS[index].execute(scanner, ctx);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a number");
                } catch (Exception e) {
                    System.err.println("Operation error: " + e.getMessage());
                }
            }

            scanner.close();
            System.out.println("Client shut down");
        } catch (Exception e) {
            System.err.println("Failed to start client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
