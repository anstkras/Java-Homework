package ru.hse.anstkras.myjunit;

public class InteractionClass {
    private String string;
    private int counterBefore = 0;
    private int counterAfter = 0;

    @BeforeClass
    void init() {
        string = "String";
    }

    @Before
    void before() {
        counterBefore++;
    }

    @After
    void after() {
        counterAfter++;
    }

    @AfterClass
    void finish() {
        if (counterBefore != 3 || counterAfter != 3) {
            throw new IllegalStateException();
        }
    }

    @Test
    void test() {
        if (string == null) {
            throw new IllegalStateException();
        }
    }

    @Test
    void test2() {
    }

    @Test(exception = IndexOutOfBoundsException.class)
    void test3() {
        throw new IndexOutOfBoundsException();
    }

    @Test(ignored = "because")
    void test4() {
    }
}
