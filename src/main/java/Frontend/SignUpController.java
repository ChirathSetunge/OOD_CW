package Frontend;

import Backend.model.UserAccount;
import Backend.service.DatabaseHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    @FXML
    private Label infoPanel;
    @FXML
    private TextField registerPassword;
    @FXML
    private TextField registerUsername;
    @FXML
    private TextField registerEmail;
    private final DatabaseHandler databaseHandler = DatabaseHandler.getDbInstance();

    @FXML
    public void onSignUpButtonClicked(ActionEvent event) {
        //register process (getting the credentials from the text fields)
        String username = registerUsername.getText();
        String password = registerPassword.getText();
        String email = registerEmail.getText();
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            infoPanel.setText("Please fill in all fields");
            System.out.println("New user did not fill in all fields");
            return;
        }
        //register process (checking the credentials)
        UserAccount userAccount = new UserAccount(username, password, email);
        boolean registerSuccess = userAccount.register(databaseHandler);
        if (registerSuccess) {
            infoPanel.setText("Account registered successfully!");
            System.out.println("New account registered successfully!");
            // Move to the next scene (ViewArticle)
            navigateToViewArticle(event, userAccount);
        } else {
            infoPanel.setText("Account registration failed!, username already exists.");
        }
    }
    private void navigateToViewArticle(ActionEvent event, UserAccount userAccount) {
        // Move to the ViewArticle scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ViewArticle.fxml"));
            Parent root = fxmlLoader.load();
            // Pass the userAccount
            ViewArticleController viewArticleController = fxmlLoader.getController();
            viewArticleController.setCurrentUser(userAccount);
            // Move to the next scene in a new window
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            infoPanel.setText("");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the ViewArticle scene.");
        }
    }
    @FXML
    private void navigateToLogin(ActionEvent event) {
        // Move to the login scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the Login scene.");
        }
    }
    @FXML
    private void navigateToStartScene(ActionEvent event) {
        // Move to the start scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the Start scene.");
        }
    }
    @FXML
    private void onStartSceneButtonClicked(ActionEvent event) {
        // Move to the start scene
        navigateToStartScene(event);
    }
    @FXML
    private void onExitButtonClicked(ActionEvent event) {
        // Exit the application
        Platform.exit();
    }

}
