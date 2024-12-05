package Frontend;

import Backend.model.Article;
import Backend.model.UserAccount;
import Backend.service.DatabaseHandler;
import Backend.service.RecommendationEngine;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ViewArticleController {
    @FXML
    private Button articleLikeButton;
    @FXML
    private Button articleDislikeButton;
    @FXML
    private Label articleTitle;
    @FXML
    private TextFlow articleContent;
    @FXML
    private Label articleType;
    @FXML
    private Label infoPanel;

    private final DatabaseHandler databaseHandler = DatabaseHandler.getDbInstance();
    private final RecommendationEngine recommendationEngine = new RecommendationEngine();
    private UserAccount currentUser;
    private Article currentArticle;
    private int preferenceUpdateCount = 0;
    private long articleLoadTime;
    private final List<String> recommendedCategories = new ArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    public void initialize() {
        System.out.println("ViewArticleController initialized");
        recommendationEngine.trainFromCSV();
        System.out.println("Model trained successfully!");
        loadRandomArticle();
    }
    public void setCurrentUser(UserAccount userAccount) {
        // Set the current user from the login controller
        this.currentUser = userAccount;
    }
    private void loadRandomArticle() {
        // Load a random article from the database
        Future<Future<Article>> futureArticleFuture = executor.submit(() -> {
            String randomCategory = getRandomCategory();
            return databaseHandler.getArticlesFromDbAsync(randomCategory);
        });
        // Load the article in the UI
        executor.submit(() -> {
            try {
                // Get the article from the future
                Future<Article> futureArticle = futureArticleFuture.get();
                currentArticle = futureArticle.get();
                Platform.runLater(() -> {
                    // Update the UI with the article's title and content
                    if (currentArticle != null) {
                        articleTitle.setText(currentArticle.getTitle());
                        articleContent.getChildren().setAll(new Text(currentArticle.getContent()));
                        articleType.setText(currentArticle.getCategory().getName());
                        //reset the time when the article is loaded
                        articleLoadTime = System.currentTimeMillis();
                        //enable the like and dislike buttons
                        articleLikeButton.setDisable(false);
                        articleDislikeButton.setDisable(false);
                        infoPanel.setText("Article loaded!");
                    } else {
                        // Handle the case where no article is found
                        articleTitle.setText("No articles available!");
                        articleContent.getChildren().clear();
                        articleType.setText("Unavailable");
                        infoPanel.setText("No articles available for this category. Please try again later.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getRandomCategory() {
        // Get a random category from the list of allowed categories
        List<String> allowedCategories = List.of("Technology", "Health", "Finance", "Politics", "Sports");
        return allowedCategories.get((int) (Math.random() * allowedCategories.size()));
    }
    @FXML
    private void onSkipButtonClicked(ActionEvent event) {
        if (currentArticle != null) {
            // Calculate the time elapsed since the article was loaded
            long timeElapsed = System.currentTimeMillis() - articleLoadTime;

            // If skip is within 10 seconds, it's considered not liking the article
            if (timeElapsed <= 10000) {
                currentUser.getUserPreferences().updatePreference(currentArticle.getCategory(), -3);// -3 points for early skip
                System.out.println("Early skip");
                System.out.println(currentUser.getUsername() + " current score after early skip: " + currentUser.getUserPreferences().getAllPreferences());
            } else {
                // If skip is after 10 seconds, it's considered as user like the article
                currentUser.getUserPreferences().updatePreference(currentArticle.getCategory(), 3); // +3 points for late skip
                System.out.println("Late skip");
                System.out.println(currentUser.getUsername() + " current score after late skip: " + currentUser.getUserPreferences().getAllPreferences());
            }
            // Increment the preference update count
            incrementPreferenceUpdateCount();
            // Load a new random article
            loadRandomArticle();
        }
    }
    @FXML
    private void onLikeButtonClicked(ActionEvent event) {
        if (currentArticle != null) {
            // Update the user's preference score for the article's category
            currentUser.getUserPreferences().updatePreference(currentArticle.getCategory(), 5); // +5 points for liking the article
            // Increment the preference update count
            incrementPreferenceUpdateCount();
            System.out.println("Like button clicked");
            System.out.println(currentUser.getUsername() + " current score after like button clicked: " + currentUser.getUserPreferences().getAllPreferences());
            articleLikeButton.setDisable(true); // Only allow one like per article
            infoPanel.setText("Article liked!");
        }
    }
    @FXML
    private void onDislikeButtonClicked(ActionEvent event) {
        if (currentArticle != null) {
            // Update the user's preference score for the article's category
            currentUser.getUserPreferences().updatePreference(currentArticle.getCategory(), -5);// -5 points for disliking the article
            // Increment the preference update count
            incrementPreferenceUpdateCount();
            System.out.println("Dislike button clicked");
            System.out.println(currentUser.getUsername() + "current score after dislike button clicked: " + currentUser.getUserPreferences().getAllPreferences());
            articleDislikeButton.setDisable(true); // Only allow one dislike per article
            infoPanel.setText("Article disliked!");
        }
    }
    private void incrementPreferenceUpdateCount() {
        // Increment the preference update count and save preferences and recompute recommendations every 5 updates
        preferenceUpdateCount++;
        int SAVE_INTERVAL = 5; // Save preferences and recompute recommendations every 5 updates
        if (preferenceUpdateCount >= SAVE_INTERVAL) {
            savePreferences();
            recomputeRecommendations();
            preferenceUpdateCount = 0;
        }
    }
    private void savePreferences() {
        // Save the user's preferences to the database
        currentUser.getUserPreferences().savePreferencesToDB(databaseHandler, currentUser.getUsername());
        System.out.println(currentUser.getUsername() + "'s preferences saved to the database");
    }
    @FXML
    private void onGetRecommendationButtonClicked(ActionEvent event) {
        // Get recommendations based on the user's preferences
        Map<String, Integer> userPreferences = currentUser.getUserPreferences().getAllPreferences();
        System.out.println(currentUser.getUsername() + "requested recommendations");
        System.out.println("His/Hers user preferences are: " + userPreferences);
        boolean allCategoriesNonZero = userPreferences.values().stream().allMatch(score -> score != 0);
        // Check if all categories have a non-zero preference score
        if (allCategoriesNonZero) {
            recomputeRecommendations();
            navigateToRecommendation(event);
            savePreferences();
        } else {
            System.out.println("Not all categories have a non-zero preference score. User needs to interact with more articles.");
            infoPanel.setText("Please interact with more articles to get recommendations.");
        }
    }
    private void recomputeRecommendations() {
        // Recompute the top categories based on the user's preferences
        Map<String, Integer> userPreferences = currentUser.getUserPreferences().getAllPreferences();
        // Get the top categories based on the user's preferences
        String[] topCategories = recommendationEngine.predictTopCategories(userPreferences);
        recommendedCategories.clear();
        recommendedCategories.addAll(Arrays.asList(topCategories));
        System.out.println("Recommendations computed");
        System.out.println("Updated recommended categories: " + recommendedCategories);
    }
    @FXML
    private void onResetPreferencesButtonClicked(ActionEvent event) {
        // Reset the user's preferences
        currentUser.getUserPreferences().resetPreferences();
        savePreferences();
        System.out.println("Preferences reset");
        infoPanel.setText("Preferences reset!");
    }
    private void navigateToRecommendation(ActionEvent event) {
        // Move to the Recommendation scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Recommendation.fxml"));
            Parent root = fxmlLoader.load();
            // Pass the userAccount
            RecommendationController recommendationController = fxmlLoader.getController();
            recommendationController.setRecommendedArticles(recommendedCategories);
            // Move to the next scene in a new window
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            infoPanel.setText("");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the Recommendation scene.");
        }
    }
    @FXML
    private void onExitButtonClicked(ActionEvent event) {
        // Exit the application
        savePreferences();
        databaseHandler.removeActiveSession(currentUser.getUsername());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();


    }



}
