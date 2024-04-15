package com.roujon.contactbook;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            // Insert the contact into the database
            String insertSql = "INSERT INTO contacts (first_name, last_name, email, phone, user_id) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement pstmt = connection.prepareStatement(insertSql);

            // Set the parameters
            pstmt.setString(1, contact.getFirstName());
            pstmt.setString(2, contact.getLastName());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getPhone());
            pstmt.setInt(5, contact.getUserId());
            pstmt.executeUpdate();

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
            // Query the database for the contacts
            String querySql = "SELECT * FROM contacts WHERE user_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(querySql);
            pstmt.setInt(1, user.getId());

            // Get result
            ResultSet result = pstmt.executeQuery();

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
    // Search contact
    public List<Contact> searchContacts(User user, String firstName, String lastName){
        List<Contact> contacts = new ArrayList<>();
        try {
            // Query the database for the contact
            String querySql = "SELECT * FROM contacts WHERE first_name = ? AND last_name = ? AND user_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(querySql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setInt(3, user.getId());

            // Get result
            ResultSet result = pstmt.executeQuery();

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
            System.err.println("Failed to search contact");
            System.out.println(e.getMessage());
        }

        return contacts;
    }
    // Delete contact
    public Boolean deleteContact(User user, Contact contact) {
        try {
            // Delete the contact from the database
            String deleteSql = "DELETE FROM contacts WHERE first_name = ? AND last_name = ? AND user_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(deleteSql);

            // Set the parameters
            pstmt.setString(1, contact.getFirstName());
            pstmt.setString(2, contact.getLastName());
            pstmt.setInt(3, user.getId());
            
            pstmt.executeUpdate();  

            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    // User table features
    // Login function
    public User login(String username, String password) {
        User user = null;
        try {
            // Query the database for the user
            String querySql = "SELECT * FROM users WHERE username = ? AND password = ?;";
            PreparedStatement pstmt = connection.prepareStatement(querySql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet result = pstmt.executeQuery();
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
            // Insert the user into the database
            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?);";
            PreparedStatement pstmt = connection.prepareStatement(insertSql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());

            pstmt.executeUpdate();

            success = true;

        } catch (SQLException e) {
            System.err.println("\nFailed to register user");
            System.out.println(e.getMessage());

            // Check if the username is already taken
            if(e.getErrorCode() == 19){
                System.err.println("Username already taken");
            }
        }

        return success;
    }
    // Delete user (also deleting all associated contacts)
    public Boolean deleteUser(User user){
        try{
            // Begin transaction
            connection.setAutoCommit(false); // Ensures that queries can be rolled back

            // Delete all contacts for user
            deleteAllContacts(user);


            // Delete the user from the database
            String deleteSql = "DELETE FROM users WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(deleteSql);
            pstmt.setInt(1, user.getId());
            int rowsAff = pstmt.executeUpdate();

            // Commit if nothing went wrong
            connection.commit();

            return rowsAff > 0;
        
        // Catch all the errors and try to rollback
        }catch(SQLException e){
            System.err.println("Failed to delete user");
            System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException e1) {
                System.err.println("Failed to rollback transaction");
                System.out.println(e1.getMessage());
            }
            return false;

        // Reset the autocommit
        }finally{
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Failed to set auto commit to true");
                System.out.println(e.getMessage());
            }
        
        }
    }
    // Delete all contacts for user
    private void deleteAllContacts(User user) throws SQLException{
        String deleteSql = "DELETE FROM contacts WHERE user_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(deleteSql);
        pstmt.setInt(1, user.getId());
        pstmt.executeUpdate();
    }

    
}
