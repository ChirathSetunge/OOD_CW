package Backend.model;

import Backend.service.DatabaseHandler;

import java.util.concurrent.ExecutionException;

public class UserAccount extends Account {
    private final UserPreferences userPreferences;

    public UserAccount(String username, String password, String email) {
        //UserAccount "is a" Account. (Inheritance)
        super(username, password, email);
        //UserAccount "has a" UserPreferences. (Composition)
        userPreferences = new UserPreferences();
    }
    public boolean register(DatabaseHandler databaseHandler) {
        // register account and save preferences to DB (initially all 0)
        boolean success = false;
        try {
            success = databaseHandler.registerAccountAsync(this).get();
            if (success) {
                // save preferences to DB
                this.userPreferences.savePreferencesToDB(databaseHandler, this.getUsername());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean login(DatabaseHandler databaseHandler) {
        // login account and load preferences from DB
        boolean success = false;
        try {
            success = databaseHandler.loginAccountAsync(this).get();
            if (success) {
                // load preferences from DB
                this.userPreferences.loadPreferencesFromDB(databaseHandler, this.getUsername());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }
}
