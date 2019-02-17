package ru.hse.anstkras.phonebook;

import org.jetbrains.annotations.NotNull;
import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    @NotNull
    private static String getName() {
        System.out.println("Enter name:");
        return scanner.next();
    }

    @NotNull
    private static String getNumber() {
        System.out.println("Enter phone number:");
        return scanner.next();
    }

    public static void main(String[] args) {
        var phoneBook = new PhoneBook("phonebook");
        while (true) {
            int command = scanner.nextInt();
            switch (command) {
                case 0:
                    return;
                case 1: {
                    String name = getName();
                    String phoneNumber = getNumber();
                    phoneBook.addEntry(name, phoneNumber);
                    break;
                }
                case 2: {
                    String name = getName();
                    List<PhoneNumber> numbers = phoneBook.getPhoneNumbersByName(name);
                    if (numbers == null) {
                        System.out.println("There is no such a user in the phone book");
                        break;
                    }

                    System.out.println("Numbers:");
                    numbers.forEach(phoneNumber -> System.out.println(phoneNumber.getNumber()));
                    break;
                }
                case 3: {
                    String number = getNumber();
                    List<User> users = phoneBook.getNamesByPhoneNumber(number);
                    if (users == null) {
                        System.out.println("There is no such a number in the phone book");
                        break;
                    }
                    System.out.println("Names:");
                    users.forEach(user -> System.out.println(user.getName()));
                    break;
                }

                case 4: {
                    String name = getName();
                    String phoneNumber = getNumber();
                    if (phoneBook.deleteEntry(name, phoneNumber)) {
                        System.out.println("Entry successfully deleted");
                    } else {
                        System.out.println("Such an entry does not exist in the phone book");
                    }
                    break;
                }

                case 5: {
                    String name = getName();
                    String phone = getNumber();
                    System.out.println("Enter new name:");
                    String newName = scanner.next();
                    if (phoneBook.changeName(name, phone, newName)) {
                        System.out.println("Name changed successfully");
                    } else {
                        System.out.println("Such an entry does not exist in the phone book");
                    }
                }

                case 6: {
                    String name = getName();
                    String phone = getNumber();
                    System.out.println("Enter new number:");
                    String newNumber = scanner.next();
                    if (phoneBook.changeNumber(phone, name, newNumber)) {
                        System.out.println("Number changed successfully");
                    } else {
                        System.out.println("Such an entry does not exist in the phone book");
                    }
                }

                case 7: {
                    Map<User, List<PhoneNumber>> pairs = phoneBook.getAllPairs();
                    pairs.forEach((user, phoneNumbers) -> phoneNumbers.forEach(phoneNumber -> System.out.println(user.getName() + " " + phoneNumber.getNumber())));
                }
            }
        }
    }
}
