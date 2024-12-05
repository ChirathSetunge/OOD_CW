package Backend.service;
// Import thr SimpleRegression class from the Apache Commons Math library
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class RecommendationEngine {
    private final SimpleRegression regression;
    public RecommendationEngine() {
        this.regression = new SimpleRegression();
    }
    public void trainFromCSV() {
        // Load user preferences from a CSV file
        String csvFilePath = "user_preferences.csv";
        // Read the CSV file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { // Skip header row
                    isHeader = false;
                    continue;
                }
                // Split the line by commas
                String[] values = line.split(",");
                String user = values[0]; // User identifier
                double totalPreferenceScore = 0.0;

                // Compute a combined preference score for training
                for (int i = 1; i < values.length; i++) {
                    totalPreferenceScore += Double.parseDouble(values[i]);
                }
                // Add the user's total preference score to the regression model
                regression.addData(Double.parseDouble(values[1]), totalPreferenceScore);
            }

            System.out.println("Model trained with CSV data!");
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }
    public String[] predictTopCategories(Map<String, Integer> userPreferences) {
        // Predict the top 2 categories based on user preferences
        return userPreferences.entrySet()
                .stream()
                // Sort the entries based on the predicted values in descending order
                .sorted((a, b) -> {
                    double predictionA = regression.predict(a.getValue());
                    double predictionB = regression.predict(b.getValue());
                    return Double.compare(predictionB, predictionA);
                })
                // Limit the result to the top 2 entries
                .limit(2)
                // Map the entries back to the category names
                .map(Map.Entry::getKey)
                // Collect the category names into an array
                .toArray(String[]::new);
    }
}
