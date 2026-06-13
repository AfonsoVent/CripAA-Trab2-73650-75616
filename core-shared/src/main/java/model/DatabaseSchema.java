package model;

// Reponsable save SQL queries
public class DatabaseSchema {
    // Target table
    public static final String TABLE_NAME = "encryptedEmployees";
    
    // SQL to create the table
    public static final String CREATE_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "idDet VARCHAR(255), " +
            "fullNameDet VARCHAR(255), " +
            "deptDet VARCHAR(255), " +
            "bonusDet VARCHAR(255), " +
            "salaryOpe BIGINT, " +
            "birthDateOpe BIGINT, " +
            "salarySum TEXT, " +
            "salaryMulC1 TEXT, " +
            "salaryMulC2 TEXT, " +
            "secureEncBlock LONGTEXT, " +
            "hmac VARBINARY(32), " +
            "signature BLOB)";
    
        // SQL to insert a record
    public static final String INSERT_SQL = 
        "INSERT INTO " + TABLE_NAME + " " +
            "(idDet, fullNameDet, deptDet, bonusDet, salaryOpe, birthDateOpe, " +
            "salarySum, salaryMulC1, salaryMulC2, secureEncBlock, hmac, signature) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
}