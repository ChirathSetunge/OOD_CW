package Frontend;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    private void onUserLoginButtonClicked(ActionEvent event) {
        // Move to the login scene
        navigateToLogin(event);
    }
    @FXML
    private void onAdminLoginButtonClicked(ActionEvent event) {
        // Move to the AdminLogin scene
        navigateToAdminLoginScene(event);
    }
    @FXML
    private void onExitButtonClicked(ActionEvent event) {
        // Exit the application
        Platform.exit();
    }
    @FXML
    private void onUserSignUpButtonClicked(ActionEvent event) {
        // Move to the signUp scene
        navigateToSignUp(event);
    }
    private void navigateToAdminLoginScene(ActionEvent event) {
        // Move to the AdminLogin scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminLogin.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the AdminLogin scene.");
        }
    }

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

}
