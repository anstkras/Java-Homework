package ru.hse.anstkras.myjunit.testClasses;

import ru.hse.anstkras.myjunit.*;

public class InteractionClass {
    private String string;
    private int counterBefore = 0;
    private int counterAfter = 0;

    @BeforeClass
    public void init() {
        string = "String";
    }

    @Before
    public void before() {
        counterBefore++;
    }

    @After
    public void after() {
        counterAfter++;
    }

    @AfterClass
    public void finish() {
        if (counterBefore != 3 || counterAfter != 3) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void test() {
        if (string == null) {
            throw new IllegalStateException();
        }
    }

    @Test
    public void test2() {
    }

    @Test(exception = IndexOutOfBoundsException.class)
    public void test3() {
        throw new IndexOutOfBoundsException();
    }

    @Test(ignored = "because")
    public void test4() {
    }
}
