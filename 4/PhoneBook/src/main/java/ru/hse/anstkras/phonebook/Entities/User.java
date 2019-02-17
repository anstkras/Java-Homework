package ru.hse.anstkras.phonebook.Entities;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    private long id;
    private String name;

    @Reference
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }

        var otherUser = (User) object;
        return otherUser.name.equals(name);
    }
}
