package ru.hse.anstkras.Trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


class TrieTest {
    private Trie trie;

    @BeforeEach
    private void init() {
        trie = new Trie();
    }

    @Test
    void empty() {
        assertTrue(trie.empty());
    }

    @Test
    void addAndCheckSize() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        assertEquals(6, trie.size());
    }

    @Test
    void addOneStringAndCheckContains() {
        String s = "123";
        trie.add(s);
        assertTrue(trie.contains("123"));
    }

    @Test
    void addSeveralStringsAndCheckContainsTrue() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        for (String string : strings) {
            assertTrue(trie.contains(string));
        }
    }

    @Test
    void addSeveralStringsAndCheckContainsFalse() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        String[] stringsToCheck = {"12", "1", "14", "1456", "1455", "4"};
        for (String string : stringsToCheck) {
            assertFalse(trie.contains(string));
        }
    }

    @Test
    void addNewElement() {
        assertTrue(trie.add("qwe"));
    }

    @Test
    void addTheSameElement() {
        String s = "qwe";
        trie.add(s);
        assertFalse(trie.add(s));
    }

    @Test
    void addNullElement() {
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
    }

    @Test
    void containsNullElement() {
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
    }

    @Test
    void addShorterString() {
        trie.add("123");
        assertTrue(trie.add("12"));
    }

    @Test
    void howManyStartWithNullPrefix() {
        assertThrows(IllegalArgumentException.class, () -> trie.howManyStartWithPrefix(null));
    }

    @Test
    void howManyStartWithPrefix() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        assertEquals(2, trie.howManyStartWithPrefix("123"));
        assertEquals(3, trie.howManyStartWithPrefix("12"));
        assertEquals(0, trie.howManyStartWithPrefix("4"));
        assertEquals(0, trie.howManyStartWithPrefix("18"));
    }

    @Test
    void removeNullElement() {
        assertThrows(IllegalArgumentException.class, () -> trie.remove(null));
    }

    @Test
    void addOneElementAndRemove() {
        String s = "123";
        trie.add(s);
        assertTrue(trie.remove(s));
    }

    @Test
    void addSeveralElementsAndRemove() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }

        String[] stringsNotToRemove = {"12", "1", "14", "1456", "1455", "4"};
        for (String string : stringsNotToRemove) {
            assertFalse(trie.remove(string));
        }

        String[] stringsToRemove = {"123", "145", "3"};
        for (String string : stringsToRemove) {
            assertTrue(trie.remove(string));
        }

        String[] stringsToStay = {"12345", "1245", "232"};
        for (String string : stringsToStay) {
            assertTrue(trie.contains(string));
        }
    }

    private void serializeAndDeserializeStrings(Collection<String> strings) throws IOException {
        for (String string : strings) {
            trie.add(string);
        }
        var trie2 = new Trie();
        var byteArrayOutputStream = new ByteArrayOutputStream();
        trie.serialize(byteArrayOutputStream);
        var byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        trie2.deserialize(byteArrayInputStream);
        assertEquals(trie, trie2);
    }

    @Test
    void serializeAndDeserialize() throws IOException {
        var strings = new ArrayList<>(Arrays.asList("123", "1245", "12345", "145", "232", "3"));
        serializeAndDeserializeStrings(strings);
    }

    @Test
    void serializeAndDeserializeWith2BytesSymbols() throws IOException {
        var strings = new ArrayList<>(Arrays.asList("\u20AC\u040b", "\u20AC24q", "12345", "145", "232", "3\u1270"));
        serializeAndDeserializeStrings(strings);
    }

    @Test
    void serializeEmptyTrie() throws IOException {
        var strings = new ArrayList<String>();
        serializeAndDeserializeStrings(strings);
    }

    @Test
    void serializeNull() {
        trie.add("123");
        assertThrows(IllegalArgumentException.class, () -> trie.serialize(null));
    }

    @Test
    void deserializeNull() {
        trie.add("123");
        assertThrows(IllegalArgumentException.class, () -> trie.deserialize(null));
    }

    @Test
    void equalsAndHashCode() {
        String[] strings = {"\u20AC\u040b", "\u20AC24q", "12345", "145", "232", "3\u1270"};
        for (String string : strings) {
            trie.add(string);
        }
        var trie2 = new Trie();
        for (String string : strings) {
            trie2.add(string);
        }
        assertEquals(trie.hashCode(), trie2.hashCode());
        assertEquals(trie, trie2);
    }

    @Test
    void equalsAndHashCodeForNotEqual() {
        String[] strings = {"\u20AC\u040b", "\u20AC24q", "12345", "145", "232", "3\u1270"};
        for (String string : strings) {
            trie.add(string);
        }
        String[] strings2 = {"\u20AC\u040b", "\u20AC24q", "12345", "145", "232", "3\u1270z"};
        var trie2 = new Trie();
        for (String string : strings2) {
            trie2.add(string);
        }
        assertNotEquals(trie.hashCode(), trie2.hashCode());
        assertNotEquals(trie, trie2);
    }
}