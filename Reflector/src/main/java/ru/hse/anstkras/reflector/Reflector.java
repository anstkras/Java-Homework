package ru.hse.anstkras.reflector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Reflector {
    private static final int TAB_SIZE = 4;

    public static void printStructure(Class<?> someClass, Writer writer) throws IOException {
        writer.write(classToString(someClass, 0));
    }

    public static void diffClasses(Class<?> firstClass, Class<?> secondClass, Writer writer) throws IOException {
        String firstName = firstClass.getSimpleName();
        String secondName = secondClass.getSimpleName();

        Set<String> firstFields = Arrays.stream(firstClass.getDeclaredFields()).
                map(field -> fieldToString(field, 0)).collect(Collectors.toSet());
        Set<String> secondFields = Arrays.stream(secondClass.getDeclaredFields()).
                map(field -> fieldToString(field, 0)).collect(Collectors.toSet());

        var firstFieldsCopy = new HashSet<>(firstFields);
        firstFields.removeAll(secondFields);
        secondFields.removeAll(firstFieldsCopy);

        printSet(firstFields, firstName + " contains fields that " + secondName + " does not contain:", writer);
        printSet(secondFields, secondName + " contains fields that " + firstName + " does not contain:", writer);

        Set<String> firstMethods = Arrays.stream(firstClass.getDeclaredMethods()).
                map(method -> methodToStringWithoutBody(method, 0)).collect(Collectors.toSet());
        Set<String> secondMethods = Arrays.stream(secondClass.getDeclaredMethods()).
                map(method -> methodToStringWithoutBody(method, 0)).collect(Collectors.toSet());

        var firstMethodsCopy = new HashSet<>(firstMethods);
        firstMethods.removeAll(secondMethods);
        secondMethods.removeAll(firstMethodsCopy);

        printSet(firstMethods, firstName + " contains methods that " + secondName + " does not contain:", writer);
        printSet(secondMethods, secondName + " contains methods that " + firstName + " does not contain:", writer);

        if (firstFields.isEmpty() && secondFields.isEmpty() && firstMethods.isEmpty() && secondMethods.isEmpty()) {
            writer.write("Classes are not different\n");
        }
    }

    private static void printSet(Set<String> set, String startString, Writer writer) throws IOException {
        var stringBuilder = new StringBuilder();
        if (!set.isEmpty()) {
            stringBuilder.append(startString).append("\n");
            for (String element : set) {
                stringBuilder.append(element).append("\n");
            }
            stringBuilder.append("\n");
        }
        writer.write(stringBuilder.toString());
    }

    private static String classToString(Class<?> clazz, int indent) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(" ".repeat(indent));
        String modifiers = Modifier.toString(clazz.getModifiers());
        if (clazz.isInterface()) {
            stringBuilder.append(modifiers).append(" ");
        } else {
            stringBuilder.append(modifiers).append(" class ");
        }
        stringBuilder.append(typeToString(clazz, true));

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            stringBuilder.append(" extends ");
            stringBuilder.append(typeToString(superClass));
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            stringBuilder.append(" implements ");
            var stringJoiner = new StringJoiner(", ");
            stringJoiner.setEmptyValue("");
            for (Class<?> classInterface : interfaces) {
                stringJoiner.add(typeToString(classInterface));
            }
            stringBuilder.append(stringJoiner);
        }
        stringBuilder.append(" {\n");

        printFields(clazz, indent + TAB_SIZE, stringBuilder);
        printConstructors(clazz, indent + TAB_SIZE, stringBuilder);
        printMethods(clazz, indent + TAB_SIZE, stringBuilder);
        printClasses(clazz, indent + TAB_SIZE, stringBuilder);
        stringBuilder.append(" ".repeat(indent)).append("}");
        return stringBuilder.toString();
    }

    private static void printConstructors(Class<?> clazz, int indent, StringBuilder stringBuilder) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length != 0) {
            stringBuilder.append("\n");
        }
        for (Constructor constructor : constructors) {
            if (!constructor.isSynthetic()) {
                stringBuilder.append(constructorToString(constructor, indent)).append("\n");
            }
        }
    }

    private static String constructorToString(Constructor<?> constructor, int indent) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(" ".repeat(indent));
        String modifiers = Modifier.toString(constructor.getModifiers());
        if (modifiers.length() > 0) {
            stringBuilder.append(modifiers).append(" ");
        }
        addTypeParameters(constructor, stringBuilder);
        stringBuilder.append(" ").append(constructor.getDeclaringClass().getSimpleName()).append("(");
        addParameterTypes(constructor, stringBuilder, isNested(constructor.getDeclaringClass()));
        stringBuilder.append(") {}\n");
        return stringBuilder.toString();
    }

    private static void addTypeParameters(Executable executable, StringBuilder stringBuilder) {
        TypeVariable<?>[] typeParameters = executable.getTypeParameters();
        StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
        stringJoiner.setEmptyValue("");
        for (Type type : typeParameters) {
            stringJoiner.add(typeToString(type));
        }
        stringBuilder.append(stringJoiner);
    }

    private static void addParameterTypes(Executable executable, StringBuilder stringBuilder, boolean skipFirst) {
        Class<?>[] parameterTypes = executable.getParameterTypes();
        int start = 0;
        if (skipFirst) {
            start = 1;
        }
        for (int i = start; i < parameterTypes.length; i++) {
            stringBuilder.append(typeToString(parameterTypes[i])).append(" ").append("name").append(i);
            if (i != parameterTypes.length - 1) {
                stringBuilder.append(", ");
            }
        }
    }

    private static void printMethods(Class<?> clazz, int indent, StringBuilder stringBuilder) {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length != 0) {
            stringBuilder.append("\n");
        }
        for (Method method : methods) {
            if (!method.isSynthetic()) {
                stringBuilder.append(methodToString(method, indent)).append("\n");
            }
        }
    }

    private static void printClasses(Class<?> clazz, int indent, StringBuilder stringBuilder) {
        Class<?>[] classes = clazz.getDeclaredClasses();
        for (Class<?> insideClass : classes) {
            stringBuilder.append(classToString(insideClass, indent)).append("\n");
        }
    }

    private static String methodToStringWithoutBody(Method method, int indent) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(" ".repeat(indent));
        if (method.isDefault()) {
            stringBuilder.append("default ");
        }
        String modifiers = Modifier.toString(method.getModifiers());
        if (modifiers.length() > 0) {
            stringBuilder.append(modifiers);
        }

        addTypeParameters(method, stringBuilder);
        stringBuilder.append(" ").append(typeToString(method.getReturnType())).append(" ");
        stringBuilder.append(method.getName()).append("(");
        addParameterTypes(method, stringBuilder, false);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private static String methodToString(Method method, int indent) {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(methodToStringWithoutBody(method, indent));
        if (Modifier.isAbstract(method.getModifiers())) {
            stringBuilder.append(";\n");
        } else {
            stringBuilder.append(" {\n");
            stringBuilder.append(" ".repeat(indent + TAB_SIZE));
            stringBuilder.append("throw new UnsupportedOperationException();\n");
            stringBuilder.append(" ".repeat(indent)).append("}\n");
        }
        return stringBuilder.toString();
    }

    private static void printFields(Class<?> clazz, int indent, StringBuilder stringBuilder) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isSynthetic()) {
                stringBuilder.append(fieldToString(field, indent)).append("\n");
            }
        }
    }

    private static String fieldToString(Field field, int indent) {
        var stringBuilder = new StringBuilder();
        String modifiers = Modifier.toString(field.getModifiers());
        String type = field.getGenericType().getTypeName();
        stringBuilder.append(" ".repeat(indent));
        if (!modifiers.isEmpty()) {
            stringBuilder.append(modifiers);
            stringBuilder.append(" ");
        }

        stringBuilder.append(type);
        stringBuilder.append(" ");
        stringBuilder.append(field.getName());
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    private static String typeToString(Type type) {
        return typeToString(type, false);
    }

    private static String typeToString(Type type, boolean simpleClassName) {
        var stringBuilder = new StringBuilder();
        typeToString(stringBuilder, type, simpleClassName);
        return stringBuilder.toString();
    }

    private static void typeToString(StringBuilder stringBuilder, Type type, boolean simpleClassName) {
        if (type instanceof ParameterizedType) {
            stringBuilder.append(type.getTypeName());
        } else if (type instanceof Class) {
            Class<?> classType = (Class<?>) type;
            stringBuilder.append(simpleClassName ? classType.getSimpleName() : classType.getName());
            addClassTypeParameters(classType, stringBuilder);
        } else if (type instanceof GenericArrayType) {
            var genericArrayType = (GenericArrayType) type;
            stringBuilder.append(genericArrayType.getGenericComponentType());
            stringBuilder.append("[]");
        } else if (type instanceof TypeVariable<?>) {
            var typeVariable = (TypeVariable<?>) type;
            stringBuilder.append(typeVariable.getName());
            Type[] bounds = typeVariable.getBounds();
            if (bounds.length != 1 || !bounds[0].equals(Object.class)) {
                stringBuilder.append(" extends ");
                var stringJoiner = new StringJoiner(" & ");
                stringJoiner.setEmptyValue("");
                for (Type boundType : bounds) {
                    if (!type.equals(Object.class)) {
                        stringJoiner.add(boundType.getTypeName());
                    }
                }
                stringBuilder.append(stringJoiner);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void addClassTypeParameters(Class<?> classType, StringBuilder stringBuilder) {
        TypeVariable<?>[] typeParameters = classType.getTypeParameters();
        if (typeParameters.length != 0) {
            stringBuilder.append("<");
            boolean first = true;
            for (Type typeParameter : typeParameters) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                first = false;
                typeToString(stringBuilder, typeParameter, false);
            }
            stringBuilder.append(">");
        }
    }

    private static boolean isNested(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }
}
