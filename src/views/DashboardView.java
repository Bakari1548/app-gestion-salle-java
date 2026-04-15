package views;

import models.Utilisateur;
import dao.BatimentDAO;
import dao.SalleDAO;
import dao.CoursDAO;
import dao.EmploiDuTempsDAO;
import models.Batiment;
import models.Salle;
import models.EmploiDuTemps;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class DashboardView extends JPanel {
    
    private Utilisateur utilisateurConnecte;
    private EmploiDuTempsDAO emploiDuTempsDAO;
    private SalleDAO salleDAO;
    private BatimentDAO batimentDAO;
    private CoursDAO coursDAO;
    
    public DashboardView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.emploiDuTempsDAO = new EmploiDuTempsDAO();
        this.salleDAO = new SalleDAO();
        this.batimentDAO = new BatimentDAO();
        this.coursDAO = new CoursDAO();
        
        initialiserInterface();
    }
    
    private void initialiserInterface() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        
        // Panel principal avec scroll
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(245, 245, 250));
        
        // En-tête
        JPanel panelEnTete = creerEnTete();
        panelPrincipal.add(panelEnTete);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        // Statistiques principales
        JPanel panelStats = creerStatistiquesPrincipales();
        panelPrincipal.add(panelStats);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        // Graphiques et analyses
        JPanel panelAnalyses = creerPanelAnalyses();
        panelPrincipal.add(panelAnalyses);
        panelPrincipal.add(Box.createVerticalStrut(20));
        
        // Salles critiques
        JPanel panelSallesCritiques = creerPanelSallesCritiques();
        panelPrincipal.add(panelSallesCritiques);
        
        scrollPane.setViewportView(panelPrincipal);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel creerEnTete() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(new EmptyBorder(20, 20, 0, 20));
        
        JLabel titre = new JLabel("Tableau de Bord - Statistiques et Analyses");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 50));
        
        JLabel sousTitre = new JLabel("Vue d'ensemble de l'utilisation des salles et des ressources");
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(100, 100, 120));
        
        JPanel panelTitre = new JPanel();
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        panelTitre.setBackground(new Color(245, 245, 250));
        panelTitre.add(titre);
        panelTitre.add(Box.createVerticalStrut(5));
        panelTitre.add(sousTitre);
        
        // Bouton d'actualisation
        JButton btnActualiser = new JButton("Actualiser");
        btnActualiser.setBackground(new Color(100, 180, 255));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFont(new Font("Arial", Font.BOLD, 12));
        btnActualiser.setBorderPainted(false);
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> actualiserDonnees());
        
        panel.add(panelTitre, BorderLayout.WEST);
        panel.add(btnActualiser, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel creerStatistiquesPrincipales() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));
        panel.setBackground(new Color(245, 245, 250));
        
        // Récupérer les données
        List<Batiment> batiments = batimentDAO.getTousLesBatiments();
        List<Salle> salles = salleDAO.getToutesLesSalles();
        List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
        
        int totalBatiments = batiments.size();
        int totalSalles = salles.size();
        int totalEmplois = emplois.size();
        double tauxOccupation = calculerTauxOccupationGlobal(emplois, salles);
        int sallesSaturees = compterSallesSaturees(salles, emplois);
        
        panel.add(creerCarteStatistique("Bâtiments", String.valueOf(totalBatiments), 
                new Color(100, 180, 255), "building.png"));
        panel.add(creerCarteStatistique("Salles", String.valueOf(totalSalles), 
                new Color(100, 220, 150), "room.png"));
        panel.add(creerCarteStatistique("Séances", String.valueOf(totalEmplois), 
                new Color(255, 180, 100), "schedule.png"));
        panel.add(creerCarteStatistique("Taux Occupation", String.format("%.1f%%", tauxOccupation), 
                new Color(220, 100, 255), "occupancy.png"));
        panel.add(creerCarteStatistique("Salles Saturées", String.valueOf(sallesSaturees), 
                new Color(255, 100, 100), "warning.png"));
        panel.add(creerCarteStatistique("Disponibilité", String.format("%.1f%%", 100 - tauxOccupation), 
                new Color(100, 255, 220), "available.png"));
        panel.add(creerCarteStatistique("Cours Actifs", String.valueOf(coursDAO.getTousLesCours().size()), 
                new Color(255, 150, 50), "course.png"));
        panel.add(creerCarteStatistique("Utilisateurs", String.valueOf(5), 
                new Color(150, 100, 255), "users.png"));
        
        return panel;
    }
    
    private JPanel creerCarteStatistique(String titre, String valeur, Color couleur, String icone) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(couleur, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel pour l'icône et le titre
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.setBackground(Color.WHITE);
        
        JLabel labelIcone = new JLabel(getIconePourStatistique(icone));
        labelIcone.setFont(new Font("Arial", Font.PLAIN, 24));
        labelIcone.setForeground(couleur);
        
        JLabel labelTitre = new JLabel(titre);
        labelTitre.setFont(new Font("Arial", Font.BOLD, 12));
        labelTitre.setForeground(couleur);
        labelTitre.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panelHaut.add(labelIcone, BorderLayout.WEST);
        panelHaut.add(labelTitre, BorderLayout.EAST);
        
        JLabel labelValeur = new JLabel(valeur);
        labelValeur.setFont(new Font("Arial", Font.BOLD, 28));
        labelValeur.setForeground(couleur);
        labelValeur.setHorizontalAlignment(SwingConstants.CENTER);
        
        carte.add(panelHaut, BorderLayout.NORTH);
        carte.add(labelValeur, BorderLayout.CENTER);
        
        return carte;
    }
    
    private String getIconePourStatistique(String nomIcone) {
        switch (nomIcone) {
            case "building.png": return "Building";
            case "room.png": return "Room";
            case "schedule.png": return "Schedule";
            case "occupancy.png": return "Occupancy";
            case "warning.png": return "Warning";
            case "available.png": return "Available";
            case "course.png": return "Course";
            case "users.png": return "Users";
            default: return "Info";
        }
    }
    
    private JPanel creerPanelAnalyses() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));
        panel.setBackground(new Color(245, 245, 250));
        
        // Graphique d'occupation par bâtiment
        JPanel panelOccupationBatiments = creerGraphiqueOccupationBatiments();
        panel.add(panelOccupationBatiments);
        
        // Graphique d'utilisation par jour
        JPanel panelUtilisationJour = creerGraphiqueUtilisationJour();
        panel.add(panelUtilisationJour);
        
        return panel;
    }
    
    private JPanel creerGraphiqueOccupationBatiments() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 180, 255), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titre = new JLabel("Taux d'occupation par bâtiment");
        titre.setFont(new Font("Arial", Font.BOLD, 16));
        titre.setForeground(new Color(30, 30, 50));
        
        JPanel contenu = creerBarresOccupationBatiments();
        
        panel.add(titre, BorderLayout.NORTH);
        panel.add(contenu, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creerBarresOccupationBatiments() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        List<Batiment> batiments = batimentDAO.getTousLesBatiments();
        List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
        List<Salle> salles = salleDAO.getToutesLesSalles();
        
        for (Batiment batiment : batiments) {
            double taux = calculerTauxOccupationBatiment(batiment, salles, emplois);
            JPanel barre = creerBarreProgression(batiment.getNom(), taux, 
                    taux > 80 ? new Color(255, 100, 100) : new Color(100, 180, 255));
            panel.add(barre);
            panel.add(Box.createVerticalStrut(10));
        }
        
        return panel;
    }
    
    private JPanel creerBarreProgression(String nom, double pourcentage, Color couleur) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel labelNom = new JLabel(nom);
        labelNom.setFont(new Font("Arial", Font.PLAIN, 12));
        labelNom.setPreferredSize(new Dimension(100, 20));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) pourcentage);
        progressBar.setString(String.format("%.1f%%", pourcentage));
        progressBar.setStringPainted(true);
        progressBar.setForeground(couleur);
        progressBar.setBackground(new Color(240, 240, 240));
        
        panel.add(labelNom, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creerGraphiqueUtilisationJour() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 220, 150), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titre = new JLabel("Répartition des séances par jour");
        titre.setFont(new Font("Arial", Font.BOLD, 16));
        titre.setForeground(new Color(30, 30, 50));
        
        JPanel contenu = creerBarresUtilisationJour();
        
        panel.add(titre, BorderLayout.NORTH);
        panel.add(contenu, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creerBarresUtilisationJour() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
        int[] comptes = compterEmploisParJour();
        
        for (int i = 0; i < jours.length; i++) {
            JPanel barre = creerBarreProgression(jours[i], comptes[i] * 10.0, 
                    new Color(100, 220, 150));
            panel.add(barre);
            panel.add(Box.createVerticalStrut(10));
        }
        
        return panel;
    }
    
    private JPanel creerPanelSallesCritiques() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 250));
        
        JLabel titre = new JLabel("Salles critiques (taux d'occupation > 80%)");
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        titre.setForeground(new Color(30, 30, 50));
        
        JPanel contenu = creerListeSallesCritiques();
        
        panel.add(titre, BorderLayout.NORTH);
        panel.add(contenu, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creerListeSallesCritiques() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 100, 100), 2));
        
        List<Salle> salles = salleDAO.getToutesLesSalles();
        List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
        
        boolean aDesSallesCritiques = false;
        
        for (Salle salle : salles) {
            double taux = calculerTauxOccupationSalle(salle, emplois);
            if (taux > 80) {
                aDesSallesCritiques = true;
                JPanel item = creerItemSalleCritique(salle, taux);
                panel.add(item);
                panel.add(Box.createVerticalStrut(5));
            }
        }
        
        if (!aDesSallesCritiques) {
            JLabel message = new JLabel("Aucune salle critique détectée");
            message.setFont(new Font("Arial", Font.ITALIC, 14));
            message.setForeground(new Color(100, 100, 120));
            message.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(message);
        }
        
        return panel;
    }
    
    private JPanel creerItemSalleCritique(Salle salle, double taux) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 240, 240));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel labelSalle = new JLabel(salle.getNom() + " - " + salle.getBatiment().getNom());
        labelSalle.setFont(new Font("Arial", Font.BOLD, 14));
        labelSalle.setForeground(new Color(200, 50, 50));
        
        JLabel labelTaux = new JLabel(String.format("Taux: %.1f%%", taux));
        labelTaux.setFont(new Font("Arial", Font.PLAIN, 12));
        labelTaux.setForeground(new Color(150, 50, 50));
        
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(new Color(255, 240, 240));
        panelInfo.add(labelSalle);
        panelInfo.add(labelTaux);
        
        JLabel alerte = new JLabel("!");
        alerte.setFont(new Font("Arial", Font.BOLD, 16));
        alerte.setForeground(Color.WHITE);
        alerte.setBackground(new Color(255, 100, 100));
        alerte.setOpaque(true);
        alerte.setPreferredSize(new Dimension(30, 30));
        alerte.setHorizontalAlignment(SwingConstants.CENTER);
        alerte.setVerticalAlignment(SwingConstants.CENTER);
        
        panel.add(panelInfo, BorderLayout.CENTER);
        panel.add(alerte, BorderLayout.EAST);
        
        return panel;
    }
    
    // Méthodes de calcul
    private double calculerTauxOccupationGlobal(List<EmploiDuTemps> emplois, List<Salle> salles) {
        if (salles.isEmpty()) return 0;
        
        int totalHeuresDisponibles = salles.size() * 5 * 8; // 5 jours, 8 heures par jour
        int totalHeuresUtilisees = emplois.size() * 2; // Approximation 2 heures par séance
        
        return Math.min(100, (totalHeuresUtilisees * 100.0) / totalHeuresDisponibles);
    }
    
    private double calculerTauxOccupationBatiment(Batiment batiment, List<Salle> salles, List<EmploiDuTemps> emplois) {
        List<Salle> sallesBatiment = new ArrayList<>();
        for (Salle salle : salles) {
            if (salle.getBatiment().getId() == batiment.getId()) {
                sallesBatiment.add(salle);
            }
        }
        
        if (sallesBatiment.isEmpty()) return 0;
        
        int totalHeuresDisponibles = sallesBatiment.size() * 5 * 8;
        int totalHeuresUtilisees = 0;
        
        for (EmploiDuTemps emploi : emplois) {
            for (Salle salle : sallesBatiment) {
                if (emploi.getSalleId() == salle.getId()) {
                    totalHeuresUtilisees += 2;
                    break;
                }
            }
        }
        
        return Math.min(100, (totalHeuresUtilisees * 100.0) / totalHeuresDisponibles);
    }
    
    private double calculerTauxOccupationSalle(Salle salle, List<EmploiDuTemps> emplois) {
        int totalHeuresDisponibles = 5 * 8; // 5 jours, 8 heures par jour
        int totalHeuresUtilisees = 0;
        
        for (EmploiDuTemps emploi : emplois) {
            if (emploi.getSalleId() == salle.getId()) {
                totalHeuresUtilisees += 2;
            }
        }
        
        return Math.min(100, (totalHeuresUtilisees * 100.0) / totalHeuresDisponibles);
    }
    
    private int compterSallesSaturees(List<Salle> salles, List<EmploiDuTemps> emplois) {
        int count = 0;
        for (Salle salle : salles) {
            if (calculerTauxOccupationSalle(salle, emplois) > 80) {
                count++;
            }
        }
        return count;
    }
    
    private int[] compterEmploisParJour() {
        int[] comptes = new int[6]; // Lundi à Samedi
        List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
        
        for (EmploiDuTemps emploi : emplois) {
            int jour = emploi.getJourSemaine() - 1; // Ajuster pour 0-5
            if (jour >= 0 && jour < 6) {
                comptes[jour]++;
            }
        }
        
        return comptes;
    }
    
    private void actualiserDonnees() {
        removeAll();
        initialiserInterface();
        revalidate();
        repaint();
    }
}
