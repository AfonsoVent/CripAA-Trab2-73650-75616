package server;

public final class Config {
    private Config() {}

    // MySQL connection credentials
    public static final String DB_URL = "jdbc:mysql://localhost:3306/encdb?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "pwdOSEOMDM324MCNMSKHCJCLCMDJ";

    // Table target
    public static final String DB_TABLE = "encryptedEmployees";

    // Configs HTTPS
    public static final int SERVER_PORT = 8443;
    public static final String KEYSTORE_PATH = "serverstore.p12";
    public static final String KEYSTORE_PASSWORD = "UPSTGQEDQAPVCASMLAppidmfP1331928";
}