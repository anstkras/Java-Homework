package ru.hse.anstkras.phonebook.Entities;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Reference
    private final List<PhoneNumber> phoneNumbers = new ArrayList<>();
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;

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

    public void addPhoneNumber(@NotNull PhoneNumber phoneNumber) {
        phoneNumbers.add(phoneNumber);
    }

    public void removePhoneNumber(@NotNull PhoneNumber phoneNumber) {
        phoneNumbers.remove(phoneNumber);
    }

    public boolean hasNoPhoneNumbers() {
        return phoneNumbers.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }

        var otherUser = (User) object;
        return otherUser.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
