package Trie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TrieTest {
    private Trie trie;

    @BeforeEach
    private void init() {
        trie = new Trie();
    }

    @AfterEach
    private void makeNull() {
        trie = null;
    }

    @Test
    public void empty() {
        assertTrue(trie.empty());
    }

    @Test
    public void addAndCheckSize() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        assertEquals(6, trie.size());
    }

    @Test
    public void addOneStringAndCheckContains() {
        String s = "123";
        trie.add(s);
    }

    @Test
    public void addSeveralStringsAndCheckContainsTrue() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        for (String string : strings) {
            assertTrue(trie.contains(string));
        }
    }

    @Test
    public void addSeveralStringsAndCheckContainsFalse() {
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
    public void addNewElement() {
        assertTrue(trie.add("qwe"));
    }

    @Test
    public void addTheSameElement() {
        String s = "qwe";
        trie.add(s);
        assertFalse(trie.add(s));
    }

    @Test
    public void addNullElement() {
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
    }

    @Test
    public void containsNullElement() {
        assertThrows(IllegalArgumentException.class, () -> trie.contains(null));
    }

    @Test
    public void addShorterString() {
        trie.add("123");
        assertTrue(trie.add("12"));
    }

    @Test
    public void howManyStartWithNullPrefix() {
        assertThrows(IllegalArgumentException.class, () -> trie.howManyStartWithPrefix(null));
    }

    @Test
    public void howManyStartWithPrefix() {
        String[] strings = {"123", "1245", "12345", "145", "232", "3"};
        for (String string : strings) {
            trie.add(string);
        }
        assertEquals(2, trie.howManyStartWithPrefix("123"));
        assertEquals(3, trie.howManyStartWithPrefix("12"));
        assertEquals(0, trie.howManyStartWithPrefix("4"));
        assertEquals(0, trie.howManyStartWithPrefix("18"));
    }

}