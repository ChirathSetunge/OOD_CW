package Frontend;


import Backend.model.UserAccount;
import Backend.service.DatabaseHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    @FXML
    private Label infoPanel;
    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;
    private final DatabaseHandler databaseHandler = DatabaseHandler.getDbInstance();

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        //login process (getting the credentials from the text fields)
        String username = loginUsername.getText();
        String password = loginPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            infoPanel.setText("Please fill in all fields");
            System.out.println("User did not fill in all fields");
            return;
        }
        // login process (checking the credentials)
        UserAccount userAccount = new UserAccount(username, password, null);
        boolean loginSuccess = userAccount.login(databaseHandler);
        if (loginSuccess) {
            infoPanel.setText("Login successful!");
            System.out.println("User login successful!");
            // Move to the next scene (ViewArticle)
            navigateToViewArticle(event, userAccount);
        } else {
            infoPanel.setText("Login failed! Please check your username and password or already logged in.");
            loginUsername.clear();
            loginPassword.clear();
            System.out.println("User login failed! error in username and password.");
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
            loginUsername.clear();
            loginPassword.clear();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the ViewArticle scene.");
        }
    }
    @FXML
    private void navigateToSignUp(ActionEvent event) {
        // Move to the signUp scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the SignUp scene.");
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
        databaseHandler.closeConnection();
        System.exit(0);
    }


}
