package com.roujon.contactbook;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private Connection connection;

    // Constructor
    public DatabaseManager(String dbUrl) {
        // Check if the database file exists
        File dbFile = new File(dbUrl.replace("jdbc:sqlite:", ""));
        if (!dbFile.exists()) {
            createDatabase(dbUrl); // Initialize database
        }

        try {
            // Connect to the database
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }


    // Initialize database
    public void createDatabase(String dbUrl) {
        try {
            // Connect to the database
            connection = DriverManager.getConnection(dbUrl);
            // Create users table
            String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL UNIQUE,"
                    + "password TEXT NOT NULL"
                    + ");";

            // Create contacts table
            String createContactsTableSql = "CREATE TABLE IF NOT EXISTS contacts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INTEGER NOT NULL,"
                    + "first_name TEXT NOT NULL,"
                    + "last_name TEXT NOT NULL,"
                    + "email TEXT NOT NULL,"
                    + "phone TEXT NOT NULL,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id)"
                    + ");";

            connection.createStatement().executeUpdate(createUsersTableSql);
            connection.createStatement().executeUpdate(createContactsTableSql);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to create database");
            e.printStackTrace();
        }
    }


    // Contact table features
    // Add contact
    public void addContact(Contact contact) {
        try{
            Statement stmt = connection.createStatement();

            // Insert the contact into the database
            String insertSql = "INSERT INTO contacts (first_name, last_name, email, phone, user_id) VALUES ('"
                    + contact.getFirstName() + "', '"
                    + contact.getLastName() + "', '"
                    + contact.getEmail() + "', '"
                    + contact.getPhone() + "', "
                    + contact.getUserId() + ");";

            stmt.executeUpdate(insertSql);

            System.out.println("Contact created successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to create contact");
            System.out.println(e.getMessage());
        }
    }
    // Show contacts
    public List<Contact> showContacts(User user) {
        List<Contact> contacts = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();

            // Query the database for the contacts
            String querySql = "SELECT * FROM contacts WHERE user_id = " + user.getId() + ";";

            // Get result
            ResultSet result = stmt.executeQuery(querySql);

            // Loop through the result and create contact objects
            while(result.next()){
                Contact contact = new Contact(
                    result.getString("first_name"),
                    result.getString("last_name"),
                    result.getString("email"),
                    result.getString("phone"),
                    user.getId()
                );
                contacts.add(contact);
            }

        } catch (SQLException e) {
            System.err.println("Failed to show contacts");
            System.out.println(e.getMessage());
        }

        return contacts;
    }


    // User table features
    // Login function
    public User login(String username, String password) {
        User user = null;
        try {
            Statement stmt = connection.createStatement();

            // Query the database for the user
            String querySql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "';";

            ResultSet result = stmt.executeQuery(querySql);
            // Check if the user exists
            if (!result.next()) {
                return null;
            }

            user = new User(username, password);
            user.setId(result.getInt("id"));

        } catch (SQLException e) {
            System.err.println("Failed to login.");
            System.out.println(e.getMessage());
        }

        return user;
    }
    // Register function
    public boolean register(User user) {
        boolean success = false;
        try {
            Statement stmt = connection.createStatement();

            // Insert the user into the database
            String insertSql = "INSERT INTO users (username, password) VALUES ('"
                    + user.getUsername() + "', '"
                    + user.getPassword() + "');";

            stmt.executeUpdate(insertSql);

            success = true;

        } catch (SQLException e) {
            System.err.println("Failed to register user");
            System.out.println(e.getMessage());
            // Check if the username is already taken
            if(e.getErrorCode() == 1062){
                System.err.println("Username already taken");
            }
        }

        return success;
    }

    
}
