package condorcet.Models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name="person_data")
public class PersonData {
    private int Id;
    private int Age;
    private String Mail;
    private String Address;
    private String Sex;
    private Set<User> Users = new HashSet<>();
    private Set<Passenger> Passengers = new HashSet<>();
    public PersonData(){

    }
    public PersonData(int id, int age, String mail, String address, String sex, Set<User> users, Set<Passenger> passengers) {
        Id = id;
        Age = age;
        Mail = mail;
        Address = address;
        Sex = sex;
        Users = users;
        Passengers = passengers;
    }
    @Column(name="sex",length = 45)
    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }
    @Column(name="address",length = 45)

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
    @Column(name="mail",length = 45)

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }
    @Column(name="age")

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "personData")
    public Set<Passenger> getPassengers() {
        return Passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        Passengers = passengers;
    }
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "personData")
    public Set<User> getUsers() {
        return Users;
    }

    public void setUsers(Set<User> users) {
        Users = users;
    }
}
