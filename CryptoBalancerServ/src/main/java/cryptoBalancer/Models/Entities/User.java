package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "appuser", schema = "crypto")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @Expose
    private Role role;

    @Column(name = "username", nullable = false, unique = true)
    @Expose
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @Expose
    private String email;

    @Column(name = "password_hash", nullable = false)
    @Expose
    private String PasswordHash;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
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
                ", role=" + role +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", PasswordHash='" + PasswordHash + '\'' +
                ", portfolios=" + portfolios +
                '}';
    }
}

