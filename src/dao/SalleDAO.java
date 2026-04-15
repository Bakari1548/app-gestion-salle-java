package dao;

import database.DatabaseConnection;
import models.Salle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    // Récupérer toutes les salles
    public List<Salle> getToutesLesSalles() {
        List<Salle> liste = new ArrayList<>();
        String sql = "SELECT * FROM salles WHERE est_active = 1";
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                liste.add(extraireSalle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return liste;
    }

    // Rechercher salles disponibles
    public List<Salle> getSallesDisponibles(int jourSemaine, String heureDebut, int dureeMinutes) {
        List<Salle> liste = new ArrayList<>();
        String sql = "SELECT * FROM salles WHERE est_active = 1 AND id NOT IN (" +
                "SELECT salle_id FROM emplois_du_temps " +
                "WHERE jour_semaine = ? AND heure_debut = ?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, jourSemaine);
            ps.setString(2, heureDebut);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(extraireSalle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }
        return liste;
    }

    // Rechercher par type et capacité
    public List<Salle> rechercherParCriteres(String typeSalle, int capaciteMin) {
        List<Salle> liste = new ArrayList<>();
        String sql = "SELECT * FROM salles WHERE est_active = 1 AND type_salle = ? AND capacite >= ?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setString(1, typeSalle);
            ps.setInt(2, capaciteMin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(extraireSalle(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }
        return liste;
    }

    // Ajouter une salle
    public boolean ajouter(Salle s) {
        String sql = "INSERT INTO salles (batiment_id, numero_salle, nom, etage, capacite, type_salle) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, s.getBatimentId());
            ps.setString(2, s.getNumeroSalle());
            ps.setString(3, s.getNom());
            ps.setInt(4, s.getEtage());
            ps.setInt(5, s.getCapacite());
            ps.setString(6, s.getTypeSalle());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
            return false;
        }
    }

    // Supprimer une salle
    public boolean supprimer(int id) {
        String sql = "UPDATE salles SET est_active = 0 WHERE id = ?";
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

    // Extraire une salle du ResultSet
    private Salle extraireSalle(ResultSet rs) throws SQLException {
        Salle s = new Salle();
        s.setId(rs.getInt("id"));
        s.setBatimentId(rs.getInt("batiment_id"));
        s.setNumeroSalle(rs.getString("numero_salle"));
        s.setNom(rs.getString("nom"));
        s.setEtage(rs.getInt("etage"));
        s.setCapacite(rs.getInt("capacite"));
        s.setTypeSalle(rs.getString("type_salle"));
        s.setEstActive(rs.getInt("est_active") == 1);
        return s;
    }
}