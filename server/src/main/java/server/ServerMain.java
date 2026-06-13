package server;

import com.sun.net.httpserver.HttpsServer;

import server.config.Config;
import server.connection.EmployeeHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

// Main server
public class ServerMain {
    public static void main(String[] args) {
        try {
            // Get port from args or use default 8443
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 8443;

            // Create instance and start
            ServerMain serverInstance = new ServerMain();
            serverInstance.start(port);
        } catch (Exception e) {
            System.err.println("Error, failed to start the server: " + e);
        }
    }

    // Starts a secure HTTPS server
    public void start(int port) throws Exception {
        // Creates HTTPS server
        HttpsServer server = HttpsServer.create(new InetSocketAddress(port), 0);

        // Create the handle request
        EmployeeHandler handler = new EmployeeHandler();

        // Initialize TLSv1.3 context
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");

        // Load server keystore containing certificate
        char[] password = Config.KEYSTORE_PASSWORD.toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        // Try read keystore file from disk
        try (FileInputStream fis = new FileInputStream(Config.KEYSTORE_PATH)) {
            ks.load(fis, password);
        } catch (FileNotFoundException e) {
            System.err.println("Error, KeyStore not found at " + Config.KEYSTORE_PATH);
            throw e;
        }

        // Initialize key manager with keystore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // Initialize TLSv1.3 context with keys
        sslContext.init(kmf.getKeyManagers(), null, null);

        // Configure HTTPS settings for the server
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                try {
                    // Get context
                    SSLContext context = getSSLContext();

                    // Get default parameters
                    SSLParameters sslParams = context.getDefaultSSLParameters();

                    // Force to be TLSv1.3
                    sslParams.setProtocols(new String[]{"TLSv1.3"});

                    // Apply parameters
                    params.setSSLParameters(sslParams);
                } catch (Exception e) {
                    System.err.println("Error, failed on configuration TLS parameters");
                }
            }
        });

        // Map API routes
        server.createContext("/health", handler::handleHealth);
        server.createContext("/employees/by-id", handler::handleFindById);
        server.createContext("/employees/by-name", handler::handleFindByFullName);
        server.createContext("/employees/order-by-salary", handler::handleOrderBySalary);
        server.createContext("/employees/by-dept", handler::handleFindByDept);
        server.createContext("/employees/highest-salary", handler::handleFindHighestSalary);
        server.createContext("/employees/compare-salaries", handler::handleCompareSalaries);
        server.createContext("/employees/order-by-age", handler::handleOrderByAge);
        server.createContext("/employees/convert-usd", handler::handleConvertUsd);
        server.createContext("/employees/payroll-sum", handler::handlePayrollSum);
        server.createContext("/employees/oldest", handler::handleFindOldest);
        server.createContext("/employees/calculate-bonus", handler::handleCalculateBonus);
        
        // Use default thread executor
        server.setExecutor(null);

        // Start HTTPS server
        server.start();

        // Debug
        System.out.println("Secure HTTPS Server is running on port: (" + port + ") - (TLSv1.3)");
    }
}