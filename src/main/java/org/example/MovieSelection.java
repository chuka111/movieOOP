package org.example;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
public class MovieSelection {
    // JDBC URL, username, and password
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
                // Print out the movies with actors and prices
                System.out.println("\nList of Movies with Actors and Prices:");
                printMoviesWithActorsAndPrices(connection);

                // Ask the user to pick a movie
                System.out.print("Pick a movie (Enter movie ID) or enter 0 to exit: ");
                int movieId = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (movieId == 0) {
                    exit = true;
                    System.out.println("Exiting...");
                } else {
                    // Get the selected movie
                    String selectedMovie = getMovie(connection, movieId);
                    if (selectedMovie != null) {
                        System.out.println("You picked: " + selectedMovie);
                    } else {
                        System.out.println("Movie with ID " + movieId + " not found.");
                    }
                }
            }

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }

    // Method to print out the list of movies with actors and prices
    static void printMoviesWithActorsAndPrices(Connection connection) throws SQLException {
        String query = "SELECT m.movie_id, m.movie_name, m.movie_genre, m.amount, a.actor_name " +
                "FROM movies m " +
                "JOIN filmactor fa ON m.movie_id = fa.movie_id " +
                "JOIN actor a ON fa.actor_id = a.actor_id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<String> movies = new ArrayList<>(); // ArrayList to store movie details

        while (resultSet.next()) {
            int movieId = resultSet.getInt("movie_id");
            String movieName = resultSet.getString("movie_name");
            String movieGenre = resultSet.getString("movie_genre");
            double amount = resultSet.getDouble("amount");
            String actorName = resultSet.getString("actor_name");

            String movieDetails = movieId + ". " + movieName + " (" + movieGenre + ") - €" + amount + " - " + actorName;
            movies.add(movieDetails); // Add movie details to the ArrayList
        }

        // Print all movie details from the ArrayList
        for (String movie : movies) {
            System.out.println(movie);
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

}
