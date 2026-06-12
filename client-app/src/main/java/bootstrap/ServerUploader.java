package bootstrap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import model.DatabaseSchema;
import model.EncryptedRecord;

// Responsable to send records to the SQL-server
public class ServerUploader {
    // MySQL connection credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/encdb?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "pwdOSEOMDM324MCNMSKHCJCLCMDJ";

    public static void upload(List<EncryptedRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("There are no records to upload");
            return;
        }

        // Insert query
        String insertSQL = DatabaseSchema.INSERT_SQL;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            // Create table if needed
            createTableIfNotExists(conn);

            // For faster batch inserts
            conn.setAutoCommit(false);

            for (EncryptedRecord rec : records) {
                // DET fields
                pstmt.setString(1, rec.getIdDet());
                pstmt.setString(2, rec.getNameDet());
                pstmt.setString(3, rec.getDeptDet());
                pstmt.setString(4, rec.getBonusDet());

                // mOPE fields
                pstmt.setLong(5, rec.getSalaryOpe());
                pstmt.setLong(6, rec.getBirthDateOpe());

                // HOM-ADD (Paillier)
                pstmt.setString(7, rec.getSalarySum() != null ? rec.getSalarySum().toString() : null);
                
                // HOM-MUL (ElGamal)
                pstmt.setString(8, rec.getSalaryMulC1() != null ? rec.getSalaryMulC1().toString() : null);
                pstmt.setString(9, rec.getSalaryMulC2() != null ? rec.getSalaryMulC2().toString() : null);
                
                // Bloco (AES/GCM)
                pstmt.setString(10, rec.getSecureEncBlock());

                // HMAC and signature
                pstmt.setBytes(11, rec.getHmac());
                pstmt.setBytes(12, rec.getSignature());

                // Add to batch
                pstmt.addBatch();
            }
            // Execute batch
            int[] results = pstmt.executeBatch();

            // Save changes
            conn.commit();

            System.out.println("Upload completed of length: " + results.length);

        } catch (SQLException e) {
            throw new RuntimeException("Error, MySQL: " + e);
        }
    }

    // Creates a table if only doesn't exists
    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String createTableSQL = DatabaseSchema.CREATE_TABLE_SQL;

        try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.execute();
        }
    }
}
