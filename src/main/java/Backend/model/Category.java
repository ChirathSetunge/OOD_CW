package Backend.model;

import java.util.Objects;

public class Category {
    //Represents a category to which articles can belong.
    private String name;
    public Category(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    // Override equals method to compare Category objects based on their name
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return Objects.equals(name, category.name);
    }
    // Override hashCode method to generate a hash code based on the name of the Category object
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    // Override toString method to return the name of the Category object
    @Override
    public String toString() {
        return name;
    }

}
