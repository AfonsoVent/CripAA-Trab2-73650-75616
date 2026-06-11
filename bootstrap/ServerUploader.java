package bootstrap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import model.EncryptedRecord;

// Responsable to send records to the SQL-server
public class ServerUploader {
    // TODO: Descobrir quais sao as credenciais
    // MySQL connection credentials
    private static final String DB_URL = "goodFuckingQuestion";
    private static final String USER = "root";
    private static final String PASS = "your_password";

    // Target table
    private static final String TABLE_NAME = "encryptedEmployees";

    public static void upload(List<EncryptedRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("There are no records to upload");
            return;
        }

        // Insert query
        String insertSQL = "INSERT INTO " + TABLE_NAME + " " +
                           "(idDet, nameDet, deptDet, salaryOpe, ageOpe, " +
                           "salarySum, salaryMulC1, salaryMulC2, secureEncBlock, hmac, signature) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            // Create table if needed
            createTableIfNotExists(conn);

            // Faster batch inserts
            conn.setAutoCommit(false);

            for (EncryptedRecord rec : records) {
                // DET fields
                pstmt.setString(1, rec.getIdDet());
                pstmt.setString(2, rec.getNameDet());
                pstmt.setString(3, rec.getDeptDet());

                // mOPE fields
                pstmt.setLong(4, rec.getSalaryOpe());
                pstmt.setLong(5, rec.getAgeOpe());

                // HOM-ADD (Paillier)
                pstmt.setString(6, rec.getSalarySum() != null ? rec.getSalarySum().toString() : null);
                
                // HOM-MUL (ElGamal)
                pstmt.setString(7, rec.getSalaryMulC1() != null ? rec.getSalaryMulC1().toString() : null);
                pstmt.setString(8, rec.getSalaryMulC2() != null ? rec.getSalaryMulC2().toString() : null);
                
                // Bloco (AES/GCM)
                pstmt.setString(9, rec.getSecureEncBlock());

                // HMAC and signature
                pstmt.setBytes(10, rec.getHmac());
                pstmt.setBytes(11, rec.getSignature());

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
        String createTableSQL =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "idDet VARCHAR(255), " +
                    "fullNameDet VARCHAR(255), " +
                    "deptDet VARCHAR(255), " +
                    "salaryOpe BIGINT, " +
                    "ageOpe BIGINT, " +
                    "salarySum TEXT, " +
                    "salaryMulC1 TEXT, " +
                    "salaryMulC2 TEXT, " +
                    "secureEncBlock LONGTEXT, " +
                    "hmac VARCHAR(255), " +
                    "signature TEXT)";
        try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.execute();
        }
    }
}
