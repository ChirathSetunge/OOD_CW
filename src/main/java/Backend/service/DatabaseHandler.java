package Backend.service;

import Backend.model.Article;
import Backend.model.UserAccount;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseHandler {
    private static final String DATABASE_NAME = "OOD_CW";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "chirath123";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
    private static DatabaseHandler dbInstance;
    private Connection connection;
    private final Map<String, UserAccount> activeSessions = new ConcurrentHashMap<>();


    private DatabaseHandler() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed");
            e.printStackTrace();
        }
    }

    public static DatabaseHandler getDbInstance() {
        if (dbInstance == null) {
            synchronized (DatabaseHandler.class) {
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
    public boolean isUsernameAvailable(String username) {
        String AvailableUsernameQuery = "SELECT count(1) FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(AvailableUsernameQuery)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean registerAccount(UserAccount userAccount) {
        if (!isUsernameAvailable(userAccount.getUsername())) {
            System.out.println("Username already exists.");
            return false;
        }
            String registerQuery = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(registerQuery)) {
                preparedStatement.setString(1, userAccount.getUsername());
                preparedStatement.setString(2, userAccount.getPassword());
                preparedStatement.setString(3, userAccount.getEmail());
                return preparedStatement.executeUpdate() > 0;//we use executeUpdate() instead of executeQuery() because we are inserting data
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

    }

    public boolean loginAccount(UserAccount userAccount){
            String loginQuery = "SELECT count(1) FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loginQuery)) {
                preparedStatement.setString(1, userAccount.getUsername());
                preparedStatement.setString(2, userAccount.getPassword());
                ResultSet resultSet = preparedStatement.executeQuery();//we use executeQuery() because we are retrieving data
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

    public UserAccount getActiveSession(String username) {
        return activeSessions.get(username);
    }

    public boolean savePreferences(String username, Map<String, Integer> preferences) {
        String savePreferencesQuery = "INSERT INTO user_preferences (username, category, score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE score = ?";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(savePreferencesQuery)) {
            for (Map.Entry<String, Integer> entry : preferences.entrySet()) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, entry.getKey());
                preparedStatement.setInt(3, entry.getValue());
                preparedStatement.setInt(4, entry.getValue());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Map<String, Integer> loadPreferences(String username) {
        String loadPreferencesQuery = "SELECT category, score FROM user_preferences WHERE username = ?";
        Map<String, Integer> preferences = new ConcurrentHashMap<>();
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loadPreferencesQuery)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                preferences.put(resultSet.getString("category"), resultSet.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferences;
    }
    public List<Map.Entry<String, String>> loadArticles() {
        String loadArticlesQuery = "SELECT * FROM articles";
        List<Map.Entry<String, String>> articles = new ArrayList<>();
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loadArticlesQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
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
    public void saveCategorizedArticles(List<Map<String, String>> categorizedArticles) {
        String insertCatArticlesQuery = "INSERT INTO CategorizedArticles (title, content, category) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = getDbInstance().getConnection().prepareStatement(insertCatArticlesQuery)) {
            for (Map<String, String> article : categorizedArticles) {
                preparedStatement.setString(1, article.get("title"));
                preparedStatement.setString(2, article.get("content"));
                preparedStatement.setString(3, article.get("category"));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch(); // Execute batch for better performance
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}