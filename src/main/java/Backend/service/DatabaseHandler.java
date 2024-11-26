package Backend.service;

import Backend.model.UserAccount;

import java.sql.*;

public class DatabaseHandler {
    private static final String DATABASE_NAME = "OOD_CW";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "chirath123";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
    private static DatabaseHandler dbInstance;
    private Connection connection;

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

    public boolean registerAccount(UserAccount userAccount) {
        String registerQuery = "INSERT INTO accounts (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(registerQuery)) {
            preparedStatement.setString(1, userAccount.getUsername());
            preparedStatement.setString(2, userAccount.getPassword());
            preparedStatement.setString(3, userAccount.getEmail());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean loginAccount(UserAccount userAccount) {
        String loginQuery = "SELECT count(1) FROM accounts WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = DatabaseHandler.getDbInstance().getConnection().prepareStatement(loginQuery)) {
            preparedStatement.setString(1, userAccount.getUsername());
            preparedStatement.setString(2, userAccount.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 1;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}