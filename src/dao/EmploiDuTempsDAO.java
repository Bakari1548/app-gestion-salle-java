package dao;

import database.DatabaseConnection;
import models.EmploiDuTemps;
import models.Cours;
import models.Salle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmploiDuTempsDAO {

    // Récupérer tous les emplois du temps
    public List<EmploiDuTemps> getTousLesEmplois() {
        List<EmploiDuTemps> liste = new ArrayList<>();
        String sql = "SELECT e.*, c.code, c.intitule, s.nom as nom_salle " +
                "FROM emplois_du_temps e " +
                "LEFT JOIN cours c ON e.cours_id = c.id " +
                "LEFT JOIN salles s ON e.salle_id = s.id";
        try {
            Statement stmt = DatabaseConnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                liste.add(extraireEmploi(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return liste;
    }

    // Vérifier les conflits
    public boolean verifierConflit(int salleId, int jourSemaine, String heureDebut) {
        String sql = "SELECT COUNT(*) FROM emplois_du_temps " +
                "WHERE salle_id = ? AND jour_semaine = ? AND heure_debut = ?";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, salleId);
            ps.setInt(2, jourSemaine);
            ps.setString(3, heureDebut);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur vérification conflit : " + e.getMessage());
        }
        return false;
    }

    // Ajouter un emploi du temps
    public boolean ajouter(EmploiDuTemps e) {
        String sql = "INSERT INTO emplois_du_temps (cours_id, salle_id, jour_semaine, " +
                "heure_debut, duree_minutes, groupe_classe, annee_academique, semestre) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, e.getCoursId());
            ps.setInt(2, e.getSalleId());
            ps.setInt(3, e.getJourSemaine());
            ps.setString(4, e.getHeureDebut());
            ps.setInt(5, e.getDureeMinutes());
            ps.setString(6, e.getGroupeClasse());
            ps.setString(7, e.getAnneeAcademique());
            ps.setInt(8, e.getSemestre());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("Erreur ajout : " + ex.getMessage());
            return false;
        }
    }

    // Supprimer un emploi du temps
    public boolean supprimer(int id) {
        String sql = "DELETE FROM emplois_du_temps WHERE id = ?";
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

    // Extraire un emploi du ResultSet
    private EmploiDuTemps extraireEmploi(ResultSet rs) throws SQLException {
        EmploiDuTemps e = new EmploiDuTemps();
        e.setId(rs.getInt("id"));
        e.setCoursId(rs.getInt("cours_id"));
        e.setSalleId(rs.getInt("salle_id"));
        e.setJourSemaine(rs.getInt("jour_semaine"));
        e.setHeureDebut(rs.getString("heure_debut"));
        e.setDureeMinutes(rs.getInt("duree_minutes"));
        e.setGroupeClasse(rs.getString("groupe_classe"));
        e.setAnneeAcademique(rs.getString("annee_academique"));
        e.setSemestre(rs.getInt("semestre"));

        Cours c = new Cours();
        c.setCode(rs.getString("code"));
        c.setIntitule(rs.getString("intitule"));
        e.setCours(c);

        // Charger la salle complète avec son bâtiment
        SalleDAO salleDAO = new SalleDAO();
        Salle s = salleDAO.getSalleParId(rs.getInt("salle_id"));
        if (s == null) {
            // Fallback si la salle n'est pas trouvée
            s = new Salle();
            s.setNom(rs.getString("nom_salle"));
        }
        e.setSalle(s);

        return e;
    }
}