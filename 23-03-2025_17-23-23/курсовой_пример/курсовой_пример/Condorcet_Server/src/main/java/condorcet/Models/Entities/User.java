package condorcet.Models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name="user")
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
    public User(int id, String name, String login, String password, String role, condorcet.Models.Entities.PersonData personData, Set<UserMark> userMarks) {
        Id = id;
        Name = name;
        Login = login;
        Password = password;
        Role = role;
        this.personData = personData;
        UserMarks = userMarks;
    }
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="person_data_id")
    public condorcet.Models.Entities.PersonData getPersonData() {
        return personData;
    }

    public void setPersonData(condorcet.Models.Entities.PersonData personData) {
        this.personData = personData;
    }
    @Column(name="role",length = 45)
    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
    @Column(name="password",length = 45)

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
    @Column(name="login",length = 45)

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }
    @Column(name="name",length = 45)

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "user")
    public Set<UserMark> getUserMarks() {
        return UserMarks;
    }

    public void setUserMarks(Set<UserMark> userMarks) {
        UserMarks = userMarks;
    }
}
