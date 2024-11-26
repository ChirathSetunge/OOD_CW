package Frontend;


import Backend.model.UserAccount;
import Backend.service.DatabaseHandler;
import Backend.util.ConcurrencyManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class LoginController {
    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginButton;

    private final DatabaseHandler databaseHandler = DatabaseHandler.getDbInstance();
    private final ConcurrencyManager concurrencyManager = new ConcurrencyManager();


    @FXML
    public void loginButtonOnAction(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }
        UserAccount userAccount = new UserAccount(username, password, null);
        concurrencyManager.loginAccount(userAccount, databaseHandler);
        //add a label in the fxml to show the user that he has logged in
    }


}
