package server.connection;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import model.EncryptedRecord;
import server.db.EncryptedEmployeeRepository;

// Responsable to take care of requests HTPPS
public class EmployeeHandler {
    // Repo that handles encrypted employee data
    private final EncryptedEmployeeRepository repo = new EncryptedEmployeeRepository();

    // Endpoint: GET /health 
    public void handleHealth(HttpExchange ex) throws IOException {
        if (!"GET".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(405, -1); return; }
        
        // Returns the server is "healthy"
        writeJson(ex, 200, "{\"status\":\"ok\"}");
    }

    // Endpoint: POST /employees/{id}
    public void handleFindById(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Get IDDet
            String idDet = JsonUtils.extractField(body, "idDet");
            
            // Send a querie to repository to try to find
            Optional<EncryptedRecord> rec = repo.findByIdDet(idDet);
            
            // If not found, say it
            if (rec.isEmpty()) { writeJson(ex, 404, "{\"error\":\"Not found\"}"); return; }
            
            // If found, then returns
            writeJson(ex, 200, JsonUtils.recordToJson(rec.get()));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/{name}
    public void handleFindByFullName(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Get fullNameDet
            String fullNameDet = JsonUtils.extractField(body, "fullNameDet");
            
            // Send a querie to repository to try to find
            Optional<EncryptedRecord> rec = repo.findByFullNameDet(fullNameDet);
            
            // If not found, say it
            if (rec.isEmpty()) { writeJson(ex, 404, "{\"error\":\"Not found\"}"); return; }
            
            // If found, then returns
            writeJson(ex, 200, JsonUtils.recordToJson(rec.get()));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/order-by-salary
    public void handleOrderBySalary(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Ascending(T) / Descending(F)
            boolean ascending = Boolean.parseBoolean(JsonUtils.extractField(body, "ascending"));
            
            // Send a querie to repository to and gets a list
            List<EncryptedRecord> list = repo.orderBySalaryOpe(ascending);
            
            // Returns the list
            writeJson(ex, 200, JsonUtils.recordListToJson(list));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/{department}
    public void handleFindByDept(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Get deptDet
            String deptDet = JsonUtils.extractField(body, "deptDet");
            
            // Send a querie to repository to and gets a list of Employees
            List<EncryptedRecord> list = repo.findByDeptDet(deptDet);
            
            // Returns the list
            writeJson(ex, 200, JsonUtils.recordListToJson(list));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/highest-salary
    public void handleFindHighestSalary(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Querie to repo the employee with the highest encrypted salary
            Optional<EncryptedRecord> rec = repo.findHighestSalaryOpe();
            
            // Note: If some reason this happens, it's a error: {DB is empty for some reason; repo op is not well implemented}
            if (rec.isEmpty()) {
                writeJson(ex, 404, "{\"error\":\"Not found\"}");
                return;
            }

            // Send to employeee
            writeJson(ex, 200, JsonUtils.recordToJson(rec.get()));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/compare-salaries
    public void handleCompareSalaries(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);
            
            // Get the encrypted name of Employee A
            String nameA = JsonUtils.extractField(body, "fullNameDetA");
            
            // Get the encrypted name of Employee B
            String nameB = JsonUtils.extractField(body, "fullNameDetB");
            
            // Querie to repo, and gets the result
            int comparisonResult = repo.compareSalariesOpe(nameA, nameB);
            
            // Send the answer
            writeJson(ex, 200, "{\"result\":" + comparisonResult + "}");
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/order-by-age
    public void handleOrderByAge(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Ascending(T) / Descending(F)
            boolean ascending = Boolean.parseBoolean(JsonUtils.extractField(body, "ascending"));
            
            // Querie to repo, and gets a list
            List<EncryptedRecord> list = repo.orderByAgeOpe(ascending);
            
            // Sends JSON
            writeJson(ex, 200, JsonUtils.recordListToJson(list));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/convert-usd
    public void handleConvertUsd(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Gets param to Convert to USD
            List<String> idsDet = JsonUtils.extractArray(body, "idsDet");
            BigInteger rateNumerator = new BigInteger(JsonUtils.extractField(body, "rateNumerator"));
            BigInteger elGamalP = new BigInteger(JsonUtils.extractField(body, "elGamalP"));

            // Querie to the repo
            Map<String, BigInteger[]> convertedMap = repo.convertSalariesToUsdElGamal(idsDet, rateNumerator, elGamalP);

            // Builds JSON response
            StringJoiner sj = new StringJoiner(",", "{", "}");
            for (Map.Entry<String, BigInteger[]> entry : convertedMap.entrySet()) {
                sj.add(String.format(
                    "\"%s\":{\"c1\":\"%s\",\"c2\":\"%s\"}",
                    entry.getKey(),
                    entry.getValue()[0].toString(),
                    entry.getValue()[1].toString()));
            }

            // Sends response
            writeJson(ex, 200, sj.toString());
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/payroll-sum
    public void handlePayrollSum(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Get param
            String deptDet = JsonUtils.extractField(body, "deptDet");
            BigInteger paillierNSquare = new BigInteger(JsonUtils.extractField(body, "paillierNSquare"));
            
            // Compute encrypted payroll
            BigInteger totalSumCiphertext =
                repo.calculatePayrollSumPaillier(deptDet, paillierNSquare);

            // Return encrypted result
            writeJson(ex, 200,
                "{\"deptDet\":\"" + deptDet + "\",\"encryptedPayrollSum\":\""
                + totalSumCiphertext.toString() + "\"}");
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/oldest
    public void handleFindOldest(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Querie to repo the oldest employee
            Optional<EncryptedRecord> rec = repo.findOldestEmployeeOpe();

            // Note: If some reason this happens, it's a error: {DB is empty for some reason; repo op is not well implemented}
            if (rec.isEmpty()) {
                writeJson(ex, 404, "{\"error\":\"Not found\"}");
                return;
            }

            // Returns answer
            writeJson(ex, 200, JsonUtils.recordToJson(rec.get()));
        } catch (Exception e) { writeError(ex, e); }
    }

    // Endpoint: POST /employees/calculate-bonus
    public void handleCalculateBonus(HttpExchange ex) throws IOException {
        if (!validatePost(ex)) return;
        
        try {
            // Read full body JSON
            String body = readBody(ex);

            // Get param
            String bonusDetEligible = JsonUtils.extractField(body, "bonusDetEligible");
            // If not specified bonus, get default 25%
            long scalarModifier = Long.parseLong(JsonUtils.extractFieldOrDefault(body, "scalarModifier", "25"));
            BigInteger elGamalP = new BigInteger(JsonUtils.extractField(body, "elGamalP"));

            // Compute bonuses for all eligible employees
            Map<String, BigInteger[]> bonusMap =
                repo.calculateBonusAllEligibleElGamal(bonusDetEligible, scalarModifier, elGamalP);

            // Builds JSON response
            StringJoiner sj = new StringJoiner(",", "{", "}");
            for (Map.Entry<String, BigInteger[]> entry : bonusMap.entrySet()) {
                sj.add(String.format("\"%s\":{\"c1\":\"%s\",\"c2\":\"%s\"}", 
                entry.getKey(), 
                entry.getValue()[0].toString(), 
                entry.getValue()[1].toString()));
            }

            // Returns JSON
            writeJson(ex, 200, sj.toString());
        } catch (Exception e) { writeError(ex, e); }
    }

    // Auxiliar Functions
    // Checks if request method is POST
    private boolean validatePost(HttpExchange ex) throws IOException {
        if (!"POST".equals(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return false;
        }
        return true;
    }

    // Reads request body
    private String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    // Writes JSON response
    private void writeJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        
        // Output
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        
        // Close exchange
        ex.close();
    }

    // Sends error response
    private void writeError(HttpExchange ex, Exception e) throws IOException {
        writeJson(ex, 400, "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}");
    }
}