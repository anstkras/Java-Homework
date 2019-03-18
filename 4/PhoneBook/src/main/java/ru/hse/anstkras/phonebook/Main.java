package ru.hse.anstkras.phonebook;

import org.jetbrains.annotations.NotNull;
import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String COMMANDS = "0 - exit\n" +
            "1 - add entry(name and phone number)\n" +
            "2 - find numbers by name\n" +
            "3 - find names by number\n" +
            "4 - delete entry\n" +
            "5 - change name for entry\n" +
            "6 - change number for entry\n" +
            "7 - print all entries\n";

    private Main() {
    }

    @NotNull
    private static String getName() {
        System.out.println("Enter name:");
        return SCANNER.next();
    }

    @NotNull
    private static String getNumber() {
        System.out.println("Enter phone number:");
        return SCANNER.next();
    }

    public static void main(String[] args) {
        final var phoneBook = new PhoneBook("phonebook");
        System.out.println(COMMANDS);
        while (true) {
            int command = SCANNER.nextInt();
            switch (command) {
                case 0:
                    return;
                case 1: {
                    final String name = getName();
                    final String phoneNumber = getNumber();
                    phoneBook.addEntry(name, phoneNumber);
                    break;
                }
                case 2: {
                    final String name = getName();
                    final List<PhoneNumber> numbers = phoneBook.getPhoneNumbersByName(name);
                    if (numbers == null) {
                        System.out.println("The user does not exists in the phone book");
                        break;
                    }

                    System.out.println("Phone numbers:");
                    numbers.forEach(phoneNumber -> System.out.println(phoneNumber.getNumber()));
                    break;
                }
                case 3: {
                    final String number = getNumber();
                    final List<User> users = phoneBook.getNamesByPhoneNumber(number);
                    if (users == null) {
                        System.out.println("The number does not exists in the phone book");
                        break;
                    }
                    System.out.println("Names:");
                    users.forEach(user -> System.out.println(user.getName()));
                    break;
                }
                case 4: {
                    final String name = getName();
                    final String phoneNumber = getNumber();
                    if (phoneBook.deleteEntry(name, phoneNumber)) {
                        System.out.println("Entry successfully deleted");
                    } else {
                        System.out.println("The entry does not exists in the phone book");
                    }
                    break;
                }
                case 5: {
                    final String name = getName();
                    final String phone = getNumber();
                    System.out.println("Enter new name:");
                    final String newName = SCANNER.next();
                    if (phoneBook.changeName(name, phone, newName)) {
                        System.out.println("Name changed successfully");
                    } else {
                        System.out.println("The entry does not exists in the phone book");
                    }
                    break;
                }
                case 6: {
                    final String name = getName();
                    final String phone = getNumber();
                    System.out.println("Enter new number:");
                    final String newNumber = SCANNER.next();
                    if (phoneBook.changeNumber(phone, name, newNumber)) {
                        System.out.println("Number changed successfully");
                    } else {
                        System.out.println("The entry does not exists in the phone book");
                    }
                    break;
                }
                case 7: {
                    final List<PhoneBook.Entry> pairs = phoneBook.getAllPairs();
                    pairs.forEach(entry -> System.out.println(entry.getUserName() + " " + entry.getNumber()));
                    break;
                }
                default: {
                    System.out.println("Wrong command number");
                    break;
                }
            }
        }
    }
}
