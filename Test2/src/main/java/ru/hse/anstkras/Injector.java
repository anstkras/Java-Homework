package ru.hse.anstkras;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class to emulate Dependency Injector
 */
public class Injector {
    private Injector() {
    }

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<Class> implementationClasses) throws Exception {
        return createClass(Class.forName(rootClassName), implementationClasses, Collections.emptyList());
    }

    private static Object createClass(Class<?> clazz, List<Class> implementationClasses, List<Object> createdClasses) throws AmbiguousImplementationException, ImplementationNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object createdClass = findClass(clazz, createdClasses);
        if (createdClass != null) {
            return createdClass;
        }

        Constructor[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new IllegalArgumentException();
        }
        if (constructors.length == 0) {
            Class implementationClass = getClassForInterfaceOrAbstractClass(clazz, implementationClasses);
            return createClass(implementationClass, implementationClasses, createdClasses);
        }

        Constructor<?> constructor = constructors[0];
        Class<?>[] parametersTypes = constructor.getParameterTypes();
        constructor.setAccessible(true);
        List parameters = new ArrayList<>();
        for (Class<?> parameterType : parametersTypes) {
            parameters.add(createClass(parameterType, implementationClasses, createdClasses));
        }
        Object instance = constructor.newInstance(parameters.toArray());
        return instance;
    }

    private static Object findClass(Class<?> clazz, List<Object> createdClasses) {
        for (Object object : createdClasses) {
            if (clazz.isAssignableFrom(object.getClass())) {
                return object;
            }
        }
        return null;
    }

    private static Class getClassForInterfaceOrAbstractClass(Class<?> abstractClass, List<Class> implementationClasses) throws AmbiguousImplementationException, ImplementationNotFoundException {
        Class<?> result = null;
        for (Class<?> clazz : implementationClasses) {
            if (abstractClass.isAssignableFrom(clazz)) {
                if (result != null) {
                    throw new AmbiguousImplementationException();
                } else {
                    result = clazz;
                }
            }
        }

        if (result == null) {
            throw new ImplementationNotFoundException();
        }
        return result;
    }
}