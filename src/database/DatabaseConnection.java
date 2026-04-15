package database;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:univscheduler.db";
    private static Connection connection = null;

    // Connexion à la base de données
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                initialiserBase();
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        return connection;
    }

    // Initialiser la base de données avec le script SQL
    private static void initialiserBase() {
        try {
            Statement stmt = connection.createStatement();
            // Activer les clés étrangères dans SQLite
            stmt.execute("PRAGMA foreign_keys = ON");
            System.out.println("Base de données initialisée !");
        } catch (SQLException e) {
            System.out.println("Erreur initialisation : " + e.getMessage());
        }
    }

    // Fermer la connexion
    public static void fermerConnexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur fermeture : " + e.getMessage());
        }
    }
}