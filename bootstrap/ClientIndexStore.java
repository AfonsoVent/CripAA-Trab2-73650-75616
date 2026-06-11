package bootstrap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import crypto.KeyManager;

// Responsable to serializes and saves the index
public class ClientIndexStore {
    // Saves the KeyManager into a file
    public static void save(KeyManager km, String filePath) {
        if (km == null || filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("KeyManager and filePath cannot be null!");
        }

        // Write object to file
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(km);
            System.out.println("Client state was saved on: " + filePath);
        } catch (IOException e) {
            System.err.println("Error, failed to save client index: " + e);
        }
    }

    // Loads the KeyManager from a file
    public static KeyManager load(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("FilePath cannot be null/empty");
        }

        // Read object from file
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {            
            KeyManager km = (KeyManager) ois.readObject();
            System.out.println("Client state was loaded with the path: " + filePath);

            return km;

        } catch (IOException e) {
            throw new RuntimeException("Error, failed to load file: " + e.getMessage());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error, invalid KeyManager class: " + e.getMessage());
        }
    }
}
