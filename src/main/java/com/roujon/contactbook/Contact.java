package com.roujon.contactbook;

public class Contact {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int userId;

    // Contructor
    public Contact(String firstName, String lastName, String email, String phone, int userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public int getUserId() {
        return userId;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // toString
    @Override
    public String toString() {
        String str = firstName + " " + lastName + "\n" + email + "\n" + phone + "\n";
        return str;
    }

    
}
