package ru.hse.anstkras.phonebook;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhoneBookTest {
    private static PhoneBook phoneBook;

    @BeforeAll
    public static void setup() {
        Fongo fongo = new Fongo("fongo_mock_server");
        MongoClient mongoClient = fongo.getMongo();
        phoneBook = new PhoneBook(mongoClient, "test_phone_book");
    }

    @Test
    public void addEntry() {
        phoneBook.addEntry("name", "number");
        List<PhoneBook.Entry> allPairs = phoneBook.getAllPairs();
        assertEquals(1, allPairs.size());
        assertEquals("name", allPairs.get(0).getUser().getName());
        assertEquals("number", allPairs.get(0).getPhoneNumber().getNumber());
    }
}