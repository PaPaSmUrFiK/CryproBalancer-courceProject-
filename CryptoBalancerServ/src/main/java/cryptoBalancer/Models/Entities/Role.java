package cryptoBalancer.Models.Entities;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "userrole", schema = "crypto_balancer")
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    @Expose
    private int roleId;

    @Column(name = "role_name", nullable = false)
    @Expose
    private String roleName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    private Set<User> users = new HashSet<>();

    public Role() {
    }

    public Role(int roleId, Set<User> users, String roleName) {
        this.roleId = roleId;
        this.users = users;
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
