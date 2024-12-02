package Backend.service;

import Backend.util.ArticleCategorizer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class ArticleFetcher {
    private final DatabaseHandler databaseHandler;
    private final ArticleCategorizer articleCategorizer;


    public ArticleFetcher() {
        this.databaseHandler = DatabaseHandler.getDbInstance();
        this.articleCategorizer = new ArticleCategorizer();
    }
    public void fetchAndCategorizeArticles() {
        try {
            // Load articles from database
            List<Map.Entry<String, String>> articles = databaseHandler.loadArticlesAsync().get();
            // Categorize articles
            List<Map<String, String>> categorizedArticles = articleCategorizer.categorizeArticles(articles);
            // Save categorized articles to database
            databaseHandler.saveCategorizedArticlesAsync(categorizedArticles);
            // Database methods run asynchronously, so ExecutionException and InterruptedException is handled
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
