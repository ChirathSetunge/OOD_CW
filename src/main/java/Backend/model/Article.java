package Backend.model;

public class Article {
    // Represents an article that belongs to a category.
    private String title;
    private String content;
    private Category category; // Composition relationship with Category

    public Article (String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public Category getCategory() {
        return category;
    }

}
