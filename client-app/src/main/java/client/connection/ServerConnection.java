package client.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import client.config.ClientConfig;

//Connection to the server (TLS 1.3 + truststore with server certificate)
public class ServerConnection {

    private final String baseUrl;
    private final SSLContext sslContext;

    public ServerConnection() throws Exception {
        this.baseUrl = "https://" + ClientConfig.SERVER_HOST + ":" + ClientConfig.SERVER_PORT;
        this.sslContext = buildSslContext();
    }

    //carrega o truststore (serverstore.p12) para confiar no certificado self-signed
    private SSLContext buildSslContext() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("PKCS12"); 
        char[] password = ClientConfig.TRUSTSTORE_PASSWORD.toCharArray(); //password do truststore
        //tenta carregar o truststore do disco
        try (InputStream in = new java.io.FileInputStream(ClientConfig.TRUSTSTORE_PATH)) {
            trustStore.load(in, password);
        }
        //inicia o trustmanager
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore); 

        SSLContext ctx = SSLContext.getInstance("TLSv1.3"); //inicia o SSLContext
        ctx.init(null, tmf.getTrustManagers(), null);
        return ctx; 
    }

    // GET -> ex: health
    public String get(String path) throws IOException {
        HttpsURLConnection conn = openConnection(path, "GET");
        conn.connect();
        return readResponse(conn);
    }

    // POST with JSON body -> all encrypted operations
    public String post(String path, String jsonBody) throws IOException {
        HttpsURLConnection conn = openConnection(path, "POST"); //abre a conexão com o server
        conn.setDoOutput(true); //permite enviar dados para o server
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); 

        byte[] body = jsonBody.getBytes(StandardCharsets.UTF_8); //converte o JSON para bytes
        conn.setFixedLengthStreamingMode(body.length); //define o tamanho do body

        conn.connect(); //conecta ao server
        try (OutputStream out = conn.getOutputStream()) { //envia os dados para o server
            out.write(body);
        }
        return readResponse(conn);
    }

    //abre a conexão com o server
    private HttpsURLConnection openConnection(String path, String method) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslContext.getSocketFactory()); //define o SSLSocketFactory
        conn.setRequestMethod(method);
        //aceitar localhost com certificado de desenvolvimento
        conn.setHostnameVerifier((host, session) ->
            ClientConfig.SERVER_HOST.equalsIgnoreCase(host) || "localhost".equalsIgnoreCase(host));
        return conn;
    }
    //vai ler a resposta do server
    private String readResponse(HttpsURLConnection conn) throws IOException {
        int code = conn.getResponseCode(); //pega o código da resposta
        InputStream stream = code >= 400 ? conn.getErrorStream() : conn.getInputStream(); //pega o stream da resposta
        if (stream == null) { //se o stream for nulo, lança uma exceção
            throw new IOException("HTTP " + code + " no response body");
        }
        String body = new String(stream.readAllBytes(), StandardCharsets.UTF_8); //converte o stream para string
        if (code >= 400) { //se o código for maior ou igual a 400, lança uma exceção
            throw new IOException("HTTP " + code + ": " + body);
        }
        return body;
    }
}
