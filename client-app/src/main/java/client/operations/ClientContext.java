package client.operations;

import bootstrap.ClientIndexStore;
import client.config.ClientConfig;
import client.connection.ServerConnection;
import crypto.KeyManager;


//contexto compartilhado por todas as operações (índice + conexão HTTPS)
public class ClientContext {

    private final KeyManager keyManager;
    private final ServerConnection connection;

    public ClientContext(KeyManager keyManager, ServerConnection connection) {
        this.keyManager = keyManager;
        this.connection = connection;
    }

    public static ClientContext load() throws Exception {
        KeyManager km = ClientIndexStore.load(ClientConfig.INDEX_PATH); //carrega o índice
        ServerConnection conn = new ServerConnection(); //cria a conexão com o server
        return new ClientContext(km, conn); 
    }
 
    public KeyManager keys() { return keyManager; } //retorna o key manager
    public ServerConnection server() { return connection; } //retorna a conexão com o server
}
