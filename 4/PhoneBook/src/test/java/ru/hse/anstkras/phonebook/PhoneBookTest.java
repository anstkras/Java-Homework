package ru.hse.anstkras.phonebook;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    private PhoneBook phoneBook;

    @BeforeEach
    void setup() {
        Fongo fongo = new Fongo("fongo_mock_server");
        MongoClient mongoClient = fongo.getMongo();
        phoneBook = new PhoneBook(mongoClient, "test_phone_book");
    }

    @Test
    void addEntry() {
        phoneBook.addEntry("name", "number");
        List<PhoneBook.Entry> allPairs = phoneBook.getAllPairs();
        assertEquals(1, allPairs.size());
        assertEquals("name", allPairs.get(0).getUser().getName());
        assertEquals("number", allPairs.get(0).getPhoneNumber().getNumber());
    }

    private void fillWithPhoneNumbers(String name, List<String> phoneNumbers) {
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

    private void fillWithUsers(String number, List<String> users) {
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

    @Test
    void deleteExistingEntry() {
        for (int i = 0; i < 10; i++) {
            phoneBook.addEntry("name" + i, "number" + i);
        }
        assertTrue(phoneBook.deleteEntry("name3", "number3"));
    }

    @Test
    void deleteNotExistingEntry() {
        for (int i = 0; i < 10; i++) {
            phoneBook.addEntry("name" + i, "number" + i);
        }
        assertFalse(phoneBook.deleteEntry("name100", "number100"));
    }

    @Test
    void addDeleteAndAddAgain() {
        for (int i = 0; i < 10; i++) {
            phoneBook.addEntry("name" + i, "number" + i);
        }
        assertTrue(phoneBook.deleteEntry("name3", "number3"));
        assertTrue(phoneBook.addEntry("name3", "number3"));
    }

    @Test
    void changeNameForNotExistingEntry() {
        for (int i = 0; i < 10; i++) {
            phoneBook.addEntry("name" + i, "number" + i);
        }
        assertFalse(phoneBook.changeName("qwe", "number1", "asd"));
    }

    @Test
    void changeName() {
        for (int i = 0; i < 10; i++) {
            phoneBook.addEntry("name" + i, "number" + i);
        }
        phoneBook.addEntry("asd", "123");
        assertTrue(phoneBook.changeName("name1", "number1", "asd"));
        List<PhoneNumber> phoneNumbers = phoneBook.getPhoneNumbersByName("asd");
        assertNotNull(phoneNumbers);
        List<String> phoneNumbersStrings = phoneNumbers.stream().map(PhoneNumber::getNumber).collect(Collectors.toList());
        assertEquals(Arrays.asList("123", "number1"), phoneNumbersStrings);
    }

    @Test
    void changeNumber() {
        for (int i = 0; i < 10; i++) {
            phoneBook.addEntry("name" + i, "number" + i);
        }
        phoneBook.addEntry("qwe", "1234");
        assertTrue(phoneBook.changeNumber("number1", "name1", "1234"));
        List<User> users = phoneBook.getNamesByPhoneNumber("1234");
        assertNotNull(users);
        List<String> usersString = users.stream().map(User::getName).collect(Collectors.toList());
        assertEquals(Arrays.asList("qwe", "name1"), usersString);
    }
}