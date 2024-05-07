package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

interface CartCalculator {
    double calculateTotalCost(List<String> cart);
}

public class MovieSelection implements CartCalculator {
    // JDBC URL, username, and password
    private static final String url = "jdbc:mysql://localhost:3306/movie_oop";
    private static final String user = "root";
    private static final String password = "password";

    private String movieName; // Declaration of movieName variable
    private double amount; // Declaration of amount variable




    public static void main(String[] args) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, user, password);

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

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }

    // Method to print out the list of movies with actors and prices
    static void MoviesAndPrices(Connection connection) throws SQLException {
        String query = "SELECT m.movie_id, m.movie_name, m.movie_genre, m.amount, a.actor_name " +
                "FROM movies m " +
                "JOIN filmactor fa ON m.movie_id = fa.movie_id " +
                "JOIN actor a ON fa.actor_id = a.actor_id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int movieId = resultSet.getInt("movie_id");
            String movieName = resultSet.getString("movie_name");
            String movieGenre = resultSet.getString("movie_genre");
            double amount = resultSet.getDouble("amount");
            String actorName = resultSet.getString("actor_name");

            System.out.println(movieId + ". " + movieName + " (" + movieGenre + ") - €" + amount + " - " + actorName);
        }
    }

    // Method to get the name of the selected movie
    static String getMovie(Connection connection, int movieId) throws SQLException {
        String query = "SELECT movie_name FROM movies WHERE movie_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, movieId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("movie_name");
        }
        return null;
    }


    @Override
    public double calculateTotalCost(List<String> cart) {
        double totalPrice = 0;
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            for (String movie : cart) {
                double price = getMoviePrice(connection, movie);
                totalPrice += price;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving movie prices: " + e.getMessage());
        }
        return totalPrice;
    }

    // Method to get the price of a movie from the database
    private double getMoviePrice(Connection connection, String movieName) throws SQLException {
        String query = "SELECT amount FROM movies WHERE movie_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, movieName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("amount");
                }
            }
        }
        return 0; // Default to 0 if movie not found or price not available
    }

    public static void addPayment(Connection connection, double totalCost) throws SQLException {
        String customerName = CustomerDetail.getLoggedInCustomerName();
        int customerId = getCustomerId(connection, customerName); // Retrieve customer ID

        if (customerId != -1) {
            String paymentQuery = "INSERT INTO Payment (amount, customer_id, customer_name) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(paymentQuery)) {
                preparedStatement.setDouble(1, totalCost);
                preparedStatement.setInt(2, customerId);
                preparedStatement.setString(3, customerName);
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Payment inserted successfully.");
                } else {
                    System.out.println("Failed to insert payment.");
                }
            } catch (SQLException e) {
                System.err.println("Failed to insert payment: " + e.getMessage());
            }
        } else {
            System.out.println("Customer not found.");
        }
    }


    private static int getCustomerId(Connection connection, String customerName) throws SQLException {
        String query = "SELECT customer_id FROM Customer WHERE customer_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, customerName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("customer_id");
                }
            }
        }
        return 0; // Default to 0 if customer ID not found
    }
}
