package main.Models.Entities;

import main.Enums.Roles;

import java.util.HashSet;
import java.util.Set;

public class User {
    private int Id;
    private String Name;
    private String Login;
    private String Password;
    private String Role;
    private PersonData personData;
    private Set<UserMark> UserMarks = new HashSet<>();
    public User(){

    }
    public User(int id, String name, String login, String password, String role, PersonData personData, Set<UserMark> userMarks) {
        Id = id;
        Name = name;
        Login = login;
        Password = password;
        Role = role;
        this.personData = personData;
        UserMarks = userMarks;
    }

    public PersonData getPersonData() {
        return personData;
    }

    public void setPersonData(PersonData personData) {
        this.personData = personData;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(Roles role) {
        Role = role.toString();
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Set<UserMark> getUserMarks() {
        return UserMarks;
    }

    public void setUserMarks(Set<UserMark> userMarks) {
        UserMarks = userMarks;
    }
}
