package bootstrap;

import java.util.ArrayList;
import java.util.List;

import crypto.KeyManager;
import model.Employee;
import model.EncryptedRecord;

// Main of bootstrap
public class Bootstrap {
    public static void main(String[] args) {
        try {
            // Create keys
            KeyManager km = new KeyManager();

            // Read the dataset
            List<Employee> rows = DatasetLoader.load("Company-Employee.Database/Dataset-Emp-Database.csv");
            
            // Check if found it
            if (rows.isEmpty()) System.err.println("CVS not found");

            // Insert values on the trees
            for (Employee emp : rows) {
                km.getMOpeSalaryTree().insert(emp.getSalary());
                km.getMOpeBirthDateTree().insert(emp.getBirthDateAsNumeric());
            }

            // Create a encrypted record of each employee
            List<EncryptedRecord> records = new ArrayList<>();
            for (Employee emp : rows) {
                EncryptedRecord rec = EncryptedRecordBuilder.build(emp, km);
                records.add(rec);
            }
    
            // Save the local indice
            ClientIndexStore.save(km, "client-index.bin");
    
            // Sends encrypted records to the server
            ServerUploader.upload(records);
    
            // // Debug time
            // TODO: Debug performance
        } catch (Exception e) {
            System.err.println("Error, something went wrong initialising the bootstrap: " + e);
        }
    }
}
