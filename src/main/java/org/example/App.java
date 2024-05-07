package org.example;

import org.example.CustomerDetail;
import org.example.MovieSelection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
public class App {

    private static final String url = "jdbc:mysql://localhost:3306/movie_oop";
    private static final String user = "root";
    private static final String password = "password";

    public static void main(String[] args) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, user, password);

            Scanner scanner = new Scanner(System.in);

            boolean exit = false;
            while (!exit) {
                System.out.println("\nOptions:");
                System.out.println("1. Login");
                System.out.println("2. Create a new login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (option) {
                    case 1:
                        // Call the login method of CustomerDetail class
                        String loggedInCustomer = CustomerDetail.login(connection);
                        // Retrieve the logged-in customer's name
                        String loggedInCustomerName = CustomerDetail.getLoggedInCustomerName();

                        // Now you can use the logged-in customer's name in your code
                        System.out.println("Logged in as: " + loggedInCustomerName);
                        // Check if login was successful
                        if (loggedInCustomer != null) {
                            System.out.println("Logged in as: " + loggedInCustomer);
                            System.out.println("\nOptions:");
                            System.out.println("1. Shop");
                            System.out.println("2. Delete or Change login details");
                            int choice = scanner.nextInt();
                            switch (choice){
                                case 1:
                                    handleMovieSelection(connection);
                                    break;
                                case 2:
                                    System.out.println("\nOptions:");
                                    System.out.println("1. change username");
                                    System.out.println("2. change password");
                                    System.out.println("3. delete user");
                                    int detail = scanner.nextInt();

                                    switch (detail) {
                                        case 1:
                                            CustomerDetail.changeUsername(connection);
                                            break;
                                        case 2:
                                            CustomerDetail.changePassword(connection);
                                            break;
                                        case 3:
                                            // Code to execute if choice is 3
                                            System.out.println("Option 3 selected");
                                            break;
                                        default:
                                            CustomerDetail.deleteUser(connection);
                                            break;
                                    }
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }

                        } else {
                            System.out.println("Login failed.");
                        }
                        break;
                    case 2:
                        // Call the createNewLogin method of CustomerDetail class
                        CustomerDetail.createNewLogin(connection);
                        break;
                    case 3:
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

    private static void handleMovieSelection(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        List<String> cart = new ArrayList<>(); // Cart to store selected movies

        boolean exit = false;
        while (!exit) {
            // Print out the movies with actors and prices
            System.out.println("\nList of Movies with Actors and Prices:");
            MovieSelection.MoviesAndPrices(connection);

            // Ask the user to pick a movie
            System.out.print("Pick a movie (Enter movie ID) or enter 0 to exit: ");
            int movieId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (movieId == 0) {
                exit = true;
                System.out.println("Exiting...");
            } else {
                // Get the selected movie
                String selectedMovie = MovieSelection.getMovie(connection, movieId);
                if (selectedMovie != null) {
                    cart.add(selectedMovie); // Add the selected movie to the cart
                    System.out.println("Added to cart: " + selectedMovie);
                } else {
                    System.out.println("Movie with ID " + movieId + " not found.");
                }
            }
        }

        // Calculate and display total price
        CartCalculator calculator = new MovieSelection();
        double totalCost = calculator.calculateTotalCost(cart);
        System.out.println("Total Price: €" + totalCost);



        // Add payment to the Payment table
        MovieSelection.addPayment(connection, totalCost);


        // Close the connection
        connection.close();
    }

    }


