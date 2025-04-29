package main.Models.Entities;

import java.util.HashSet;
import java.util.Set;

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

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Set<Passenger> getPassengers() {
        return Passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        Passengers = passengers;
    }

    public Set<User> getUsers() {
        return Users;
    }

    public void setUsers(Set<User> users) {
        Users = users;
    }
}
