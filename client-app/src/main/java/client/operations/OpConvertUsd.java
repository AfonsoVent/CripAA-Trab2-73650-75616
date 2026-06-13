package client.operations;

import java.util.Arrays;
import java.util.Scanner;

import client.config.ClientConfig;
import client.connection.JsonHelper;
import client.crypto.DetTokens;
import crypto.ElGamal;
import crypto.ElGamalCiphertext;
import crypto.ExchangeRate;

//op8-> converte salarios para USD
public class OpConvertUsd implements ClientOperation {
    @Override
    public String label() { return "8. Salaries in USD (ID list)"; }

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        System.out.print("Comma-separated IDs: ");
        String line = scanner.nextLine().trim(); 
        String[] ids = Arrays.stream(line.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);

        //cria o array de ids det
        String[] idsDet = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            idsDet[i] = DetTokens.employeeId(ctx.keys(), ids[i]); //cria o token do id
        }

        ExchangeRate rate = new ExchangeRate(ClientConfig.EUR_USD_RATE); //cria a taxa de conversão
        //cria o body da requisição e envia para o server
        String body = "{" + "\"idsDet\":" + JsonHelper.array(idsDet) + "," + JsonHelper.field("rateNumerator", rate.getNumerator().toString()) + ","
            + JsonHelper.field("elGamalP", ctx.keys().getElGamalP().toString()) + "}"; 
        String response = ctx.server().post("/employees/convert-usd", body);

        System.out.println("Salaries in USD (decrypted):");
        //analisa a resposta do server e imprime os salarios decriptados
        for (String id : ids) {
            String idDet = DetTokens.employeeId(ctx.keys(), id); //cria o token do id
            if (!response.contains("\"" + idDet + "\"")) { 
                System.out.println(id + ": not found in response");
                continue;
            }
            //se o id for encontrado, pega o objeto do id e pega os valores de c1 e c2
            String block = extractObjectForKey(response, idDet);
            java.math.BigInteger c1 = new java.math.BigInteger(JsonHelper.extractField(block, "c1"));
            java.math.BigInteger c2 = new java.math.BigInteger(JsonHelper.extractField(block, "c2"));

            //cria o ciphertext e decripta o salario em EUR
            ElGamalCiphertext ct = new ElGamalCiphertext(c1, c2);
            java.math.BigInteger raw = ElGamal.decrypt(ct, ctx.keys().getElGamalP(), ctx.keys().getElGamalPriv());
            java.math.BigInteger usd = rate.applyTo(raw);

            System.out.println(id + ": " + usd + " USD (decrypted EUR salary=" + raw + ")");
        }
    }

    //pega o objeto do id e os valores de c1 e c2
    private String extractObjectForKey(String json, String key) {
        String marker = "\"" + key + "\":{";  
        int i = json.indexOf(marker); //encontra o índice do marker
        if (i < 0) throw new IllegalArgumentException("Key not found: " + key);
        int start = i + marker.length() - 1; //pega o índice do primeiro {
        int depth = 0; 
        for (int p = start; p < json.length(); p++) { 
            char c = json.charAt(p); //pega o caractere do índice p
            if (c == '{') depth++;
            else if (c == '}') { //se encontrar um }, decrementa o depth
                depth--;
                if (depth == 0) return json.substring(start, p + 1); //se o depth for 0, retorna o objeto
            }
        }
        throw new IllegalArgumentException("Malformed JSON");
    }
}
