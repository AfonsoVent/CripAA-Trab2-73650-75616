package bootstrap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Employee;

// Responsable to read the Dataset-Emp.cvs
public class DatasetLoader {
    // Reads file and returns a list of employees.
    public static List<Employee> load(String filePath) {
        List<Employee> employees = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        // Open CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip header row
            String header = br.readLine();

            // Check error
            if (header == null) {
                System.err.println("CSV file is empty.");
                return employees;
            }

            // Read remaining rows
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(csvSplitBy);

                // Save employee object
                Employee emp = new Employee();
                emp.setId(data[0].trim());
                emp.setFullName(data[3].trim());
                emp.setBirthDate(data[4].trim());
                emp.setDepartment(data[11].trim());
                emp.setSalary(Integer.parseInt(data[14].trim()));

                employees.add(emp);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error, CSV read: " + e);

        } catch (NumberFormatException e) {
            // Invalid number in CSV
            throw new RuntimeException("Error, CSV format: " + e);
        }

        return employees;
    }
}
