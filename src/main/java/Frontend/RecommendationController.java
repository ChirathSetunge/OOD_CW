package Frontend;

import Backend.model.Article;
import Backend.service.DatabaseHandler;
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
import java.util.List;

public class RecommendationController {
    @FXML
    private Label category2Title;
    @FXML
    private Label category1Title;
    @FXML
    private TextFlow category1Content;
    @FXML
    private TextFlow category2Content;
    @FXML
    private Button ViewArticleScene;
    private final DatabaseHandler databaseHandler = DatabaseHandler.getDbInstance();
    public void initialize() {
        System.out.println("RecommendationController initialized");
    }

    public void setRecommendedArticles(List<String> recommendedCategories) {
        if (recommendedCategories != null && recommendedCategories.size() >= 2) {
            String category1 = recommendedCategories.get(0); // First recommended category
            String category2 = recommendedCategories.get(1); // Second recommended category

            // Fetch a random article from category 1 and set the article title
            Article article1 = loadRandomArticle(category1, category1Content);
            if (article1 != null) {
                // Set the article title for category 1
                category1Title.setText(article1.getTitle());
            }

            // Fetch a random article from category 2 and set the article title
            Article article2 = loadRandomArticle(category2, category2Content);
            if (article2 != null) {
                // Set the article title for category 2
                category2Title.setText(article2.getTitle());
            }
        }
    }
    private Article loadRandomArticle(String category, TextFlow contentFlow) {
        // Get a random article for the given category
        Article article = databaseHandler.getArticlesFromDb(category);

        if (article != null) {
            // Update the UI with the article's title and content
            Platform.runLater(() -> {
                contentFlow.getChildren().clear(); // Clear any existing content
                contentFlow.getChildren().add(new Text(article.getContent())); // Add the article's content
            });

        } else {
            // Handle the case where no article is found for the category
            Platform.runLater(() -> {
                contentFlow.getChildren().clear();
                contentFlow.getChildren().add(new Text("No articles available!"));
            });
            System.out.println("No article found for category: " + category);
        }

        return article;
    }
    @FXML
    private void navigateToViewArticle(ActionEvent event) {
        // move to the ViewArticle scene by close the recommendation scene due to object conflicts
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        }





}
