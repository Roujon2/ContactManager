package com.roujon.contactbook;

// Imports 
import java.util.Scanner;

public class Main {

    // Database connection param
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/data/contact_manager.db";

    public static void main(String[] args) {
        // Create database manager
        DatabaseManager dbManager = new DatabaseManager(DB_URL);

        // Main menu
        User user = mainMenu(dbManager);

        if(user != null){
            // Contact book menu
            contactBookMenu(dbManager, user);
        }

    }

    
    public static User mainMenu(DatabaseManager dbManager) {
        // User to determine login
        User user = null;

        Scanner scanner = new Scanner(System.in);

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
    public static void contactBookMenu(DatabaseManager db, User user){
        Scanner scanner = new Scanner(System.in);

        Boolean running = true;

        while(running){
            System.out.println("\nContact Book\n");
            System.out.println("1. Add contact");
            System.out.println("2. Show contacts");
            System.out.println("3. Search contact");
            System.out.println("4. Delete contact");
            System.out.println("5. Exit");
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
                    //showContacts();
                    break;
                
                case 3:
                    // Search contact
                    System.out.println("\nSearch Contact\n");
                    break;

                case 4:
                    // Delete contact
                    System.out.println("\nDelete Contact\n");

                    break;
                case 5:
                    // Exit
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        
        scanner.close();
    }

}