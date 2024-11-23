package Backend.model;

public class Account {
    private String username;
    private String password;
    private String email;
    private String name;
    private String surname;

    public Account(String username, String password, String email, String name, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
