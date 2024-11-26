package Backend.model;

import Backend.service.DatabaseHandler;

public class UserAccount extends Account {
    private UserPreferences userPreferences;

    public UserAccount(String username, String password, String email) {
        super(username, password, email);
        userPreferences = new UserPreferences();
    }
    public boolean register(DatabaseHandler dbHandler) {
        return dbHandler.registerAccount(this);
    }
    public boolean login(DatabaseHandler dbHandler) {
        return dbHandler.loginAccount(this);
    }
}
