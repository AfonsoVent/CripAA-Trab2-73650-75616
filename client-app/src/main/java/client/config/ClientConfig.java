package client.config;

//Client config
public final class ClientConfig {
    private ClientConfig() {}

    //Servidor HTTPS (TLS 1.3)
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8443;

    //truststore
    public static final String TRUSTSTORE_PATH = "../server/serverstore.p12";
    public static final String TRUSTSTORE_PASSWORD = "UPSTGQEDQAPVCASMLAppidmfP1331928";

    //index
    public static final String INDEX_PATH = "client-index.bin";

    //rate
    public static final double EUR_USD_RATE = 1.08;

    //bonus
    public static final long BONUS_PERCENT_SCALAR = 25;
}
