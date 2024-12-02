package Backend.util;

import java.util.*;

public class ArticleCategorizer {

    // Category keywords for article classification
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        CATEGORY_KEYWORDS.put("Technology", Arrays.asList("computer", "software", "hardware", "internet", "ai",
                "programming", "cybersecurity", "blockchain", "network", "data",
                "virtual", "cloud", "storage", "coding", "automation",
                "electronics", "smartphone", "gadget", "innovation", "developer",
                "web", "application", "system", "algorithm", "interface",
                "sensor", "chip", "design", "encryption", "database"));
        CATEGORY_KEYWORDS.put("Health", Arrays.asList("medicine", "doctor", "hospital", "disease", "treatment",
                "nurse", "clinic", "surgery", "wellness", "healthcare",
                "diagnosis", "therapy", "vaccine", "nutrition", "exercise",
                "pharmacy", "infection", "symptoms", "recovery", "prevention",
                "mental", "fitness", "patient", "immune", "diet",
                "epidemic", "pandemic", "hygiene", "biotechnology", "telemedicine"));
        CATEGORY_KEYWORDS.put("Politics", Arrays.asList("government", "policy", "election", "legislation", "democracy",
                "parliament", "senate", "congress", "constitution", "diplomacy",
                "campaign", "voting", "minister", "cabinet", "governance",
                "bureaucracy", "judiciary", "administration", "reform", "justice",
                "rights", "freedom", "representation", "politician", "debate",
                "coalition", "party", "leader", "international", "treaty"));
        CATEGORY_KEYWORDS.put("Sports", Arrays.asList("football", "basketball", "tennis", "athlete", "game",
                "soccer", "baseball", "cricket", "swimming", "running",
                "cycling", "hockey", "golf", "volleyball", "training",
                "stadium", "league", "match", "tournament", "coach",
                "referee", "score", "team", "championship", "medal",
                "fitness", "competition", "record", "goal", "fans"));
        CATEGORY_KEYWORDS.put("Finance", Arrays.asList("money", "investment", "economy", "banking", "stock",
                "market", "budget", "capital", "trade", "currency",
                "loan", "interest", "taxation", "revenue", "debt",
                "savings", "insurance", "pension", "audit", "profit",
                "loss", "wealth", "portfolio", "dividend", "inflation",
                "credit", "mortgage", "asset", "fund", "venture"));
    }

    public List<Map<String, String>> categorizeArticles(List<Map.Entry<String, String>> articles) {
        List<Map<String, String>> categorizedArticles = new ArrayList<>();
        System.out.println("Uncategorized articles size of " + articles.size() + "received for categorization");

        for (Map.Entry<String, String> article : articles) {
            String title = article.getKey();
            String content = article.getValue();
            // Determine the category of the article
            String category = determineCategory(content);

            Map<String, String> categorizedArticle = new HashMap<>();
            categorizedArticle.put("title", title);
            categorizedArticle.put("content", content);
            categorizedArticle.put("category", category);

            categorizedArticles.add(categorizedArticle);
        }
        System.out.println("Categorized articles size of " + categorizedArticles.size() + " generated");
        return categorizedArticles;
    }

    private String determineCategory(String content) {
        Map<String, Integer> categoryScores = new HashMap<>();
        // Tokenize the content
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
        // Clean the content by removing non-alphabetic characters and converting to lowercase
        String cleanedContent = content.toLowerCase().replaceAll("[^a-zA-Z ]", " ");
        // Tokenize the content by splitting on whitespace
        return cleanedContent.split("\\s+");
    }
}

