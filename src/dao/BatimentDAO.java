package dao;

import database.DatabaseConnection;
import models.Batiment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatimentDAO {

    // Récupérer tous les bâtiments
    public List<Batiment> getTousLesBatiments() {
        List<Batiment> liste = new ArrayList<>();
        String sql = "SELECT * FROM batiments";
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                liste.add(extraireBatiment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return liste;
    }

    // Ajouter un bâtiment
    public boolean ajouter(Batiment b) {
        String sql = "INSERT INTO batiments (nom, code, localisation, nombre_etages, description) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, b.getNom());
            ps.setString(2, b.getCode());
            ps.setString(3, b.getLocalisation());
            ps.setInt(4, b.getNombreEtages());
            ps.setString(5, b.getDescription());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
            return false;
        }
    }

    // Modifier un bâtiment
    public boolean modifier(Batiment b) {
        String sql = "UPDATE batiments SET nom = ?, localisation = ?, " +
                "nombre_etages = ?, description = ? WHERE id = ?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, b.getNom());
            ps.setString(2, b.getLocalisation());
            ps.setInt(3, b.getNombreEtages());
            ps.setString(4, b.getDescription());
            ps.setInt(5, b.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur modification : " + e.getMessage());
            return false;
        }
    }

    // Supprimer un bâtiment
    public boolean supprimer(int id) {
        String sql = "DELETE FROM batiments WHERE id = ?";
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

    // Extraire un bâtiment du ResultSet
    private Batiment extraireBatiment(ResultSet rs) throws SQLException {
        Batiment b = new Batiment();
        b.setId(rs.getInt("id"));
        b.setNom(rs.getString("nom"));
        b.setCode(rs.getString("code"));
        b.setLocalisation(rs.getString("localisation"));
        b.setNombreEtages(rs.getInt("nombre_etages"));
        b.setDescription(rs.getString("description"));
        return b;
    }
}