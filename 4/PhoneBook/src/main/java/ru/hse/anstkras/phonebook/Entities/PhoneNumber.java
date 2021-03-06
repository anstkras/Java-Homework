package ru.hse.anstkras.phonebook.Entities;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

/** Represents a phone number Morphia entity */
@Entity
public class PhoneNumber {
    @Reference
    private final List<User> users = new ArrayList<>();
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String number;

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

    public void addUser(@NotNull User user) {
        users.add(user);
    }

    public void removeUser(@NotNull User user) {
        users.remove(user);
    }

    public boolean hasNoUsers() {
        return users.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }

        var otherPhoneNumber = (PhoneNumber) object;
        return otherPhoneNumber.number.equals(number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }
}
