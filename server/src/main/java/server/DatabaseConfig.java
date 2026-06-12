package server;

public final class DatabaseConfig {
    // MySQL connection credentials
    public static final String URL = "jdbc:mysql://localhost:3306/encdb?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String USER = "root";
    public static final String PASS = "pwdOSEOMDM324MCNMSKHCJCLCMDJ";

    // Table target
    public static final String TABLE = "encryptedEmployees";

    private DatabaseConfig(){}
}