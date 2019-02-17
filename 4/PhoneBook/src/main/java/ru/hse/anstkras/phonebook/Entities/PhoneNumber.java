package ru.hse.anstkras.phonebook.Entities;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PhoneNumber {
    @Id
    private ObjectId id;
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

    @Override
    public boolean equals(Object object) {
        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }

        var otherPhoneNumber = (PhoneNumber) object;
        return otherPhoneNumber.number.equals(number);
    }
}
