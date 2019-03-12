package ru.hse.anstkras.reflector;

import java.io.File;
import java.io.FileWriter;
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

    public static void printStructure(Class<?> someClass) throws IOException {
        File file = new File(someClass.getSimpleName() + ".java");
        FileWriter fileWriter = new FileWriter(file);
        printClass(someClass, 0, fileWriter);
        fileWriter.close();
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

    }

    private static void printSet(Set<String> set, String startString, Writer writer) throws IOException {
        if (!set.isEmpty()) {
            writer.write(startString + "\n");
            for (String element : set) {
                writer.write(element);
                writer.write("\n");
            }
            writer.write("\n");
        }
    }

    private static void printClass(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        fileWriter.write(" ".repeat(indent));
        String modifiers = Modifier.toString(clazz.getModifiers());
        if (clazz.isInterface()) {
            fileWriter.write(modifiers + " ");
        } else {
            fileWriter.write(modifiers + " class ");
        }
        fileWriter.write(typeToString(clazz, true));

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            fileWriter.write(" extends ");
            fileWriter.write(typeToString(superClass, false));
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            fileWriter.write(" implements ");
            var stringJoiner = new StringJoiner(", ");
            stringJoiner.setEmptyValue("");
            for (Class<?> classInterface : interfaces) {
                stringJoiner.add(typeToString(classInterface, false));
            }
            fileWriter.write(stringJoiner.toString());
        }
        fileWriter.write(" {\n");
        printFields(clazz, indent, fileWriter);
        printConstructors(clazz, indent + TAB_SIZE, fileWriter);
        printMethods(clazz, indent + TAB_SIZE, fileWriter);
        printClasses(clazz, indent, fileWriter);
        fileWriter.write(" ".repeat(indent) + "}");
    }

    private static void printConstructors(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length != 0) {
            fileWriter.write("\n");
        }
        for (Constructor constructor : constructors) {
            if (!constructor.isSynthetic()) {
                printConstructor(constructor, indent, fileWriter);
                fileWriter.write("\n");
            }
        }
    }

    private static void printConstructor(Constructor<?> constructor, int indent, FileWriter fileWriter) throws IOException {
        fileWriter.write(" ".repeat(indent));
        String modifiers = Modifier.toString(constructor.getModifiers());
        if (modifiers.length() > 0) {
            fileWriter.write(modifiers + " ");
        }

        TypeVariable<?>[] typeParameters = constructor.getTypeParameters();
        StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
        stringJoiner.setEmptyValue("");
        for (Type type : typeParameters) {
            stringJoiner.add(typeToString(type, false));
        }
        fileWriter.write(stringJoiner + " " + constructor.getDeclaringClass().getSimpleName() + "(");
        Class<?>[] paramTypes = constructor.getParameterTypes();
        int start = 0;
        if (isNested(constructor.getDeclaringClass())) {
            start = 1;
        }
        for (int i = start; i < paramTypes.length; i++) {
            fileWriter.write(typeToString(paramTypes[i], false) + " " + "name" + i);
            if (i != paramTypes.length - 1) {
                fileWriter.write(", ");
            }
        }
        fileWriter.write(") {}\n");
    }

    private static void printMethods(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length != 0) {
            fileWriter.write("\n");
        }
        for (Method method : methods) {
            if (!method.isSynthetic()) {
                fileWriter.write(methodToString(method, indent));
                fileWriter.write("\n");
            }
        }
    }

    private static void printClasses(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        Class<?>[] classes = clazz.getDeclaredClasses();
        for (Class<?> insideClass : classes) {
            printClass(insideClass, indent + TAB_SIZE, fileWriter);
            fileWriter.write("\n");
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

        TypeVariable<?>[] typeParameters = method.getTypeParameters();
        StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
        stringJoiner.setEmptyValue("");
        for (Type type : typeParameters) {
            stringJoiner.add(typeToString(type, false));
        }
        stringBuilder.append(stringJoiner).append(" ").append(typeToString(method.getReturnType(), false)).append(" ").append(method.getName()).append("(");
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            stringBuilder.append(typeToString(paramTypes[i], false)).append(" ").append("name").append(i);
            if (i != paramTypes.length - 1) {
                stringBuilder.append(", ");
            }
        }
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

    private static void printFields(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isSynthetic()) {
                fileWriter.write(fieldToString(field, indent + TAB_SIZE) + "\n");
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

    private static String typeToString(Type type, boolean simple) {
        var stringBuilder = new StringBuilder();
        typeToString(stringBuilder, type, simple);
        return stringBuilder.toString();
    }

    private static void typeToString(StringBuilder stringBuilder, Type type, boolean simple) {
        if (type instanceof ParameterizedType) {
            stringBuilder.append(type.getTypeName());
        } else if (type instanceof WildcardType) { // does it work?
            stringBuilder.append(type.getTypeName());
        } else if (type instanceof Class) {
            Class<?> classType = (Class<?>) type;
            TypeVariable<?>[] typeParameters = classType.getTypeParameters();
            StringJoiner params = new StringJoiner(" ,", "<", ">");
            params.setEmptyValue("");
            for (Type typeParameter : typeParameters) {
                params.add(typeToString(typeParameter, false));
            }
            if (!simple) {
                stringBuilder.append(classType.getName());
            } else {
                stringBuilder.append(classType.getSimpleName());
            }
            stringBuilder.append(params.toString());
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

    private static boolean isNested(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }
}
