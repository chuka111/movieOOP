package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerDetail {
    // JDBC URL, username, and password
    private static final String url = "jdbc:mysql://localhost:3306/movie_oop";
    private static final String user = "root";
    private static final String password = "password";

    public static void main(String[] args) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, user, password);

            // Ask the user for options
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;
            while (!exit) {
                System.out.println("1. Create new customer");
                System.out.println("2. Update customer name");
                System.out.println("3. Delete customer");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (option) {
                    case 1:
                        // Create new customer
                        System.out.print("Enter customer name: ");
                        String newName = scanner.nextLine();
                        insertCustomer(connection, newName);
                        break;
                    case 2:
                        // Update customer name
                        System.out.print("Enter current customer name: ");
                        String currentName = scanner.nextLine();
                        System.out.print("Enter new customer name: ");
                        String updatedName = scanner.nextLine();
                        updateCustomerName(connection, currentName, updatedName);
                        break;
                    case 3:
                        // Delete customer
                        System.out.print("Enter customer name to delete: ");
                        String nameToDelete = scanner.nextLine();
                        deleteCustomer(connection, nameToDelete);
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }

    // Insert new customer into the customer table
    static void insertCustomer(Connection connection, String name) throws SQLException {
        String insertSQL = "INSERT INTO Customer (customer_name) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, name);
        preparedStatement.executeUpdate();
        System.out.println("Customer '" + name + "' added successfully\n");
    }

    // this code is simply just to insert the customers name onto the customer table
    static void updateCustomerName(Connection connection, String currentName, String newName) throws SQLException {
        String updateSQL = "UPDATE Customer SET customer_name = ? WHERE customer_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, currentName);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Customer name updated successfully\n");
        } else {
            System.out.println("Failed to update customer name. Customer with current name '" + currentName + "' not found.\n");
        }
    }

    // code writen to check if the customer enters the right name and if the name is right it updates the name of their current
    // customer name with the new name they choose
    static void deleteCustomer(Connection connection, String name) throws SQLException {
        String deleteSQL = "DELETE FROM Customer WHERE customer_name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setString(1, name);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Customer '" + name + "' deleted successfully\n");
        } else {
            System.out.println("Unsuccessful. Customer with name '" + name + "' not found.\n");
        }
    }
}





