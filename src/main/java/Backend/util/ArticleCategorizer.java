package Backend.util;

import java.util.*;

public class ArticleCategorizer {

    // Category keywords for classification
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        CATEGORY_KEYWORDS.put("Technology", Arrays.asList("computer", "software", "hardware", "internet", "ai"));
        CATEGORY_KEYWORDS.put("Health", Arrays.asList("medicine", "doctor", "hospital", "disease", "treatment"));
        CATEGORY_KEYWORDS.put("Sports", Arrays.asList("football", "basketball", "tennis", "athlete", "game"));
        CATEGORY_KEYWORDS.put("AI", Arrays.asList("artificial", "intelligence", "machine", "learning", "robotics"));
    }

    public List<Map<String, String>> categorizeArticles(List<Map.Entry<String, String>> articles) {
        List<Map<String, String>> categorizedArticles = new ArrayList<>();

        for (Map.Entry<String, String> article : articles) {
            String title = article.getKey();
            String content = article.getValue();
            String category = determineCategory(content);

            Map<String, String> categorizedArticle = new HashMap<>();
            categorizedArticle.put("title", title);
            categorizedArticle.put("content", content);
            categorizedArticle.put("category", category);

            categorizedArticles.add(categorizedArticle);
        }

        return categorizedArticles;
    }

    private String determineCategory(String content) {
        Map<String, Integer> categoryScores = new HashMap<>();
        String[] tokens = tokenizeContent(content);

        // Calculate scores for each category
        for (String token : tokens) {
            for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
                if (entry.getValue().contains(token)) {
                    categoryScores.put(entry.getKey(), categoryScores.getOrDefault(entry.getKey(), 0) + 1);
                }
            }
        }

        // Determine the category with the highest score
        return categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Uncategorized");
    }

    private String[] tokenizeContent(String content) {
        String cleanedContent = content.toLowerCase().replaceAll("[^a-zA-Z ]", " ");
        return cleanedContent.split("\\s+");
    }
}

