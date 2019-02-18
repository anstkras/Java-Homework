package ru.hse.anstkras.phonebook;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class PhoneBook {
    private final Morphia morphia = new Morphia();

    private Datastore datastore;

    public PhoneBook(MongoClient mongoClient, String name) {
        datastore = morphia.createDatastore(mongoClient, name);
        morphia.mapPackage("ru.hse.anstkras.phonebook.Entities");
        datastore.ensureIndexes();
    }

    public PhoneBook(String name) {
        this(new MongoClient(), name);
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

    public boolean addEntry(@NotNull String name, @NotNull String phoneNumber) {
        User user = getOrCreateUser(name);
        PhoneNumber number = getOrCreateNumber(phoneNumber);
        if (user.getPhoneNumbers().contains(number)) {
            return false;
        }
        user.getPhoneNumbers().add(number);
        number.getUsers().add(user);
        datastore.save(user);
        datastore.save(number);
        return true;
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
        if (!checkEntryExists(user, phoneNumber)) {
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
    public List<Entry> getAllPairs() {
        List<User> users = datastore.createQuery(User.class).asList();
        return users.stream().flatMap(user -> user.getPhoneNumbers().stream().
                map(number -> new Entry(user, number))).collect(Collectors.toList());
    }

    public boolean changeName(@NotNull String oldName, @NotNull String number, @NotNull String newName) {
        User oldUser = getUser(oldName);
        PhoneNumber phoneNumber = getPhoneNumber(number);
        if (!checkEntryExists(oldUser, phoneNumber)) {
            return false;
        }
        oldUser.getPhoneNumbers().remove(phoneNumber);
        datastore.save(oldUser);
        if (oldUser.getPhoneNumbers().isEmpty()) {
            datastore.delete(oldUser);
        }
        User newUser = getOrCreateUser(newName);
        phoneNumber.getUsers().remove(oldUser);
        phoneNumber.getUsers().add(newUser);
        datastore.save(phoneNumber);
        newUser.getPhoneNumbers().add(phoneNumber);
        datastore.save(newUser);
        return true;
    }

    public boolean changeNumber(@NotNull String oldNumber, @NotNull String name, @NotNull String newNumber) {
        PhoneNumber oldPhoneNumber = getPhoneNumber(oldNumber);
        User user = getUser(name);
        if (!checkEntryExists(user, oldPhoneNumber)) {
            return false;
        }
        oldPhoneNumber.getUsers().remove(user);
        datastore.save(oldPhoneNumber);
        if (oldPhoneNumber.getUsers().isEmpty()) {
            datastore.delete(oldPhoneNumber);
        }
        PhoneNumber newPhoneNumber = getOrCreateNumber(newNumber);
        user.getPhoneNumbers().remove(oldPhoneNumber);
        user.getPhoneNumbers().add(newPhoneNumber);
        datastore.save(user);
        newPhoneNumber.getUsers().add(user);
        datastore.save(newPhoneNumber);
        return true;
    }

    private boolean checkEntryExists(@Nullable User user, @Nullable PhoneNumber phoneNumber) {
        return user != null && phoneNumber != null && user.getPhoneNumbers().contains(phoneNumber);
    }

    public static class Entry {
        private User user;
        private PhoneNumber phoneNumber;


        public Entry(User user, PhoneNumber phoneNumber) {
            this.user = user;
            this.phoneNumber = phoneNumber;
        }

        public User getUser() {
            return user;
        }

        public PhoneNumber getPhoneNumber() {
            return phoneNumber;
        }

        public String getUserName() {
            return user.getName();
        }

        public String getNumber() {
            return phoneNumber.getNumber();
        }
    }
}
