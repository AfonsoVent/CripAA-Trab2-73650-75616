package client;

import java.util.Scanner;

import client.operations.SomeOpExample;
import crypto.KeyManager;

// It's the main to sent ops to DB server
public class Client {
    public static void main(String[] args) {
        KeyManager km;
        try {
            km = KeyManager.load("keys.dat");
        } catch (Exception e) {
            System.err.println("It wasn't possible to load the keys");
            return;
        }

        // TODO: something like this
        // ServerConnector server = new ServerConnector("localhost", <portNumber>);

        // if (!server.connect()) {
        //     System.err.println("It wasn't possible to connect");
        //     return;
        // }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        // TODO: Prototipo:
        while (running) {
            System.out.println("\n1. Add Employee");
            System.out.println("2. Search Bonus");
            System.out.println("3. Convert Salary");
            System.out.println("0. Exit");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    SomeOpExample.execute(scanner, km, server);
                    System.out.println("Operation 1");
                    break;
                case "2":
                    // SearchBonusOp.execute(scanner, km, server);
                    System.out.println("Operation 2");
                    break;
                case "3":
                    System.out.println("Operation 3");
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("The options should be: [...]");
            }
        }

        // Close
        server.disconnect();
        scanner.close();
        System.out.println("Client safely ended");
    }
}
