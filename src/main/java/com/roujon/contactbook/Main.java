package com.roujon.contactbook;

// Imports 
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class Main {

    // Database connection param
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/data/contact_book.db";

    public static void main(String[] args) {
        // Database initialization
        initDatabase();

        Scanner scanner = new Scanner(System.in);

        // Establish connection to the database
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Connected to database successfully!");

            Boolean running = true;

            while(running){

                System.out.println("\nContact Book\n");
                System.out.println("1. Add contact");
                System.out.println("2. Show contacts");
                System.out.println("3. Search contact");
                System.out.println("4. Exit");
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
                        Contact contact = new Contact(firstName, lastName, email, phone);

                        // Add contact to database
                        addContact(contact);
                        break;

                    case 2:
                        // Show contacts
                        System.out.println("\nContact List\n");
                        showContacts();
                        break;
                    
                    case 3:
                        // Search contact
                        System.out.println("\nSearch Contact\n");
                        System.out.print("Enter first name: ");
                        String searchFirstName = scanner.nextLine();

                        Contact searchContact = searchContact(searchFirstName);

                        if(searchContact != null){
                            System.out.println("\nResults\n");
                            System.out.println(searchContact);
                        } else {
                            System.out.println("\nContact not found\n");
                        }
                        break;

                    case 4:
                        // Exit
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice");
                }

            }

        
        } catch (SQLException e) {
            System.err.println("Failed to connect to database");
            System.out.println(e.getMessage());
        }

        scanner.close();

    }

    // Database initialization
    private static void initDatabase() {
        // Check database file exists
        File dbFile = new File(DB_URL.replace("jdbc:sqlite:", ""));
        if (!dbFile.exists()) {
            // Create the database
            System.out.println("Database does not exist, creating database...");
            createDatabase();
        }
        return;
    }

    // Create database func
    private static void createDatabase() {
        // Create the database
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();

            // Create the table, defining the schema
            String createTableSql = "CREATE TABLE IF NOT EXISTS contacts (\n"
                        + "id INTEGER PRIMARY KEY,\n"
                        + "first_name TEXT NOT NULL,\n"
                        + "last_name TEXT NOT NULL,\n"
                        + "email TEXT NOT NULL,\n"
                        + "phone TEXT NOT NULL\n"
                        + ");";
            
            stmt.executeUpdate(createTableSql);

            System.out.println("Database created successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to create database");
            System.out.println(e.getMessage());
        }
    }

    // Add contact
    public static void addContact(Contact contact) {
        // Establish connection to the database
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();

            // Insert the contact into the database
            String insertSql = "INSERT INTO contacts (first_name, last_name, email, phone) VALUES ('"
                                + contact.getFirstName() + "', '" 
                                + contact.getLastName() + "', '" 
                                + contact.getEmail() + "', '" 
                                + contact.getPhone() + "');";

            stmt.executeUpdate(insertSql);

            System.out.println("Contact created successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to create contact");
            System.out.println(e.getMessage());
        }
    }

    // Show contacts
    public static void showContacts() {
        // Establish connection to the database
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();

            // Select all contacts from the database
            String selectSql = "SELECT * FROM contacts;";
            // Result
            ResultSet result = stmt.executeQuery(selectSql);
            
            // Loop through the result set and create a contact object for each row
            while(result.next()) {
                Contact contact = new Contact(result.getString("first_name"), result.getString("last_name"), result.getString("email"), result.getString("phone"));
                System.out.println(contact);
            }

        } catch (SQLException e) {
            System.err.println("Failed to show contacts");
            System.out.println(e.getMessage());
        }
    }

    // Search contact
    public static Contact searchContact(String firstName) {
        // Establish connection to the database
        try(Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();

            // Select contact from the database
            String selectSql = "SELECT * FROM contacts WHERE first_name = '" + firstName + "';";
            // Result
            ResultSet result = stmt.executeQuery(selectSql);
            
            // Loop through the result set and create a contact object for each row
            while(result.next()) {
                Contact contact = new Contact(result.getString("first_name"), result.getString("last_name"), result.getString("email"), result.getString("phone"));
                return contact;
            }

        } catch (SQLException e) {
            System.err.println("Failed to search contact");
            System.out.println(e.getMessage());
        }
        return null;
    }
}