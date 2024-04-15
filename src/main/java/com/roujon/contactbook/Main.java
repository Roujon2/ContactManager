package com.roujon.contactbook;

import java.util.List;
// Imports 
import java.util.Scanner;

public class Main {

    // Database connection param
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/data/contact_manager.db";

    public static void main(String[] args) {
        // Create database manager
        DatabaseManager dbManager = new DatabaseManager(DB_URL);

        // Scanner for input
        Scanner scanner = new Scanner(System.in);

        // Main menu
        User user = mainMenu(dbManager, scanner);

        if(user != null){
            // Contact book menu
            contactBookMenu(dbManager, user, scanner);
        }

        // Close scanner
        scanner.close();
    }

    
    public static User mainMenu(DatabaseManager dbManager, Scanner scanner) {
        // User to determine login
        User user = null;

        // Display menu
        System.out.println("\nWelcome to Contact Manager!\n");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.println("Enter your choice: ");

        // Read user input
        int choice = scanner.nextInt();

        switch(choice){
            case 1:
                // Login
                System.out.println("\nLogin\n");
                System.out.print("Enter username: ");
                String username = scanner.next();
                System.out.print("Enter password: ");
                String password = scanner.next();

                user = dbManager.login(username, password);

                if(user != null){
                    System.out.println("\nLogin successful\n");
                } else {
                    System.out.println("\nInvalid credentials\n");
                }
                break;
            case 2:
                // Register
                System.out.println("\nRegister\n");
                System.out.print("Enter username: ");
                String regUsername = scanner.next();
                System.out.print("Enter password: ");
                String regPassword = scanner.next();

                User regUser = new User(regUsername, regPassword);

                if(dbManager.register(regUser)){
                    System.out.println("\nRegistration successful\nLogging in...\n");
                    user = dbManager.login(regUsername, regPassword);
                } else {
                    System.out.println("\nRegistration failed\n");
                }
                break;
            case 3:
                // Exit
                System.out.println("\nGoodbye!\n");
                break;
            default:
                System.out.println("Invalid choice");
        }
        scanner.nextLine();
        return user;
    }

    // Contact book menu
    public static void contactBookMenu(DatabaseManager db, User user, Scanner scanner){

        Boolean running = true;

        while(running){
            System.out.println("\nContact Book\n");
            System.out.println("1. Add contact");
            System.out.println("2. Show contacts");
            System.out.println("3. Search contact");
            System.out.println("4. Delete contact");
            System.out.println("5. Exit");
            System.out.println("6. Delete User");
            System.out.println("Enter your choice: ");

            // Read user input
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1:
                    // Getting contact info
                    System.out.println("\nAdd Contact");
                    System.out.print("Enter first name: ");
                    String firstName = scanner.nextLine();
            
                    System.out.print("Enter last name: ");
                    String lastName = scanner.nextLine();
            
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
            
                    System.out.print("Enter phone number: ");
                    String phone = scanner.nextLine();

                    // Create contact object
                    Contact contact = new Contact(firstName, lastName, email, phone, user.getId());

                    // Add contact to database
                    db.addContact(contact);
                    break;

                case 2:
                    // Show contacts
                    System.out.println("\nContact List\n");
                    List<Contact> contacts = db.showContacts(user);

                    if(contacts.size() > 0){
                        for(Contact c : contacts){
                            System.out.println(c);
                        }
                    } else {
                        System.out.println("No contacts found");
                    }
                    
                    break;
                
                case 3:
                    // Search contact
                    System.out.println("\nSearch Contact\n");
                    System.out.print("Enter contact first name: ");
                    String searchFirstName = scanner.nextLine();
                    System.out.print("Enter contact last name: ");
                    String searchLastName = scanner.nextLine();
                    System.out.println();

                    // Search the contact
                    List<Contact> searchContacts = db.searchContacts(user, searchFirstName, searchLastName);

                    if(searchContacts.size() > 0){
                        for(Contact c : searchContacts){
                            System.out.println(c);
                        }
                    } else {
                        System.out.println("No contacts found");
                    }
                    break;

                case 4:
                    // Delete contact
                    System.out.println("\nDelete Contact\n");
                    System.out.print("Enter contact first name: ");
                    String contactFirstName = scanner.nextLine();
                    System.out.print("Enter contact last name: ");
                    String contactLastName = scanner.nextLine();

                    // Search the contact
                    List<Contact> deleteContacts = db.searchContacts(user, contactFirstName, contactLastName);

                    // Get the first contact
                    if(deleteContacts.size() > 0){
                        Contact deleteContact = deleteContacts.get(0);
                        System.out.println("Are you sure you want to delete the following contact? y/n");
                        System.out.println();
                        System.out.println(deleteContact);
                        String confirm = scanner.nextLine();

                        if(!confirm.equals("y")){
                            System.out.println("Operation cancelled");
                            break;
                        }
                        Boolean success = db.deleteContact(user, deleteContact);
                        if(success){
                            System.out.println("Contact deleted");
                        }else{
                            System.out.println("Failed to delete contact");
                        }
                    } else {
                        System.out.println("No contacts found");
                    }

                    break;
                case 5:
                    // Exit
                    running = false;
                    break;
                case 6:
                    System.out.println("WARNING: This will delete all user data and associated contacts. Are you sure you want to delete your account? y/n");
                    String confirm = scanner.nextLine();

                    if(confirm.equals("y")){
                        Boolean success = db.deleteUser(user);
                        if(success){
                            System.out.println("User deleted");
                            running = false;
                        }else{
                            System.out.println("Failed to delete user");
                        }
                    }else{
                        System.out.println("Operation cancelled");
                    }
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

}