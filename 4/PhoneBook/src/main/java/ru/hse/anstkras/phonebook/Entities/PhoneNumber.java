package ru.hse.anstkras.phonebook.Entities;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PhoneNumber {
    @Id
    private long id;
    private String number;
    @Reference
    private List<User> users = new ArrayList<>();

    public PhoneNumber(String number) {
        this.number = number;
    }

    public PhoneNumber() {

    }

    public List<User> getUsers() {
        return users;
    }

    public String getNumber() {
        return number;
    }
}
