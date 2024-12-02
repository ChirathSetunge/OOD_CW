package Backend.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminAccount extends Account {
    //constructor
    public AdminAccount(String username, String password, String email) {
        super(username, password, email);
    }
    public boolean login() {
        String line;
        String csvFile = "admin_credentials.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials[0].equals(this.getUsername()) && credentials[1].equals(this.getPassword())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
