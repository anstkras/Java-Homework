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
        TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
        StringJoiner params = new StringJoiner(" ,", "<", ">");
        params.setEmptyValue("");
        for (Type type : typeParameters) {
            params.add(typeToString(type));
        }
        fileWriter.write(" ".repeat(indent) + "public class " + clazz.getName() + params + " {\n");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            printField(field, indent + TAB_SIZE, fileWriter);
        }
        fileWriter.write(" ".repeat(indent) + "}");
    }

    private static void printField(Field field, int indent, FileWriter fileWriter) throws IOException {
        String modifiers = Modifier.toString(field.getModifiers());
        String type = field.getGenericType().getTypeName();
        fileWriter.write(" ".repeat(indent) + modifiers + " " + type + " " + field.getName() + ";\n"); // TODO fix spaces
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
            stringBuilder.append(type.getTypeName());
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
