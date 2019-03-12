package ru.hse.anstkras.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Reflector is a class that allows to generate a .java file using reflection
 * or to analyze the difference between two classes
 */
public class Reflector {
    private static final int TAB_SIZE = 4;

    private Reflector() {
    }

    /**
     * Prints a valid java code of someClass to given writer
     *
     * @throws IOException in case of problems with writer
     */
    public static void printStructure(@NotNull Class<?> someClass, @NotNull Writer writer) throws IOException {
        writer.write(classToString(someClass, 0));
    }

    /**
     * Prints a difference between given classes to the given writer
     *
     * @throws IOException in case of problems with writer
     */
    public static void diffClasses(@NotNull Class<?> firstClass, @NotNull Class<?> secondClass, @NotNull Writer writer) throws IOException {
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

    private static void printSet(@NotNull Set<String> set, @NotNull String startString, @NotNull Writer writer) throws IOException {
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

    @NotNull
    private static String classToString(@NotNull Class<?> clazz, int indent) {
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

        addFields(clazz, indent + TAB_SIZE, stringBuilder);
        addConstructors(clazz, indent + TAB_SIZE, stringBuilder);
        addMethods(clazz, indent + TAB_SIZE, stringBuilder);
        addClasses(clazz, indent + TAB_SIZE, stringBuilder);
        stringBuilder.append(" ".repeat(indent)).append("}");
        return stringBuilder.toString();
    }

    private static void addConstructors(@NotNull Class<?> clazz, int indent, @NotNull StringBuilder stringBuilder) {
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

    @NotNull
    private static String constructorToString(@NotNull Constructor<?> constructor, int indent) {
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

    private static void addTypeParameters(@NotNull Executable executable, @NotNull StringBuilder stringBuilder) {
        TypeVariable<?>[] typeParameters = executable.getTypeParameters();
        StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
        stringJoiner.setEmptyValue("");
        for (Type type : typeParameters) {
            stringJoiner.add(typeToString(type));
        }
        stringBuilder.append(stringJoiner);
    }

    private static void addParameterTypes(@NotNull Executable executable, @NotNull StringBuilder stringBuilder, boolean skipFirst) {
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

    private static void addMethods(@NotNull Class<?> clazz, int indent, @NotNull StringBuilder stringBuilder) {
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

    private static void addClasses(@NotNull Class<?> clazz, int indent, @NotNull StringBuilder stringBuilder) {
        Class<?>[] classes = clazz.getDeclaredClasses();
        for (Class<?> insideClass : classes) {
            stringBuilder.append(classToString(insideClass, indent)).append("\n");
        }
    }

    @NotNull
    private static String methodToStringWithoutBody(@NotNull Method method, int indent) {
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

    @NotNull
    private static String methodToString(@NotNull Method method, int indent) {
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

    private static void addFields(@NotNull Class<?> clazz, int indent, @NotNull StringBuilder stringBuilder) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isSynthetic()) {
                stringBuilder.append(fieldToString(field, indent)).append("\n");
            }
        }
    }

    @NotNull
    private static String fieldToString(@NotNull Field field, int indent) {
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
        if (Modifier.isFinal(field.getModifiers())) {
            stringBuilder.append(" = ");
            stringBuilder.append(getDefaultValue(field.getType()));
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    @NotNull
    private static String typeToString(@NotNull Type type) {
        return typeToString(type, false);
    }

    @NotNull
    private static String typeToString(@NotNull Type type, boolean simpleClassName) {
        var stringBuilder = new StringBuilder();
        addType(stringBuilder, type, simpleClassName);
        return stringBuilder.toString();
    }

    private static void addType(@NotNull StringBuilder stringBuilder, @NotNull Type type, boolean simpleClassName) {
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

    private static void addClassTypeParameters(@NotNull Class<?> classType, @NotNull StringBuilder stringBuilder) {
        TypeVariable<?>[] typeParameters = classType.getTypeParameters();
        if (typeParameters.length != 0) {
            stringBuilder.append("<");
            boolean first = true;
            for (Type typeParameter : typeParameters) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                first = false;
                addType(stringBuilder, typeParameter, false);
            }
            stringBuilder.append(">");
        }
    }

    private static boolean isNested(@NotNull Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    @NotNull
    private static String getDefaultValue(@NotNull Type type) {
        if (type == byte.class || type == short.class || type == int.class) {
            return "0";
        }
        if (type == long.class) {
            return "0L";
        }
        if (type == char.class) {
            return "'\u0000'";
        }
        if (type == float.class) {
            return "0.0F";
        }
        if (type == double.class) {
            return "0.0";
        }
        if (type == boolean.class) {
            return "false";
        }
        if (type == void.class) {
            return "void";
        }
        return "null";
    }
}
