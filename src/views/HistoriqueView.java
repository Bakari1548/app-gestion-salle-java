package views;

import models.Utilisateur;
import dao.EmploiDuTempsDAO;
import dao.SalleDAO;
import dao.CoursDAO;
import dao.BatimentDAO;
import models.EmploiDuTemps;
import models.Salle;
import models.Cours;
import models.Batiment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class HistoriqueView extends JPanel {
    
    private Utilisateur utilisateurConnecte;
    private EmploiDuTempsDAO emploiDuTempsDAO;
    private SalleDAO salleDAO;
    private CoursDAO coursDAO;
    private BatimentDAO batimentDAO;
    
    private JTable tableHistorique;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboFiltreBatiment;
    private JComboBox<String> comboFiltreSalle;
    private JComboBox<String> comboFiltrePeriode;
    private JTextField txtRecherche;
    private JLabel labelStats;
    
    public HistoriqueView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.emploiDuTempsDAO = new EmploiDuTempsDAO();
        this.salleDAO = new SalleDAO();
        this.coursDAO = new CoursDAO();
        this.batimentDAO = new BatimentDAO();
        
        initialiserInterface();
        chargerDonnees();
    }
    
    private void initialiserInterface() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 245, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // En-tête
        JPanel panelEnTete = creerEnTete();
        panelPrincipal.add(panelEnTete, BorderLayout.NORTH);
        
        // Panel filtres
        JPanel panelFiltres = creerPanelFiltres();
        panelPrincipal.add(panelFiltres, BorderLayout.CENTER);
        
        // Panel table et statistiques
        JPanel panelContenu = creerPanelContenu();
        panelPrincipal.add(panelContenu, BorderLayout.SOUTH);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private JPanel creerEnTete() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        
        JLabel titre = new JLabel("Historique des Réservations");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 50));
        
        JLabel sousTitre = new JLabel("Consultez l'historique complet des réservations de salles");
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(100, 100, 120));
        
        JPanel panelTitre = new JPanel();
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        panelTitre.setBackground(new Color(245, 245, 250));
        panelTitre.add(titre);
        panelTitre.add(Box.createVerticalStrut(5));
        panelTitre.add(sousTitre);
        
        // Boutons d'action
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelActions.setBackground(new Color(245, 245, 250));
        
        JButton btnExporter = new JButton("Exporter");
        btnExporter.setBackground(new Color(100, 180, 255));
        btnExporter.setForeground(Color.WHITE);
        btnExporter.setFont(new Font("Arial", Font.BOLD, 12));
        btnExporter.setBorderPainted(false);
        btnExporter.setFocusPainted(false);
        btnExporter.addActionListener(e -> exporterHistorique());
        
        JButton btnActualiser = new JButton("Actualiser");
        btnActualiser.setBackground(new Color(100, 220, 150));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFont(new Font("Arial", Font.BOLD, 12));
        btnActualiser.setBorderPainted(false);
        btnActualiser.setFocusPainted(false);
        btnActualiser.addActionListener(e -> chargerDonnees());
        
        panelActions.add(btnExporter);
        panelActions.add(btnActualiser);
        
        panel.add(panelTitre, BorderLayout.WEST);
        panel.add(panelActions, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel creerPanelFiltres() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel labelFiltres = new JLabel("Filtres de recherche");
        labelFiltres.setFont(new Font("Arial", Font.BOLD, 16));
        labelFiltres.setForeground(new Color(30, 30, 50));
        
        JPanel panelFiltresContenu = new JPanel(new GridLayout(2, 4, 15, 10));
        panelFiltresContenu.setBackground(new Color(245, 245, 250));
        
        // Filtre par bâtiment
        JPanel panelBatiment = new JPanel(new BorderLayout());
        panelBatiment.setBackground(new Color(245, 245, 250));
        panelBatiment.add(new JLabel("Bâtiment:"), BorderLayout.NORTH);
        comboFiltreBatiment = new JComboBox<>();
        comboFiltreBatiment.addItem("Tous");
        for (Batiment batiment : batimentDAO.getTousLesBatiments()) {
            comboFiltreBatiment.addItem(batiment.getNom());
        }
        comboFiltreBatiment.addActionListener(e -> appliquerFiltres());
        panelBatiment.add(comboFiltreBatiment, BorderLayout.CENTER);
        
        // Filtre par salle
        JPanel panelSalle = new JPanel(new BorderLayout());
        panelSalle.setBackground(new Color(245, 245, 250));
        panelSalle.add(new JLabel("Salle:"), BorderLayout.NORTH);
        comboFiltreSalle = new JComboBox<>();
        comboFiltreSalle.addItem("Toutes");
        comboFiltreSalle.addActionListener(e -> appliquerFiltres());
        panelSalle.add(comboFiltreSalle, BorderLayout.CENTER);
        
        // Filtre par période
        JPanel panelPeriode = new JPanel(new BorderLayout());
        panelPeriode.setBackground(new Color(245, 245, 250));
        panelPeriode.add(new JLabel("Période:"), BorderLayout.NORTH);
        comboFiltrePeriode = new JComboBox<>();
        comboFiltrePeriode.addItem("Toutes");
        comboFiltrePeriode.addItem("Aujourd'hui");
        comboFiltrePeriode.addItem("Cette semaine");
        comboFiltrePeriode.addItem("Ce mois");
        comboFiltrePeriode.addItem("Ce trimestre");
        comboFiltrePeriode.addActionListener(e -> appliquerFiltres());
        panelPeriode.add(comboFiltrePeriode, BorderLayout.CENTER);
        
        // Recherche textuelle
        JPanel panelRecherche = new JPanel(new BorderLayout());
        panelRecherche.setBackground(new Color(245, 245, 250));
        panelRecherche.add(new JLabel("Recherche:"), BorderLayout.NORTH);
        txtRecherche = new JTextField();
        txtRecherche.setToolTipText("Rechercher par cours, salle ou professeur");
        txtRecherche.addActionListener(e -> appliquerFiltres());
        panelRecherche.add(txtRecherche, BorderLayout.CENTER);
        
        panelFiltresContenu.add(panelBatiment);
        panelFiltresContenu.add(panelSalle);
        panelFiltresContenu.add(panelPeriode);
        panelFiltresContenu.add(panelRecherche);
        
        panel.add(labelFiltres, BorderLayout.NORTH);
        panel.add(panelFiltresContenu, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creerPanelContenu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        
        // Table des réservations
        JPanel panelTable = creerPanelTable();
        panel.add(panelTable, BorderLayout.CENTER);
        
        // Panel statistiques
        JPanel panelStats = creerPanelStatistiques();
        panel.add(panelStats, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel creerPanelTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titreTable = new JLabel("Liste des réservations");
        titreTable.setFont(new Font("Arial", Font.BOLD, 16));
        titreTable.setForeground(new Color(30, 30, 50));
        
        // Création du modèle de table
        String[] colonnes = {"Date", "Jour", "Heure", "Durée", "Cours", "Salle", "Bâtiment", "Classe"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableHistorique = new JTable(tableModel);
        tableHistorique.setFont(new Font("Arial", Font.PLAIN, 12));
        tableHistorique.setRowHeight(25);
        tableHistorique.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHistorique.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // Personnaliser les colonnes
        tableHistorique.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        tableHistorique.getColumnModel().getColumn(1).setPreferredWidth(80);  // Jour
        tableHistorique.getColumnModel().getColumn(2).setPreferredWidth(80);  // Heure
        tableHistorique.getColumnModel().getColumn(3).setPreferredWidth(60);  // Durée
        tableHistorique.getColumnModel().getColumn(4).setPreferredWidth(150); // Cours
        tableHistorique.getColumnModel().getColumn(5).setPreferredWidth(100); // Salle
        tableHistorique.getColumnModel().getColumn(6).setPreferredWidth(100); // Bâtiment
        tableHistorique.getColumnModel().getColumn(7).setPreferredWidth(100); // Classe
        
        JScrollPane scrollPane = new JScrollPane(tableHistorique);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        panel.add(titreTable, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creerPanelStatistiques() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel titreStats = new JLabel("Statistiques des réservations");
        titreStats.setFont(new Font("Arial", Font.BOLD, 16));
        titreStats.setForeground(new Color(30, 30, 50));
        
        labelStats = new JLabel("Chargement des statistiques...");
        labelStats.setFont(new Font("Arial", Font.PLAIN, 14));
        labelStats.setForeground(new Color(100, 100, 120));
        
        JPanel panelStatsContenu = new JPanel(new GridLayout(1, 4, 15, 0));
        panelStatsContenu.setBackground(new Color(245, 245, 250));
        
        panel.add(titreStats, BorderLayout.NORTH);
        panel.add(labelStats, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void chargerDonnees() {
        // Vider la table
        tableModel.setRowCount(0);
        
        // Récupérer toutes les réservations
        List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
        
        // Trier par date et heure
        emplois.sort((e1, e2) -> {
            int jourCompare = Integer.compare(e1.getJourSemaine(), e2.getJourSemaine());
            if (jourCompare != 0) return jourCompare;
            return e1.getHeureDebut().compareTo(e2.getHeureDebut());
        });
        
        // Remplir la table
        for (EmploiDuTemps emploi : emplois) {
            Object[] rowData = {
                formatDate(emploi),
                getNomJour(emploi.getJourSemaine()),
                emploi.getHeureDebut(),
                emploi.getDureeMinutes() + " min",
                emploi.getCours().getCode() + " - " + emploi.getCours().getIntitule(),
                emploi.getSalle().getNom(),
                emploi.getSalle().getBatiment().getNom(),
                emploi.getGroupeClasse()
            };
            tableModel.addRow(rowData);
        }
        
        // Mettre à jour les statistiques
        mettreAJourStatistiques(emplois);
        
        // Mettre à jour le filtre des salles
        mettreAJourFiltreSalles();
    }
    
    private void appliquerFiltres() {
        String filtreBatiment = (String) comboFiltreBatiment.getSelectedItem();
        String filtreSalle = (String) comboFiltreSalle.getSelectedItem();
        String filtrePeriode = (String) comboFiltrePeriode.getSelectedItem();
        String recherche = txtRecherche.getText().toLowerCase().trim();
        
        // Vérifications pour éviter les NullPointerException
        if (filtreBatiment == null) filtreBatiment = "Tous";
        if (filtreSalle == null) filtreSalle = "Toutes";
        if (filtrePeriode == null) filtrePeriode = "Toutes";
        if (recherche == null) recherche = "";
        
        // Récupérer toutes les réservations
        List<EmploiDuTemps> tousLesEmplois = emploiDuTempsDAO.getTousLesEmplois();
        List<EmploiDuTemps> emploisFiltres = new ArrayList<>();
        
        for (EmploiDuTemps emploi : tousLesEmplois) {
            boolean correspond = true;
            
            // Filtre par bâtiment
            if (!filtreBatiment.equals("Tous")) {
                if (emploi.getSalle().getBatiment() == null || 
                    !emploi.getSalle().getBatiment().getNom().equals(filtreBatiment)) {
                    correspond = false;
                }
            }
            
            // Filtre par salle
            if (!filtreSalle.equals("Toutes")) {
                if (emploi.getSalle() == null || 
                    !emploi.getSalle().getNom().equals(filtreSalle)) {
                    correspond = false;
                }
            }
            
            // Filtre par période
            if (!filtrePeriode.equals("Toutes")) {
                if (!correspondPeriode(emploi, filtrePeriode)) {
                    correspond = false;
                }
            }
            
            // Recherche textuelle
            if (!recherche.isEmpty()) {
                String texteRecherche = (emploi.getCours().getCode() + " " + 
                        emploi.getCours().getIntitule() + " " + 
                        emploi.getSalle().getNom() + " " + 
                        emploi.getGroupeClasse()).toLowerCase();
                if (!texteRecherche.contains(recherche)) {
                    correspond = false;
                }
            }
            
            if (correspond) {
                emploisFiltres.add(emploi);
            }
        }
        
        // Mettre à jour la table
        tableModel.setRowCount(0);
        for (EmploiDuTemps emploi : emploisFiltres) {
            Object[] rowData = {
                formatDate(emploi),
                getNomJour(emploi.getJourSemaine()),
                emploi.getHeureDebut(),
                emploi.getDureeMinutes() + " min",
                emploi.getCours().getCode() + " - " + emploi.getCours().getIntitule(),
                emploi.getSalle().getNom(),
                emploi.getSalle().getBatiment().getNom(),
                emploi.getGroupeClasse()
            };
            tableModel.addRow(rowData);
        }
        
        // Mettre à jour les statistiques
        mettreAJourStatistiques(emploisFiltres);
    }
    
    private boolean correspondPeriode(EmploiDuTemps emploi, String periode) {
        Calendar calendrier = Calendar.getInstance();
        int jourActuel = calendrier.get(Calendar.DAY_OF_WEEK);
        int jourEmploi = emploi.getJourSemaine();
        
        switch (periode) {
            case "Aujourd'hui":
                return jourActuel == jourEmploi;
            case "Cette semaine":
                return true; // Tous les jours de la semaine
            case "Ce mois":
                return true; // Simplifié - tous les jours
            case "Ce trimestre":
                return true; // Simplifié - tous les jours
            default:
                return true;
        }
    }
    
    private void mettreAJourFiltreSalles() {
        String batimentSelectionne = (String) comboFiltreBatiment.getSelectedItem();
        
        // Vérification pour éviter NullPointerException
        if (batimentSelectionne == null) batimentSelectionne = "Tous";
        
        comboFiltreSalle.removeAllItems();
        comboFiltreSalle.addItem("Toutes");
        
        if (batimentSelectionne.equals("Tous")) {
            for (Salle salle : salleDAO.getToutesLesSalles()) {
                comboFiltreSalle.addItem(salle.getNom());
            }
        } else {
            for (Salle salle : salleDAO.getToutesLesSalles()) {
                if (salle.getBatiment() != null && 
                    salle.getBatiment().getNom().equals(batimentSelectionne)) {
                    comboFiltreSalle.addItem(salle.getNom());
                }
            }
        }
    }
    
    private void mettreAJourStatistiques(List<EmploiDuTemps> emplois) {
        int totalReservations = emplois.size();
        int totalHeures = emplois.stream().mapToInt(e -> e.getDureeMinutes()).sum() / 60;
        int sallesUtilisees = (int) emplois.stream().map(e -> e.getSalleId()).distinct().count();
        int coursDifferents = (int) emplois.stream().map(e -> e.getCoursId()).distinct().count();
        
        String stats = String.format(
            "<html><body style='font-family: Arial; font-size: 14px;'>" +
            "<strong>Total réservations:</strong> %d | " +
            "<strong>Total heures:</strong> %d | " +
            "<strong>Salles utilisées:</strong> %d | " +
            "<strong>Cours différents:</strong> %d" +
            "</body></html>",
            totalReservations, totalHeures, sallesUtilisees, coursDifferents
        );
        
        labelStats.setText(stats);
    }
    
    private String formatDate(EmploiDuTemps emploi) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendrier = Calendar.getInstance();
        calendrier.set(Calendar.DAY_OF_WEEK, emploi.getJourSemaine());
        return sdf.format(calendrier.getTime());
    }
    
    private String getNomJour(int jourSemaine) {
        String[] jours = {"", "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
        return jourSemaine >= 1 && jourSemaine <= 7 ? jours[jourSemaine] : "Inconnu";
    }
    
    private void exporterHistorique() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter l'historique");
        fileChooser.setSelectedFile(new java.io.File("historique_reservations.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try {
                exporterVersCSV(fileToSave);
                JOptionPane.showMessageDialog(this, 
                    "Historique exporté avec succès vers " + fileToSave.getName(),
                    "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'export: " + ex.getMessage(),
                    "Erreur d'export", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exporterVersCSV(java.io.File fichier) throws java.io.IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(fichier))) {
            // En-tête CSV
            writer.println("Date,Jour,Heure,Durée,Cours,Salle,Bâtiment,Classe");
            
            // Données
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder ligne = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    if (j > 0) ligne.append(",");
                    String valeur = tableModel.getValueAt(i, j).toString();
                    // Échapper les guillemets et ajouter des guillemets si nécessaire
                    if (valeur.contains(",")) {
                        valeur = "\"" + valeur.replace("\"", "\"\"") + "\"";
                    }
                    ligne.append(valeur);
                }
                writer.println(ligne.toString());
            }
        }
    }
}
