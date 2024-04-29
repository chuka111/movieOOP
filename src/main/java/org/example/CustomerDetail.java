package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

public class CustomerDetail {

    private static String loggedInCustomer; // Static variable to store the logged-in customer name
    private static String loggedInCustomerName;

    // JDBC URL, username, and password
    private static final String url = "jdbc:mysql://localhost:3306/movie_oop";
    private static final String user = "root";
    private static final String password = "password";

    // Constructor to set the logged-in customer name
    public CustomerDetail(String name) {
        loggedInCustomer = name;
    }

    private String customerName; // Declaration of customerName variable
    private String cpassword; // Declaration of cpassword variable
    private List<String> cart;

    public static void main(String[] args) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, user, password);

            // User interface
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;
            while (!exit) {
                System.out.println("\nOptions:");
                System.out.println("1. Login");
                System.out.println("2. Create a new login");
                System.out.println("3. Delete a user");
                System.out.println("4. Change username");
                System.out.println("5. Change password");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (option) {
                    case 1:
                        login(connection);
                        break;
                    case 2:
                        createNewLogin(connection);
                        break;
                    case 3:
                        deleteUser(connection);
                        break;
                    case 4:
                        changeUsername(connection);
                        break;
                    case 5:
                        changePassword(connection);
                        break;
                    case 6:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }

    // Method to log in
    static String login(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nLogin:");
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Validate customer credentials
        if (validateCustomer(connection, customerName, password)) {
            System.out.println("Login successful!");
            // Set the logged-in customer
            loggedInCustomer = customerName;
            // Proceed with other actions after successful login
        } else {
            System.out.println("Invalid customer name or password. Please try again.");
        }
        return customerName;
    }

    // Method to validate customer credentials
    static boolean validateCustomer(Connection connection, String customerName, String password) throws SQLException {
        String query = "SELECT * FROM Customer WHERE customer_name = ? AND password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, customerName);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Method to create a new login
    static void createNewLogin(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nCreating a new login:");
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Check if the customer already exists
        if (customerExists(connection, customerName)) {
            System.out.println("Customer already exists. Please choose a different name.");
            return;
        }

        // Insert new customer with password
        String insertQuery = "INSERT INTO Customer (customer_name, password) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, customerName);
        preparedStatement.setString(2, password);
        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("New login created successfully.");
        } else {
            System.out.println("Failed to create new login.");
        }
    }

    // Method to delete a user
    static void deleteUser(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nDeleting a user:");
        System.out.print("Enter customer name to delete: ");
        String customerName = scanner.nextLine();

        // Check if the customer exists
        if (!customerExists(connection, customerName)) {
            System.out.println("Customer does not exist.");
            return;
        }

        // Delete the user
        String deleteQuery = "DELETE FROM Customer WHERE customer_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
        preparedStatement.setString(1, customerName);
        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Failed to delete user.");
        }
    }

    // Method to change username
    static void changeUsername(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nChanging username:");
        System.out.print("Enter current customer name: ");
        String currentName = scanner.nextLine();

        // Check if the customer exists
        if (!customerExists(connection, currentName)) {
            System.out.println("Customer does not exist.");
            return;
        }

        System.out.print("Enter new customer name: ");
        String newName = scanner.nextLine();

        // Update the username
        String updateQuery = "UPDATE Customer SET customer_name = ? WHERE customer_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, currentName);
        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Username updated successfully.");
        } else {
            System.out.println("Failed to update username.");
        }
    }

    // Method to change password
    static void changePassword(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nChanging password:");
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();

        // Check if the customer exists
        if (!customerExists(connection, customerName)) {
            System.out.println("Customer does not exist.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        // Update the password
        String updateQuery = "UPDATE Customer SET password = ? WHERE customer_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
        preparedStatement.setString(1, newPassword);
        preparedStatement.setString(2, customerName);
        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Failed to update password.");
        }
    }

    // Method to check if customer exists
    static boolean customerExists(Connection connection, String customerName) throws SQLException {
        String query = "SELECT * FROM Customer WHERE customer_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, customerName);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }



    // Getter for customer name
    public String getCustomerName() {
        return customerName;
    }

    // Getter for password
    public String getCPassword() {
        return cpassword;
    }

    // Getter for cart
    public List<String> getCart() {
        return cart;
    }

    public static String getLoggedInCustomerName() {
        return loggedInCustomerName;
    }

}
