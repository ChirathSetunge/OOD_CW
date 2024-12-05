package Backend.model;

import Backend.service.DatabaseHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class UserPreferences {
    // Map of Category objects to their respective scores (composition)
    private final Map<Category, Integer> categoryScores; //composition because UserPreferences has a map of Category objects
    // Default categories used to initialize the categoryScores map
    private static final Category[] DEFAULT_CATEGORIES = {
            new Category("Technology"), new Category("Health"), new Category("Politics"),
            new Category("Sports"), new Category("Finance")
    };
    public UserPreferences() {
        this.categoryScores = new ConcurrentHashMap<>();
        // Initialize categoryScores with default categories and a score of 0
        for (Category category : DEFAULT_CATEGORIES) {
            categoryScores.put(category, 0);
        }
    }
    public void updatePreference(Category category, int scoreChange) {
        // Update the score of the category by adding the scoreChange
        categoryScores.put(category, categoryScores.getOrDefault(category, 0) + scoreChange);
    }
    public void savePreferencesToDB(DatabaseHandler databaseHandler, String username) {
        // Save the user preferences to the database
        Map<String, Integer> preferences = new ConcurrentHashMap<>();
        // Convert Category objects to their names for database storage
        for (Map.Entry<Category, Integer> entry : categoryScores.entrySet()) {
            preferences.put(getCategoryName(entry.getKey()), entry.getValue());
        }
        System.out.println("Saving preferences to DB: " + preferences);
        databaseHandler.savePreferencesAsync(username, preferences);
    }
    public void loadPreferencesFromDB(DatabaseHandler databaseHandler, String username) {
        // Load the category scores from the database
        try {
            Map<String, Integer> preferences = databaseHandler.loadPreferencesAsync(username).get();
            // Convert category names from the database back to Category objects
            for (Category category : DEFAULT_CATEGORIES) {
                categoryScores.put(getCategoryByName(category.getName()), preferences.getOrDefault(category.getName(), 0));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public Map<String, Integer> getAllPreferences() {
        // Get all preferences as a map of category names to scores
        Map<String, Integer> preferences = new ConcurrentHashMap<>();
        for (Map.Entry<Category, Integer> entry : categoryScores.entrySet()) {
            preferences.put(entry.getKey().getName(), entry.getValue());
        }
        return preferences;
    }
    public void resetPreferences() {
        // Reset the category scores to 0
        for (Category category : DEFAULT_CATEGORIES) {
            categoryScores.put(category, 0);
        }
    }
    public Category getCategoryByName(String name) {
        // Get the category object by name because the category objects are stored in the database as strings
        return new Category(name);
    }

    public String getCategoryName(Category category) {
        // Get the name of the category object because the category objects are stored in the database as strings
        return category.getName();
    }
}
