package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends Portfolio {
    @Expose
    private int userId;
    @Expose
    private Role role;
    @Expose
    private String username;
    @Expose
    private String email;
    @Expose
    private String PasswordHash;

    private List<Portfolio> portfolios = new ArrayList<>();

    public User() {
    }

    public User(int userId, List<Portfolio> portfolios, String password_hash, String email, String username, Role role) {
        this.userId = userId;
        this.portfolios = portfolios;
        PasswordHash = password_hash;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.PasswordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", PasswordHash='" + PasswordHash + '\'' +
                '}';
    }
}
