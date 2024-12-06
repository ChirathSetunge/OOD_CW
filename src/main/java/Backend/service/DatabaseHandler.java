package Backend.service;

import Backend.model.Article;
import Backend.model.Category;
import Backend.model.UserAccount;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:OOD_CW_Database.sqlite";
    private static DatabaseHandler dbInstance;
    private Connection connection;
    private final Map<String, UserAccount> activeSessions = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);


    private DatabaseHandler() {
        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed");
            e.printStackTrace();
        }
    }

    public static DatabaseHandler getDbInstance() {
        // Singleton pattern
        if (dbInstance == null) {// Check if the instance has been created
            synchronized (DatabaseHandler.class) {
                // Synchronize the block to prevent multiple threads from creating multiple instances
                if (dbInstance == null) {
                    dbInstance = new DatabaseHandler();
                }
            }
        }
        return dbInstance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
            System.out.println("Database connection closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean isUsernameAvailable(String username) {
        // Check if the username is available
        String AvailableUsernameQuery = "SELECT count(1) FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(AvailableUsernameQuery)) {
            preparedStatement.setString(1, username);
            //executeQuery() because we are retrieving data
            ResultSet resultSet = preparedStatement.executeQuery();
            // If the username is available, the count will be 0
            return resultSet.next() && resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Future<Boolean> registerAccountAsync(UserAccount userAccount) {
        // Register the user account asynchronously
        return executor.submit(() -> registerAccount(userAccount));
    }
    private boolean registerAccount(UserAccount userAccount) {
        // Register the user account
        if (!isUsernameAvailable(userAccount.getUsername())) {
            System.out.println("(DB) Username already exists.");
            return false;
        }
        // Insert the user account details into the database
        String registerQuery = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(registerQuery)) {
            preparedStatement.setString(1, userAccount.getUsername());
            preparedStatement.setString(2, userAccount.getPassword());
            preparedStatement.setString(3, userAccount.getEmail());
            //executeUpdate() because we are inserting data
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public Future<Boolean> loginAccountAsync(UserAccount userAccount) {
        // Login the user account asynchronously
        return executor.submit(() -> loginAccount(userAccount));
    }
    private boolean loginAccount(UserAccount userAccount){
        // Login the user account
        System.out.println("(DB) Logging in user: " + userAccount.getUsername());
        System.out.println("(DB) Active sessions: " + activeSessions);
        if (activeSessions.containsKey(userAccount.getUsername())) {
            System.out.println("User already has an active session: " + userAccount.getUsername() + " Inform the user to logout from the other device.");
            return false;
        }
        // Check if the username and password match
        String loginQuery = "SELECT count(1) FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loginQuery)) {
            preparedStatement.setString(1, userAccount.getUsername());
            preparedStatement.setString(2, userAccount.getPassword());
            //executeQuery() because we are retrieving data
            ResultSet resultSet = preparedStatement.executeQuery();
            // If the username and password match, the count will be 1
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                addActiveSession(userAccount);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public void addActiveSession(UserAccount userAccount) {
        activeSessions.put(userAccount.getUsername(), userAccount);
    }

    public void removeActiveSession(String username) {
        activeSessions.remove(username);
    }

public void savePreferencesAsync(String username, Map<String, Integer> preferences) {
        // Save the user preferences asynchronously
    executor.submit(() -> {
        savePreferences(username, preferences);
        return null;
    });
}
    public void savePreferences(String username, Map<String, Integer> preferences) {
        // Save the user preferences
        String savePreferencesQuery = "INSERT INTO UserPreferences (username, category, score) VALUES (?, ?, ?) ON CONFLICT(username, category) DO UPDATE SET score = ?";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(savePreferencesQuery)) {
            for (Map.Entry<String, Integer> entry : preferences.entrySet()) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, entry.getKey());
                preparedStatement.setInt(3, entry.getValue());
                preparedStatement.setInt(4, entry.getValue());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            System.out.println("Preferences saved successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Future<Map<String, Integer>> loadPreferencesAsync(String username) {
        // Load the user preferences asynchronously
        return executor.submit(() -> loadPreferences(username));
    }
    public Map<String, Integer> loadPreferences(String username) {
        // Load the user preferences
        String loadPreferencesQuery = "SELECT category, score FROM UserPreferences WHERE username = ?";
        Map<String, Integer> preferences = new ConcurrentHashMap<>();
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loadPreferencesQuery)) {
            preparedStatement.setString(1, username);
            //executeQuery() because we are retrieving data
            ResultSet resultSet = preparedStatement.executeQuery();
            // Iterate through the result set and add the preferences to the map
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                int score = resultSet.getInt("score");
                preferences.put(category, score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferences;
    }
    public Future<List<Map.Entry<String, String>>> loadArticlesAsync() {
        // Load the articles asynchronously
        return executor.submit(() -> loadArticles());
        }
    public List<Map.Entry<String, String>> loadArticles() {
        // Load the uncategorized articles from the database
        String loadArticlesQuery = "SELECT * FROM articles";
        List<Map.Entry<String, String>> articles = new ArrayList<>();
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loadArticlesQuery)) {
            //executeQuery() because we are retrieving data
            ResultSet resultSet = preparedStatement.executeQuery();
            // Iterate through the result set and add the articles to the list
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                articles.add(new AbstractMap.SimpleEntry<>(title, content));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }
    public void clearCategorizedArticlesTable() {
        // Clear the CategorizedArticles table, for admin to add new articles
        String clearTableQuery = "DELETE FROM CategorizedArticles";
        try (PreparedStatement preparedStatement = getDbInstance().getConnection().prepareStatement(clearTableQuery)) {
            //executeUpdate() because we are deleting data
            preparedStatement.executeUpdate();
            System.out.println("CategorizedArticles table cleared successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveCategorizedArticlesAsync(List<Map<String, String>> categorizedArticles) {
        // Save the categorized articles asynchronously
        executor.submit(() -> {
            saveCategorizedArticles(categorizedArticles);
            return null;
        });
    }
    public void saveCategorizedArticles(List<Map<String, String>> categorizedArticles) {
        // Save the categorized articles
        String insertCatArticlesQuery = "INSERT INTO CategorizedArticles (title, content, category) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = getDbInstance().getConnection().prepareStatement(insertCatArticlesQuery)) {
            for (Map<String, String> article : categorizedArticles) {
                preparedStatement.setString(1, article.get("title"));
                preparedStatement.setString(2, article.get("content"));
                preparedStatement.setString(3, article.get("category"));
                // Add the batch to the prepared statement
                preparedStatement.addBatch();
            }
            // Execute the batch for better performance by bulk inserting
            preparedStatement.executeBatch(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Future<Article> getArticlesFromDbAsync(String category) {
        // Get the articles from the database asynchronously
        return executor.submit(() -> getArticlesFromDb(category));
    }
    public Article getArticlesFromDb(String category) {
        // Get the articles from the database (categorized)
        Article article = null;
        String GetCategorizedArticleQuery = "SELECT title, content, category FROM CategorizedArticles WHERE category = ? ORDER BY RANDOM() LIMIT 1";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(GetCategorizedArticleQuery)) {
           preparedStatement.setString(1, category);
           //executeQuery() because we are retrieving data
           ResultSet resultSet = preparedStatement.executeQuery();
           // Iterate through the result set and create an Article object
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                String categoryName = resultSet.getString("category");
                // Create a Category object to assign to the Article object
                Category categoryObj = new Category(categoryName);
                // Create an Article object
                article = new Article(title, content, categoryObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return article;
    }
}