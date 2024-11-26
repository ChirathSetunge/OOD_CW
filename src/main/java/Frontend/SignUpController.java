package Frontend;

import Backend.model.UserAccount;
import Backend.service.DatabaseHandler;
import Backend.util.ConcurrencyManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class SignUpController {
    @FXML
    private Button signUpButton;
    @FXML
    private TextField signUpUsername;
    @FXML
    private TextField signUpPassword;
    @FXML
    private Button loginButton;
    @FXML
    private TextField signUpEmail;


    private final DatabaseHandler databaseHandler = DatabaseHandler.getDbInstance();
    private final ConcurrencyManager concurrencyManager = new ConcurrencyManager();

    @FXML
    public void signUpButtonOnAction(ActionEvent event) {
        String username = signUpUsername.getText();
        String password = signUpPassword.getText();
        String email = signUpEmail.getText();
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }
        UserAccount userAccount = new UserAccount(username, password, email);
        concurrencyManager.registerAccount(userAccount, databaseHandler);
        //add a label in the fxml to show the user that the account has been created
    }
}
