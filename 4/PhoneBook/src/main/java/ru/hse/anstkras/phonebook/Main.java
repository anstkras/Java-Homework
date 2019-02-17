package ru.hse.anstkras.phonebook;

import ru.hse.anstkras.phonebook.Entities.PhoneNumber;
import ru.hse.anstkras.phonebook.Entities.User;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var phoneBook = new PhoneBook("phonebook");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int command = scanner.nextInt();
            switch (command) {
                case 0:
                    return;
                case 1: {
                    System.out.println("Enter name:");
                    String name = scanner.next();
                    System.out.println("Enter phone number");
                    String phoneNumber = scanner.next();
                    phoneBook.addEntry(name, phoneNumber);
                    break;
                }
                case 2: {
                    System.out.println("Enter name:");
                    String name = scanner.next();
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
                    System.out.println("Enter number:");
                    String number = scanner.next();
                    List<User> users = phoneBook.getNamesByPhoneNumber(number);
                    if (users == null) {
                        System.out.println("There is no such a number in the phone book");
                        break;
                    }
                    System.out.println("Names:");
                    users.forEach(user -> System.out.println(user.getName()));
                    break;
                }

            }
        }
    }
}
