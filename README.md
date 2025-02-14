# MY NEWS Article Recommendation System

## Overview
**MY NEWS** Article Recommendation System is an intelligent news recommendation platform built with **Java** and **JavaFX**, designed to enhance user engagement by providing personalized article recommendations. Leveraging **machine learning**, the system continuously learns from user interactions—articles read, liked, or skipped—to deliver the most relevant content dynamically.

This project was developed as part of my CM2601: Object-Oriented Development Module and was successfully completed with a Distinction (A) Pass.

### Key Highlights:
- AI-Powered Recommendations: Uses a machine learning model to suggest articles based on user preferences and reading history.
- Strong Object-Oriented Design: Developed with solid OOP principles (inheritance, encapsulation, and polymorphism) for scalability and maintainability.
- Interactive JavaFX UI: A sleek and responsive JavaFX-based frontend seamlessly connected to the backend.
- Multi-User Support & Concurrency: Efficiently handles multiple user interactions simultaneously using Java’s concurrency utilities.
- Database-Backed Storage: Utilizes an SQLite database to store user data, preferences, and article details.
- Robust Exception Handling: Built-in mechanisms for handling file operations, invalid inputs to ensure smooth operation.

### System Architecture & Design:
This project was carefully designed following a structured development approach:

**UML Diagrams for Clear Design & Planning:**
- Use Case Diagram: Defines user roles and system functionalities (Login, View Articles, Rate Articles, Get Recommendations, etc.).
- Activity Diagram: Illustrates how the system processes user actions, including article categorization and recommendations.
- Class Diagram: Showcases key components like User, Article, RecommendationEngine, DatabaseHandler, demonstrating strong OOP design.
- Sequence Diagram: Details interactions between components like User, ML Model, DatabaseHandler, ensuring smooth workflow.

With a well-structured backend, robust database handling, and an integrated AI-powered recommendation engine, this project demonstrates the end-to-end development of an intelligent news platform—from design to implementation.

## Prerequisites
- JDK 17 or higher
- JavaFX SDK 17 or higher
- Maven 3.6 or higher

### Setup Instructions

1. **Clone the repository:**
   git clone <https://github.com/ChirathSetunge/OOD_CW.git>
   cd <repository-directory> your location

2. or unzip project file

3. Ensure your pom.xml includes the following dependencies for JavaFX: 
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17</version>
    </dependency>
</dependencies>

4. The project uses an SQLite database (OOD_CW_Database.sqlite). no need to setup sql servers

5. Build the project:  
	mvn clean install

6. Run the application:  
	mvn javafx:run -f pom.xml

7. Or open the project from your prefered IDE and run (inteliJ)
