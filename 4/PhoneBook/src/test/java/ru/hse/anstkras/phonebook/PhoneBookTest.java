package ru.hse.anstkras.phonebook;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    private final List<String> names = Arrays.asList("name1", "name2", "name3", "name4");
    private final List<String> numbers = Arrays.asList("number1", "number2", "number3", "number4");
    private PhoneBook phoneBook;

    @BeforeEach
    void setup() {
        Fongo fongo = new Fongo("fongo_server");
        MongoClient mongoClient = fongo.getMongo();
        phoneBook = new PhoneBook(mongoClient, "test_phone_book");
    }

    @Test
    void addEntry() {
        final String name = "name";
        final String number = "number";
        phoneBook.addEntry(name, number);
        List<PhoneBook.Entry> allPairs = phoneBook.getAllPairs();
        assertEquals(1, allPairs.size());
        assertEquals(name, allPairs.get(0).getUserName());
        assertEquals(number, allPairs.get(0).getNumber());
    }

    private void fillWithPhoneNumbers(@NotNull String name, @NotNull List<String> phoneNumbers) {
        phoneNumbers.forEach(phoneNumber -> phoneBook.addEntry(name, phoneNumber));
    }

    @Test
    void getPhoneNumbersByName() {
        final List<String> phoneNumbers = Arrays.asList("123", "32177", "7868132", "3123");
        final String name = "name";
        fillWithPhoneNumbers(name, phoneNumbers);
        phoneBook.addEntry("qwerty", "8-737-12");
        phoneBook.addEntry("zxc", "5352435");
        final List<PhoneNumber> phoneNumberList = phoneBook.getPhoneNumbersByName(name);
        assertNotNull(phoneNumberList);
        final List<String> phoneNumbersFromPhoneBook = phoneNumberList.stream().map(PhoneNumber::getNumber).collect(Collectors.toList());
        assertEquals(phoneNumbers, phoneNumbersFromPhoneBook);

    }

    private void fillWithUsers(@NotNull String number, @NotNull List<String> users) {
        users.forEach(user -> phoneBook.addEntry(user, number));
    }

    @Test
    void getUsersByNumber() {
        final List<String> names = Arrays.asList("qwe", "asd", "zxc", "name", "username");
        final String number = "12345678";
        fillWithUsers(number, names);
        phoneBook.addEntry("qwerty", "8-737-12");
        phoneBook.addEntry("user", "5352435");
        final List<User> userList = phoneBook.getNamesByPhoneNumber(number);
        assertNotNull(userList);
        final List<String> namesFromPhoneBook = userList.stream().map(User::getName).collect(Collectors.toList());
        assertEquals(names, namesFromPhoneBook);
    }

    @Test
    void getUsersFromEmptyPhoneBook() {
        final List<User> namesFromPhoneBook = phoneBook.getNamesByPhoneNumber("123");
        assertNull(namesFromPhoneBook);
    }

    private void fill(@NotNull List<String> names, @NotNull List<String> numbers) {
        for (int i = 0; i < names.size(); i++) {
            phoneBook.addEntry(names.get(i), numbers.get(i));
        }
    }

    @Test
    void deleteExistingEntry() {
        fill(names, numbers);
        assertTrue(phoneBook.deleteEntry("name3", "number3"));
    }

    @Test
    void deleteNotExistingEntry() {
        fill(names, numbers);
        assertFalse(phoneBook.deleteEntry("name100", "number100"));
    }

    @Test
    void addDeleteAndAddAgain() {
        fill(names, numbers);
        final String name = "name3";
        final String number = "number3";
        assertTrue(phoneBook.deleteEntry(name, number));
        assertTrue(phoneBook.addEntry(name, number));
    }

    @Test
    void changeNameForNotExistingEntry() {
        fill(names, numbers);
        assertFalse(phoneBook.changeName("qwe", "number1", "asd"));
    }

    @Test
    void changeName() {
        fill(names, numbers);
        final String newName = "asd";
        phoneBook.addEntry(newName, "123");
        assertTrue(phoneBook.changeName("name1", "number1", newName));
        final List<PhoneNumber> phoneNumbers = phoneBook.getPhoneNumbersByName(newName);
        assertNotNull(phoneNumbers);
        final List<String> phoneNumbersStrings = phoneNumbers.stream().map(PhoneNumber::getNumber).collect(Collectors.toList());
        assertEquals(Arrays.asList("123", "number1"), phoneNumbersStrings);
    }

    @Test
    void changeNumber() {
        fill(names, numbers);
        final String newNumber = "1234";
        phoneBook.addEntry("qwe", newNumber);
        assertTrue(phoneBook.changeNumber("number1", "name1", newNumber));
        final List<User> users = phoneBook.getNamesByPhoneNumber(newNumber);
        assertNotNull(users);
        final List<String> usersString = users.stream().map(User::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("qwe", "name1"), usersString);
    }
}