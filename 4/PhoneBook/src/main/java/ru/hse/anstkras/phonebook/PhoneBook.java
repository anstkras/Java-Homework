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

    private final Datastore datastore;

    public PhoneBook(@NotNull MongoClient mongoClient, @NotNull String name) {
        datastore = morphia.createDatastore(mongoClient, name);
        morphia.mapPackage("ru.hse.anstkras.phonebook.Entities");
        datastore.ensureIndexes();
    }

    public PhoneBook(@NotNull String name) {
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
    public PhoneNumber getOrCreateNumber(@NotNull String stringNumber) {
        PhoneNumber phoneNumber = getPhoneNumber(stringNumber);
        if (phoneNumber == null) {
            phoneNumber = new PhoneNumber(stringNumber);
            datastore.save(phoneNumber);
        }
        return phoneNumber;
    }

    @Nullable
    public PhoneNumber getPhoneNumber(@NotNull String stringNumber) {
        return datastore.createQuery(PhoneNumber.class).field("number").equal(stringNumber).get();
    }

    public boolean addEntry(@NotNull String name, @NotNull String stringNumber) {
        final User user = getOrCreateUser(name);
        final PhoneNumber phoneNumber = getOrCreateNumber(stringNumber);
        return addEntry(user, phoneNumber);
    }

    @Nullable
    public List<PhoneNumber> getPhoneNumbersByName(@NotNull String name) {
        final User user = getUser(name);
        if (user == null) {
            return null;
        }
        return user.getPhoneNumbers();
    }

    @Nullable
    public List<User> getNamesByPhoneNumber(@NotNull String stringNumber) {
        final PhoneNumber phoneNumber = getPhoneNumber(stringNumber);
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.getUsers();
    }

    public boolean deleteEntry(@NotNull String name, @NotNull String stringNumber) {
        final User user = getUser(name);
        final PhoneNumber phoneNumber = getPhoneNumber(stringNumber);
        if (user == null || phoneNumber == null) {
            return false;
        }
        return deleteEntry(user, phoneNumber);
    }

    @NotNull
    public List<Entry> getAllPairs() {
        final List<User> users = datastore.createQuery(User.class).asList();
        return users.stream().flatMap(user -> user.getPhoneNumbers().stream().
                map(number -> new Entry(user, number))).collect(Collectors.toList());
    }

    public boolean changeName(@NotNull String oldName, @NotNull String stringNumber, @NotNull String newName) {
        User oldUser = getUser(oldName);
        PhoneNumber phoneNumber = getPhoneNumber(stringNumber);
        if (oldUser == null || phoneNumber == null) {
            return false;
        }
        if (!deleteEntry(oldUser, phoneNumber)) {
            return false;
        }
        User newUser = getOrCreateUser(newName);
        return addEntry(newUser, phoneNumber);
    }

    public boolean changeNumber(@NotNull String oldNumber, @NotNull String name, @NotNull String newNumber) {
        PhoneNumber oldPhoneNumber = getPhoneNumber(oldNumber);
        User user = getUser(name);
        if (oldPhoneNumber == null || user == null) {
            return false;
        }
        if (!deleteEntry(user, oldPhoneNumber)) {
            return false;
        }
        PhoneNumber newPhoneNumber = getOrCreateNumber(newNumber);
        return addEntry(user, newPhoneNumber);
    }

    private boolean checkEntryExists(@Nullable User user, @Nullable PhoneNumber phoneNumber) {
        return user != null && phoneNumber != null && user.getPhoneNumbers().contains(phoneNumber);
    }

    private boolean addEntry(@NotNull User user, @NotNull PhoneNumber phoneNumber) {
        if (user.getPhoneNumbers().contains(phoneNumber)) {
            return false;
        }
        user.addPhoneNumber(phoneNumber);
        phoneNumber.addUser(user);
        datastore.save(user);
        datastore.save(phoneNumber);
        return true;
    }

    private boolean deleteEntry(@NotNull User user, @NotNull PhoneNumber phoneNumber) {
        if (!checkEntryExists(user, phoneNumber)) {
            return false;
        }
        user.removePhoneNumber(phoneNumber);
        datastore.save(user);
        phoneNumber.removeUser(user);
        datastore.save(phoneNumber);

        if (user.hasNoPhoneNumbers()) {
            datastore.delete(user);
        }
        if (phoneNumber.hasNoUsers()) {
            datastore.delete(phoneNumber);
        }
        return true;
    }

    public static class Entry {
        private final User user;
        private final PhoneNumber phoneNumber;

        public Entry(@NotNull User user, @NotNull PhoneNumber phoneNumber) {
            this.user = user;
            this.phoneNumber = phoneNumber;
        }

        @NotNull
        public User getUser() {
            return user;
        }

        @NotNull
        public PhoneNumber getPhoneNumber() {
            return phoneNumber;
        }

        @NotNull
        public String getUserName() {
            return user.getName();
        }

        @NotNull
        public String getNumber() {
            return phoneNumber.getNumber();
        }
    }
}
