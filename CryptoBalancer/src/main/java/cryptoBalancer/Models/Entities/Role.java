package cryptoBalancer.Models.Entities;

import java.util.HashSet;
import java.util.Set;

public class Role extends User {
    private int roleId;

    private String roleName;

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
}
