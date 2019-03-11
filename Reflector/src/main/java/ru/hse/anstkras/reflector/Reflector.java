package ru.hse.anstkras.reflector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.StringJoiner;

public class Reflector {
    private static final int TAB_SIZE = 4;

    public static void printStructure(Class<?> someClass) throws IOException {
        File file = new File(someClass.getSimpleName() + ".java");
        FileWriter fileWriter = new FileWriter(file);
        printClass(someClass, 0, fileWriter);
        fileWriter.close();
    }

    private static void printClass(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        fileWriter.write(" ".repeat(indent));
        fileWriter.write("public class ");

        fileWriter.write(typeToString(clazz));

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            fileWriter.write(" extends ");
            fileWriter.write(typeToString(superClass));
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length != 0) {
            fileWriter.write(" implements ");
            var stringJoiner = new StringJoiner(", ");
            stringJoiner.setEmptyValue("");
            for (Class<?> classInterface : interfaces) {
                stringJoiner.add(typeToString(classInterface));
            }
            fileWriter.write(stringJoiner.toString());
        }
        fileWriter.write(" {\n");
        printFields(clazz, indent, fileWriter);
        printMethods(clazz, indent + TAB_SIZE, fileWriter);
        fileWriter.write(" ".repeat(indent) + "}");
    }

    private static void printMethods(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length != 0) {
            fileWriter.write("\n");
        }
        for (Method method : methods) {
            printMethod(method, indent, fileWriter);
            fileWriter.write("\n");
        }
    }

    private static void printMethod(Method method, int indent, FileWriter fileWriter) throws IOException {
        fileWriter.write(" ".repeat(indent));
        String modifiers = Modifier.toString(method.getModifiers());
        if (modifiers.length() > 0) {
            fileWriter.write(modifiers + " ");
        }

        TypeVariable<?>[] typeParameters = method.getTypeParameters();
        StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
        stringJoiner.setEmptyValue("");
        for (Type type : typeParameters) {
            stringJoiner.add(typeToString(type));
        }
        fileWriter.write(stringJoiner + " " + typeToString(method.getReturnType()) + " " + method.getName() + "(");
        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            fileWriter.write(typeToString(paramTypes[i]) + " " + "name" + i);
            if (i != paramTypes.length - 1) {
                fileWriter.write(", ");
            }
        }
        fileWriter.write(") {\n");
        fileWriter.write(" ".repeat(indent + TAB_SIZE));
        fileWriter.write("throw new UnsupportedOperationException();\n");
        fileWriter.write(" ".repeat(indent) + "}\n");
    }

    private static void printFields(Class<?> clazz, int indent, FileWriter fileWriter) throws IOException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            printField(field, indent + TAB_SIZE, fileWriter);
        }
    }

    private static void printField(Field field, int indent, FileWriter fileWriter) throws IOException {
        String modifiers = Modifier.toString(field.getModifiers());
        String type = field.getGenericType().getTypeName();
        fileWriter.write(" ".repeat(indent));
        if (!modifiers.isEmpty()) {
            fileWriter.write(modifiers + " ");
        }

        fileWriter.write(type + " " + field.getName() + ";\n");
    }

    private static String typeToString(Type type) {
        var stringBuilder = new StringBuilder();
        typeToString(stringBuilder, type);
        return stringBuilder.toString();
    }

    private static void typeToString(StringBuilder stringBuilder, Type type) {
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
                params.add(typeToString(typeParameter));
            }
            stringBuilder.append(classType.getName());
            stringBuilder.append(params.toString());
        } else if (type instanceof GenericArrayType) {
            var genericArrayType = (GenericArrayType) type;
            stringBuilder.append(genericArrayType.getGenericComponentType());
            stringBuilder.append("[]");
        } else if (type instanceof TypeVariable<?>) {
            var typeVariable = (TypeVariable<?>) type;
            stringBuilder.append(typeVariable.getName());
            stringBuilder.append(" extends ");
            var stringJoiner = new StringJoiner(" & ");
            stringJoiner.setEmptyValue("");
            for (Type boundType : typeVariable.getBounds()) {
                stringJoiner.add(boundType.getTypeName());
            }
            stringBuilder.append(stringJoiner);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
