import database.DatabaseConnection;
import views.LoginView;
import javax.swing.*;
import java.sql.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        // Initialiser la base de données
        initialiserBaseDeDonnees();

        // Lancer l'interface graphique
        SwingUtilities.invokeLater(LoginView::new);
    }

    private static void initialiserBaseDeDonnees() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            // Vérifier si les tables existent déjà
            ResultSet rs = conn.getMetaData().getTables(null, null, "utilisateurs", null);
            if (!rs.next()) {
                // Lire et exécuter le script SQL
                InputStream is = Main.class.getResourceAsStream("/resources/sql/init_database.sql");
                if (is == null) {
                    is = new FileInputStream("src/resources/sql/init_database.sql");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sql = new StringBuilder();
                String ligne;
                while ((ligne = reader.readLine()) != null) {
                    if (!ligne.startsWith("--") && !ligne.trim().isEmpty()) {
                        sql.append(ligne).append(" ");
                        if (ligne.trim().endsWith(";")) {
                            conn.createStatement().execute(
                                    sql.toString().trim().replace(";", ""));
                            sql = new StringBuilder();
                        }
                    }
                }
                System.out.println("Base de données créée avec succès !");
            } else {
                System.out.println("Base de données déjà existante !");
            }
        } catch (Exception e) {
            System.out.println("Erreur initialisation DB : " + e.getMessage());
        }
    }
}