package client.operations;

import java.util.Scanner;

import client.config.ClientConfig;
import client.connection.JsonHelper;
import client.crypto.DetTokens;
import crypto.ElGamal;
import crypto.ElGamalCiphertext;

//op11-> calculate bonus
public class OpCalculateBonus implements ClientOperation {
    @Override
    public String label() { return "11. 25% bonus (eligible employees)"; } 

    @Override
    public void execute(Scanner scanner, ClientContext ctx) throws Exception {
        String bonusDet = DetTokens.bonusEligibleTrue(ctx.keys());//cria o token 
        String body = JsonHelper.object( //cria o body da requisição
            JsonHelper.field("bonusDetEligible", bonusDet), 
            JsonHelper.field("scalarModifier", ClientConfig.BONUS_PERCENT_SCALAR), //25%
            JsonHelper.field("elGamalP", ctx.keys().getElGamalP().toString()) 
        );
        //envia a requisição para o server
        String response = ctx.server().post("/employees/calculate-bonus", body);

        System.out.println("25% bonus (decrypted):");
        // resposta: {"idDet1":{"c1":"...","c2":"..."}, ...}
        parseAndPrintBonuses(response, ctx);
    }
    //analisa a resposta do server e imprime os bonus decriptados
    private void parseAndPrintBonuses(String json, ClientContext ctx) throws Exception {
        String trimmed = json.trim();
        if (trimmed.equals("{}")) { //se a resposta for vazia, imprime uma mensagem de erro
            System.out.println("(no eligible employees)");
            return;
        }

        // Percorrer pares idDet → {c1,c2}
        int i = 1;
        while (i < trimmed.length() - 1) {
            int keyStart = trimmed.indexOf('"', i); //encontra o índice das primeiras aspas
            if (keyStart < 0) break; //se não encontrar, termina o loop
            int keyEnd = trimmed.indexOf('"', keyStart + 1); //encontra o índice das últimas aspas
            String idDet = trimmed.substring(keyStart + 1, keyEnd); //pega o idDet

            int objStart = trimmed.indexOf('{', keyEnd); //encontra o índice do primeiro {
            int objEnd = findClosingBrace(trimmed, objStart); //encontra o índice do último }
            String obj = trimmed.substring(objStart, objEnd + 1);
            
            //pega os valores de c1 e c2
            java.math.BigInteger c1 = new java.math.BigInteger(JsonHelper.extractField(obj, "c1"));
            java.math.BigInteger c2 = new java.math.BigInteger(JsonHelper.extractField(obj, "c2"));
            ElGamalCiphertext ct = new ElGamalCiphertext(c1, c2); //cria o ciphertext

            //decripta o bonus e divide por 100
            java.math.BigInteger raw = ElGamal.decrypt(ct, ctx.keys().getElGamalP(), ctx.keys().getElGamalPriv());
            java.math.BigInteger bonus = raw.divide(java.math.BigInteger.valueOf(100));
            
            System.out.println("idDet=" + idDet + " -> 25% bonus ~ " + bonus + " EUR (raw=" + raw + ")");
            i = objEnd + 1; //atualiza
        }
    }
    //encontra o índice do último }
    private int findClosingBrace(String s, int open) {
        int depth = 0; 
        for (int p = open; p < s.length(); p++) { 
            if (s.charAt(p) == '{') depth++; //incrementa o depth
            //se encontrar um }, decrementa o depth
            else if (s.charAt(p) == '}') { 
                depth--;
                if (depth == 0) return p; //se o depth for 0, retorna o índice
            }
        }
        throw new IllegalArgumentException("JSON mal formado");
    }
}
