package ru.hse.anstkras;

import org.junit.jupiter.api.Test;
import ru.hse.anstkras.TestClasses.ClassWithOneClassDependency;
import ru.hse.anstkras.TestClasses.ClassWithOneInterfaceDependency;
import ru.hse.anstkras.TestClasses.ClassWithoutDependencies;
import ru.hse.anstkras.TestClasses.InterfaceImpl;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InjectorTest {

    @Test
    void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize("ru.hse.anstkras.TestClasses.ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                "ru.hse.anstkras.TestClasses.ClassWithOneClassDependency",
                Collections.singletonList(Class.forName("ru.hse.anstkras.TestClasses.ClassWithoutDependencies"))
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertTrue(instance.dependency != null);
    }

    @Test
    void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                "ru.hse.anstkras.TestClasses.ClassWithOneInterfaceDependency",
                Collections.singletonList(Class.forName("ru.hse.anstkras.TestClasses.InterfaceImpl"))
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }
}
