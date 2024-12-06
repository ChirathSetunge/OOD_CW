package Frontend;

import Backend.model.AdminAccount;
import Backend.service.ArticleFetcher;
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

public class AdminController {
    @FXML
    private Label adminLoginInfoPanel;
    @FXML
    private Label infoPanel3;
    @FXML
    private TextField loginUsername;
    @FXML
    private TextField loginPassword;
    private final ArticleFetcher articleFetcher = new ArticleFetcher();

    @FXML
    private void onLoginButtonClicked(ActionEvent event) {
        //admin login process (getting the admin credentials from the text fields)
        String username = loginUsername.getText();
        String password = loginPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            adminLoginInfoPanel.setText("Please fill in all fields");
            System.out.println("Admin did not fill in all fields");
            return;
        }
        // admin login process (checking the credentials)
        AdminAccount adminAccount = new AdminAccount(username, password, null);
        boolean loginSuccess = adminAccount.login();
        if (loginSuccess) {
            adminLoginInfoPanel.setText("Login successful!");
            System.out.println("Admin Login successful!");
            // Move to the next scene (Admin)
            navigateToAdminDashboard(event);
        } else {
            adminLoginInfoPanel.setText("Login failed! Please check your username and password.");
            System.out.println("Admin login failed! error in username and password.");
        }

    }
    private void navigateToAdminDashboard(ActionEvent event) {
        // Move to the Admin scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the AdminDashboard scene.");
        }
    }
    @FXML
    private void onCategorizeArticlesButtonClicked() {
        //admin fetch and categorize articles process
        try {
            articleFetcher.fetchAndCategorizeArticles();
            infoPanel3.setText("Articles fetched and categorized successfully.");
            System.out.println("Articles fetched and categorized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            infoPanel3.setText("Failed to fetch and categorize articles.");
            System.out.println("Failed to fetch and categorize articles.");
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
    private void onHomeButtonClicked(ActionEvent event) {
        // Move to the scene
        navigateToStartScene(event);
    }
    @FXML
    private void onStartSceneButtonClicked(ActionEvent event) {
        // Move to the scene
        navigateToStartScene(event);
    }

}

