package client.operations;

import java.util.Scanner;
//contrato de cada operação cliente
public interface ClientOperation {
    String label();
    void execute(Scanner scanner, ClientContext ctx) throws Exception;
}
