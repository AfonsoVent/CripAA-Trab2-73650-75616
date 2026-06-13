package server.db;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;

import model.EncryptedRecord;
import server.config.Config;
import server.crypto.SalaryConversionService;
import model.DatabaseSchema;

// Responsable to save encrypted employees records
public class EncryptedEmployeeRepository {
    // Insert multiple records into the database
    public void insertAll(List<EncryptedRecord> records) throws SQLException {
        if (records == null || records.isEmpty()) {
            System.out.println("There are no records to save");
            return;
        }

        // Insert query
        String sql = DatabaseSchema.INSERT_SQL;

        try (Connection conn = DriverManager.getConnection(
            Config.DB_URL, Config.DB_USER, Config.DB_PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Create table if needed
            createTableIfNotExists(conn);

            // For faster batch inserts
            conn.setAutoCommit(false);

            for (EncryptedRecord rec : records) {
                // DET fields
                ps.setString(1, rec.getIdDet());
                ps.setString(2, rec.getNameDet());
                ps.setString(3, rec.getDeptDet());
                ps.setString(4, rec.getBonusDet());
                
                // mOPE fields
                ps.setLong(5, rec.getSalaryOpe());
                ps.setLong(6, rec.getBirthDateOpe());
                
                // HOM-ADD (Paillier)
                ps.setString(7, rec.getSalarySum() != null ? rec.getSalarySum().toString() : null);

                // HOM-MUL (ElGamal)
                ps.setString(8, rec.getSalaryMulC1() != null ? rec.getSalaryMulC1().toString() : null);
                ps.setString(9, rec.getSalaryMulC2() != null ? rec.getSalaryMulC2().toString() : null);

                // Bloco (AES/GCM)
                ps.setString(10, rec.getSecureEncBlock());
                
                // HMAC and signature
                ps.setBytes(11, rec.getHmac());
                ps.setBytes(12, rec.getSignature());
                
                // Add to batch
                ps.addBatch();
            }
            // Execute batch
            int[] results = ps.executeBatch();

            // Save changes
            conn.commit();

            System.out.println("Saved completed of length: " + results.length);
        }
    }

    // Search one record by encrypted ID
    // "Search and retrieve registered information by Employee Identification"
    public Optional<EncryptedRecord> findByIdDet(String idDet) throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + " WHERE idDet = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(
            Config.DB_URL, Config.DB_USER, Config.DB_PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idDet);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    // Search one record by encrypt FullName
    // "Search and retrieve registered information by Employee Full Name"
    public Optional<EncryptedRecord> findByFullNameDet(String fullNameDet) throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + " WHERE fullNameDet = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fullNameDet);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    // Search all employees from a department
    // "Search and retrieve Employees belonging to a given Department"
    public List<EncryptedRecord> findByDeptDet(String deptDet) throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + " WHERE deptDet = ?";
        List<EncryptedRecord> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, deptDet);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // Order employees by salary
    // "Search and retrieve Employees by an ordered list of their salaries"
    public List<EncryptedRecord> orderBySalaryOpe(boolean ascending) throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + 
                     " ORDER BY salaryOpe " + (ascending ? "ASC" : "DESC");
        List<EncryptedRecord> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // Get employee with highest salary
    // "Search and retrieve the Employee with the highest salary"
    public Optional<EncryptedRecord> findHighestSalaryOpe() throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + " ORDER BY salaryOpe DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        }
    }

    // Compare salaries of 2 employees
    // "Compare whether an employee has a higher salary than another, given the FullNames of both employees"
    public int compareSalariesOpe(String fullNameDetA, String fullNameDetB) throws SQLException {
        Optional<EncryptedRecord> empA = findByFullNameDet(fullNameDetA);
        Optional<EncryptedRecord> empB = findByFullNameDet(fullNameDetB);

        if (empA.isEmpty() || empB.isEmpty()) {
            throw new IllegalArgumentException("Some employee wasn't found");
        }

        return Long.compare(empA.get().getSalaryOpe(), empB.get().getSalaryOpe());
    }

    // Order employees by age
    // "Search and retrieve the list of employees ordered by their ages"
    public List<EncryptedRecord> orderByAgeOpe(boolean ascending) throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + " ORDER BY birthDateOpe " + (ascending ? "DESC" : "ASC");
        List<EncryptedRecord> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
    
    // Get oldest employee
    // "Find the oldest employee and retrieve all registered information for that employee"
    public Optional<EncryptedRecord> findOldestEmployeeOpe() throws SQLException {
        String sql = "SELECT * FROM " + DatabaseSchema.TABLE_NAME + " ORDER BY birthDateOpe ASC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        }
    }

    // Convert encrypted salaries EUR to USD
    // "Obtain the salary of a given list of employees converted to US Dollars (using an llustrative Euro–US Dollar exchange rate)"
    public Map<String, BigInteger[]> convertSalariesToUsdElGamal(List<String> idsDet, BigInteger rateNumerator, BigInteger elGamalP) throws SQLException {
        Map<String, BigInteger[]> results = new HashMap<>();
        if (idsDet == null || idsDet.isEmpty()) return results;
    
        StringJoiner placeholders = new StringJoiner(",", "(", ")");
        for (int i = 0; i < idsDet.size(); i++) placeholders.add("?");
    
        String sql = "SELECT idDet, salaryMulC1, salaryMulC2 FROM " + DatabaseSchema.TABLE_NAME + " WHERE idDet IN " + placeholders.toString();
    
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < idsDet.size(); i++) {
                ps.setString(i + 1, idsDet.get(i));
            }
    
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("idDet");
                    String sC1 = rs.getString("salaryMulC1");
                    String sC2 = rs.getString("salaryMulC2");
    
                    if (sC1 != null && sC2 != null) {
                        BigInteger c1 = new BigInteger(sC1);
                        BigInteger c2 = new BigInteger(sC2);                        
                        
                        EncryptedRecord tmp = new EncryptedRecord();
                        tmp.setSalaryMulC1(c1);
                        tmp.setSalaryMulC2(c2);
                        
                        crypto.ElGamalCiphertext converted = 
                            SalaryConversionService.convertEurToUsdEncrypted(
                            tmp, rateNumerator, elGamalP
                        );
                        
                        results.put(
                            id, 
                            new BigInteger[]{converted.c1, converted.c2}
                        );
                    }
                }
            }
        }
        return results;
    }

    // Calculate total payroll of specific department
    // "Find all employees in a given Department AND compute the encrypted payroll um of their salaries to obtain the total payroll of the list"
    public BigInteger calculatePayrollSumPaillier(String deptDet, BigInteger paillierN) throws SQLException {
        String sql = "SELECT salarySum FROM " + DatabaseSchema.TABLE_NAME + " WHERE deptDet = ?";
        BigInteger paillierNSquare = paillierN.multiply(paillierN);
        BigInteger encryptedSum = null;
        
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, deptDet);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sSum = rs.getString("salarySum");
                    if (sSum != null) {
                        BigInteger currentCiphertext = new BigInteger(sSum);

                        if (encryptedSum == null) {
                            encryptedSum = currentCiphertext;
                        } else { 
                            encryptedSum = encryptedSum.multiply(currentCiphertext).mod(paillierNSquare);
                        }
                    }
                }
            }
        }

        if (encryptedSum == null) {
            throw new IllegalArgumentException("Error, there are no employees in department: " + deptDet);
        }

        return encryptedSum;
    }

    // Calculate bonus for eligible employees
    // "For all employees with Bonus Eligibility, compute the bonus to be awarded according to the company's bonus policy: 25% of the registered salary"
    public Map<String, BigInteger[]> calculateBonusAllEligibleElGamal(String bonusDetEligible, long scalarModifier, BigInteger elGamalP) throws SQLException {
        String sql = "SELECT idDet, salaryMulC1, salaryMulC2 FROM " + Config.DB_TABLE + " WHERE bonusDet = ?";
        Map<String, BigInteger[]> bonusMap = new HashMap<>();
        BigInteger scalar = BigInteger.valueOf(scalarModifier); // (default value)=> 25
    
        try (Connection conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, bonusDetEligible);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("idDet");
                    String sC1 = rs.getString("salaryMulC1");
                    String sC2 = rs.getString("salaryMulC2");
    
                    if (sC1 != null && sC2 != null) {
                        BigInteger c1 = new BigInteger(sC1);
                        BigInteger c2 = new BigInteger(sC2);
                        
                        EncryptedRecord tmp = new EncryptedRecord();
                        tmp.setSalaryMulC1(c1);
                        tmp.setSalaryMulC2(c2);
                        
                        crypto.ElGamalCiphertext bonusConverted = 
                        SalaryConversionService.convertEurToUsdEncrypted(
                            tmp, scalar, elGamalP
                        );
                        
                        bonusMap.put(
                            id, 
                            new BigInteger[]{bonusConverted.c1, bonusConverted.c2}
                        );
                    }
                }
            }
        }
        return bonusMap;
    }

    // Convert a database row to EncryptedRecord
    private EncryptedRecord mapRow(ResultSet rs) throws SQLException {
        EncryptedRecord r = new EncryptedRecord();

        // DET fields
        r.setIdDet(rs.getString("idDet"));
        r.setNameDet(rs.getString("fullNameDet"));
        r.setDeptDet(rs.getString("deptDet"));
        r.setBonusDet(rs.getString("bonusDet"));

        // mOPE fields
        r.setSalaryOpe(rs.getLong("salaryOpe"));
        r.setBirthDateOpe(rs.getLong("birthDateOpe"));

        // HOM-ADD (Paillier)
        r.setSalarySum(rs.getString("salarySum") != null ? new BigInteger(rs.getString("salarySum")) : null);

        // HOM-MUL (ElGamal)
        r.setSalaryMulC1(rs.getString("salaryMulC1") != null ? new BigInteger(rs.getString("salaryMulC1")) : null);
        r.setSalaryMulC2(rs.getString("salaryMulC2") != null ? new BigInteger(rs.getString("salaryMulC2")) : null);

        // Bloco (AES/GCM)
        r.setSecureEncBlock(rs.getString("secureEncBlock"));

        // HMAC and signature
        r.setHmac(rs.getBytes("hmac"));
        r.setSignature(rs.getBytes("signature"));

        return r;
    }

    // Creates a table if only doesn't exists
    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String createTableSQL = DatabaseSchema.CREATE_TABLE_SQL;
        
        try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.execute();
        }
    }
}