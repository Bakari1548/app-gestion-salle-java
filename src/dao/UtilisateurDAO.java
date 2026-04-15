package dao;

import database.DatabaseConnection;
import models.Utilisateur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // Récupérer tous les utilisateurs
    public List<Utilisateur> getTousLesUtilisateurs() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE est_actif = 1";
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                liste.add(extraireUtilisateur(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return liste;
    }

    // Connexion utilisateur
    public Utilisateur connexion(String nomUtilisateur, String motDePasse) {
        String sql = "SELECT * FROM utilisateurs WHERE nom_utilisateur = ? AND mot_de_passe = ?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, nomUtilisateur);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extraireUtilisateur(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur connexion : " + e.getMessage());
        }
        return null;
    }

    // Ajouter un utilisateur
    public boolean ajouter(Utilisateur u) {
        String sql = "INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, email, prenom, nom, role) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, u.getNomUtilisateur());
            ps.setString(2, u.getMotDePasse());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPrenom());
            ps.setString(5, u.getNom());
            ps.setString(6, u.getRole());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
            return false;
        }
    }

    // Supprimer un utilisateur
    public boolean supprimer(int id) {
        String sql = "UPDATE utilisateurs SET est_actif = 0 WHERE id = ?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur suppression : " + e.getMessage());
            return false;
        }
    }

    // Extraire un utilisateur du ResultSet
    private Utilisateur extraireUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getInt("id"));
        u.setNomUtilisateur(rs.getString("nom_utilisateur"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setEmail(rs.getString("email"));
        u.setPrenom(rs.getString("prenom"));
        u.setNom(rs.getString("nom"));
        u.setRole(rs.getString("role"));
        u.setDepartement(rs.getString("departement"));
        u.setGroupeClasse(rs.getString("groupe_classe"));
        u.setEstActif(rs.getInt("est_actif") == 1);
        return u;
    }
}