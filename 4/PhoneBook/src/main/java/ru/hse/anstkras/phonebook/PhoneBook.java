package ru.hse.anstkras.phonebook;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PhoneBook {
    private final Morphia morphia = new Morphia();

    private Datastore datastore;

    public PhoneBook(String name) {
        datastore = morphia.createDatastore(new MongoClient(), name);
        morphia.mapPackage("ru.hse.anstkras.phonebook.Entities");
    }

    /**
     * if user with the given name exists in the database then returns this user
     * otherwise create a new one and save it to database
     *
     * @return existing user or a created one
     */
    @NotNull
    public User getOrCreateUser(@NotNull String name) {
        User user = getUser(name);
        if (user == null) {
            user = new User(name);
            datastore.save(user);

        }
        return user;
    }

    @Nullable
    public User getUser(@NotNull String name) {
        return datastore.createQuery(User.class).field("name").equal(name).get();
    }

    @NotNull
    public PhoneNumber getOrCreateNumber(@NotNull String phoneNumber) {
        PhoneNumber number = getPhoneNumber(phoneNumber);
        if (number == null) {
            number = new PhoneNumber(phoneNumber);
            datastore.save(number);
        }
        return number;
    }

    @Nullable
    public PhoneNumber getPhoneNumber(@NotNull String phoneNumber) {
        return datastore.createQuery(PhoneNumber.class).field("number").equal(phoneNumber).get();
    }

    public void addEntry(@NotNull String name, @NotNull String phoneNumber) {
        User user = getOrCreateUser(name);
        PhoneNumber number = getOrCreateNumber(phoneNumber);
        user.getPhoneNumbers().add(number);
        number.getUsers().add(user);
        datastore.save(user);
        datastore.save(number);
    }

    @Nullable
    public List<PhoneNumber> getPhoneNumbersByName(@NotNull String name) {
        User user = getUser(name);
        if (user == null) {
            return null;
        }

        return user.getPhoneNumbers();
    }

    @Nullable
    public List<User> getNamesByPhoneNumber(@NotNull String number) {
        PhoneNumber phoneNumber = getPhoneNumber(number);
        if (phoneNumber == null) {
            return null;
        }

        return phoneNumber.getUsers();
    }

    public boolean deleteEntry(@NotNull String name, @NotNull String number) {
        User user = getUser(name);
        PhoneNumber phoneNumber = getPhoneNumber(number);
        if (user == null || phoneNumber == null || !user.getPhoneNumbers().contains(phoneNumber)) {
            return false;
        }

        if (!phoneNumber.getUsers().contains(user)) {
            throw new IllegalStateException();
        }
        user.getPhoneNumbers().remove(phoneNumber);
        datastore.save(user);
        phoneNumber.getUsers().remove(user);
        datastore.save(phoneNumber);

        if (user.getPhoneNumbers().isEmpty()) {
            datastore.delete(user);
        }
        if (phoneNumber.getUsers().isEmpty()) {
            datastore.delete(phoneNumber);
        }
        return true;
    }

    @NotNull
    public Map<User, List<PhoneNumber>> getAllPairs() {
        List<User> users = datastore.createQuery(User.class).asList();
        return users.stream().collect(Collectors.toMap(Function.identity(), User::getPhoneNumbers));
    }
}
