# Contact Manager
A simple Java application allowing users to manage their contacts. Through the command line,
users can register and login to their account, add contacts, view contacts, and delete contacts.
The application uses SQLite to store the contact information and follows ACID principles to ensure
data integrity and reliability.

## How to run
1. Clone the repository
```
git clone https://github.com/Roujon2/ContactManager.git
```
2. Navigate to the project directory
3. Compile the project
```
mvn package
```
4. Run the project
```
java -jar target/addressbook-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Technologies Used
- Java
- SQLite
- JDBC
