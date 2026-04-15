package views;

import models.Utilisateur;
import services.ExportService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MainView extends JFrame {

    private Utilisateur utilisateurConnecte;
    private JPanel panelContenu;

    public MainView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        initialiserInterface();
    }

    private void initialiserInterface() {
        setTitle("UNIV-SCHEDULER - " + utilisateurConnecte.getPrenom() + " (" + utilisateurConnecte.getRole() + ")");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Menu gauche
        JPanel menuGauche = creerMenuGauche();
        panelPrincipal.add(menuGauche, BorderLayout.WEST);

        // Panel contenu (centre)
        panelContenu = new JPanel(new BorderLayout());
        panelContenu.setBackground(new Color(245, 245, 250));
        panelContenu.add(new DashboardView(utilisateurConnecte), BorderLayout.CENTER);
        panelPrincipal.add(panelContenu, BorderLayout.CENTER);

        add(panelPrincipal);
        setVisible(true);
    }

    private JPanel creerMenuGauche() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(new Color(30, 30, 50));
        menu.setPreferredSize(new Dimension(200, getHeight()));
        menu.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Logo
        JLabel logo = new JLabel("UNIV-SCHEDULER");
        logo.setFont(new Font("Arial", Font.BOLD, 14));
        logo.setForeground(new Color(100, 180, 255));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(logo);
        menu.add(Box.createVerticalStrut(30));

        // Boutons du menu selon le rôle
        ajouterBoutonMenu(menu, "Dashboard", () -> afficherDashboard());
        
        // Historique et Rapports seulement pour les rôles non-étudiants
        if (!utilisateurConnecte.getRole().equals("ETUDIANT")) {
            ajouterBoutonMenu(menu, "Historique", () -> afficherHistorique());
            ajouterBoutonMenu(menu, "Bâtiments", () -> afficherBatiments());
            ajouterBoutonMenu(menu, "Salles", () -> afficherSalles());
            ajouterBoutonMenu(menu, "Cours", () -> afficherCours());
            ajouterBoutonMenu(menu, "Emplois du temps", () -> afficherEmploisDuTemps());
            ajouterBoutonMenu(menu, "Rapports", () -> afficherRapports());
        } else {
            // Pour les étudiants, seulement les consultations de base
            ajouterBoutonMenu(menu, "Emplois du temps", () -> afficherEmploisDuTemps());
        }

        // Boutons admin seulement
        if (utilisateurConnecte.getRole().equals("ADMIN")) {
            ajouterBoutonMenu(menu, "Utilisateurs", () -> afficherUtilisateurs());
        }

        menu.add(Box.createVerticalGlue());

        // Bouton déconnexion
        ajouterBoutonMenu(menu, "Déconnexion", () -> seDeconnecter());

        return menu;
    }

    private void ajouterBoutonMenu(JPanel menu, String texte, Runnable action) {
        JButton bouton = new JButton(texte);
        bouton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bouton.setMaximumSize(new Dimension(180, 40));
        bouton.setBackground(new Color(50, 50, 80));
        bouton.setForeground(Color.WHITE);
        bouton.setFont(new Font("Arial", Font.PLAIN, 13));
        bouton.setBorderPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.addActionListener(e -> action.run());
        menu.add(bouton);
        menu.add(Box.createVerticalStrut(5));
    }

    private JPanel creerDashboard() {
        JPanel dashboard = new JPanel(new GridLayout(2, 3, 15, 15));
        dashboard.setBackground(new Color(245, 245, 250));
        dashboard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dashboard.add(creerCarte("🏛️ Bâtiments", "3", new Color(100, 180, 255)));
        dashboard.add(creerCarte("🚪 Salles", "9", new Color(100, 220, 150)));
        dashboard.add(creerCarte("📚 Cours", "4", new Color(255, 180, 100)));
        dashboard.add(creerCarte("👥 Utilisateurs", "5", new Color(220, 100, 255)));
        dashboard.add(creerCarte("📅 Séances", "5", new Color(255, 100, 100)));
        dashboard.add(creerCarte("📋 Réservations", "0", new Color(100, 255, 220)));

        return dashboard;
    }

    private JPanel creerCarte(String titre, String valeur, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createLineBorder(couleur, 2));

        JLabel labelTitre = new JLabel(titre, SwingConstants.CENTER);
        labelTitre.setFont(new Font("Arial", Font.BOLD, 14));
        labelTitre.setForeground(couleur);
        labelTitre.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel labelValeur = new JLabel(valeur, SwingConstants.CENTER);
        labelValeur.setFont(new Font("Arial", Font.BOLD, 40));
        labelValeur.setForeground(couleur);

        carte.add(labelTitre, BorderLayout.NORTH);
        carte.add(labelValeur, BorderLayout.CENTER);

        return carte;
    }

    private void afficherContenu(JPanel panel) {
        panelContenu.removeAll();
        panelContenu.add(panel, BorderLayout.CENTER);
        panelContenu.revalidate();
        panelContenu.repaint();
    }

    private void afficherDashboard() {
        afficherContenu(new DashboardView(utilisateurConnecte));
    }

    private void afficherHistorique() {
        afficherContenu(new HistoriqueView(utilisateurConnecte));
    }

    private void afficherBatiments() {
        afficherContenu(new BatimentView(utilisateurConnecte));
    }

    private void afficherSalles() {
        afficherContenu(new SalleView(utilisateurConnecte));
    }

    private void afficherCours() {
        afficherContenu(new CoursView(utilisateurConnecte));
    }

    private void afficherEmploisDuTemps() {
        afficherContenu(new EmploiDuTempsView(utilisateurConnecte));
    }

    private void afficherRapports() {
        afficherContenu(creerPanelRapports());
    }

    private void afficherUtilisateurs() {
        afficherContenu(new UtilisateurView(utilisateurConnecte));
    }

    private JPanel creerPanelRapports() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // En-tête
        JLabel titre = new JLabel("Rapports et Exports");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(30, 30, 50));
        
        JLabel sousTitre = new JLabel("Générez et exportez des rapports d'utilisation");
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(100, 100, 120));
        
        JPanel panelTitre = new JPanel();
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        panelTitre.setBackground(new Color(245, 245, 250));
        panelTitre.add(titre);
        panelTitre.add(Box.createVerticalStrut(5));
        panelTitre.add(sousTitre);
        
        // Panel des boutons d'export
        JPanel panelExports = new JPanel(new GridLayout(2, 3, 15, 15));
        panelExports.setBackground(new Color(245, 245, 250));
        panelExports.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        // Export CSV
        JButton btnExportCSV = creerBoutonExport("Export CSV", new Color(100, 180, 255));
        btnExportCSV.addActionListener(e -> exporterEmploisDuTemps("CSV"));
        panelExports.add(btnExportCSV);
        
        // Export Excel
        JButton btnExportExcel = creerBoutonExport("Export Excel", new Color(100, 220, 150));
        btnExportExcel.addActionListener(e -> exporterEmploisDuTemps("Excel"));
        panelExports.add(btnExportExcel);
        
        // Export PDF
        JButton btnExportPDF = creerBoutonExport("Export PDF", new Color(255, 100, 100));
        btnExportPDF.addActionListener(e -> exporterEmploisDuTemps("PDF"));
        panelExports.add(btnExportPDF);
        
        // Rapport hebdomadaire
        JButton btnRapportHebdo = creerBoutonExport("Rapport Hebdomadaire", new Color(255, 180, 100));
        btnRapportHebdo.addActionListener(e -> exporterRapport("hebdomadaire"));
        panelExports.add(btnRapportHebdo);
        
        // Rapport mensuel
        JButton btnRapportMensuel = creerBoutonExport("Rapport Mensuel", new Color(220, 100, 255));
        btnRapportMensuel.addActionListener(e -> exporterRapport("mensuel"));
        panelExports.add(btnRapportMensuel);
        
        // Rapport complet
        JButton btnRapportComplet = creerBoutonExport("Rapport Complet", new Color(100, 255, 220));
        btnRapportComplet.addActionListener(e -> exporterRapport("complet"));
        panelExports.add(btnRapportComplet);
        
        panel.add(panelTitre, BorderLayout.NORTH);
        panel.add(panelExports, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton creerBoutonExport(String texte, Color couleur) {
        JButton bouton = new JButton(texte);
        bouton.setBackground(couleur);
        bouton.setForeground(Color.WHITE);
        bouton.setFont(new Font("Arial", Font.BOLD, 14));
        bouton.setBorderPainted(false);
        bouton.setFocusPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.setPreferredSize(new Dimension(200, 80));
        
        return bouton;
    }
    
    private void exporterEmploisDuTemps(String format) {
        ExportService exportService = new ExportService();
        JFileChooser fileChooser = new JFileChooser();
        
        String extension = "";
        String description = "";
        
        switch (format) {
            case "CSV":
                extension = "csv";
                description = "Fichiers CSV (*.csv)";
                break;
            case "Excel":
                extension = "html";
                description = "Fichiers Excel (*.html)";
                break;
            case "PDF":
                extension = "pdf";
                description = "Fichiers PDF (*.pdf)";
                break;
        }
        
        fileChooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        fileChooser.setSelectedFile(new File("emplois_du_temps." + extension));
        
        int resultat = fileChooser.showSaveDialog(this);
        if (resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();
            boolean succes = false;
            
            switch (format) {
                case "CSV":
                    succes = exportService.exporterEmploisDuTempsCSV(fichier.getAbsolutePath(), 0);
                    break;
                case "Excel":
                    succes = exportService.exporterEmploisDuTempsExcel(fichier.getAbsolutePath(), 0);
                    break;
                case "PDF":
                    succes = exportService.exporterEmploisDuTempsPDF(fichier.getAbsolutePath(), 0);
                    break;
            }
            
            if (succes) {
                JOptionPane.showMessageDialog(this, 
                    "Export " + format + " réussi !\nFichier sauvegardé dans: " + fichier.getName(),
                    "Export réussi", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'export " + format,
                    "Erreur d'export", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exporterRapport(String type) {
        ExportService exportService = new ExportService();
        JFileChooser fileChooser = new JFileChooser();
        
        String nomFichier = "";
        String description = "";
        
        switch (type) {
            case "hebdomadaire":
                nomFichier = "rapport_hebdomadaire.txt";
                description = "Fichiers texte (*.txt)";
                break;
            case "mensuel":
                nomFichier = "rapport_mensuel.txt";
                description = "Fichiers texte (*.txt)";
                break;
            case "complet":
                nomFichier = "rapport_complet.txt";
                description = "Fichiers texte (*.txt)";
                break;
        }
        
        fileChooser.setFileFilter(new FileNameExtensionFilter(description, "txt"));
        fileChooser.setSelectedFile(new File(nomFichier));
        
        int resultat = fileChooser.showSaveDialog(this);
        if (resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();
            boolean succes = false;
            
            switch (type) {
                case "hebdomadaire":
                    succes = exportService.exporterRapportHebdomadaire(fichier.getAbsolutePath());
                    break;
                case "mensuel":
                    succes = exportService.exporterRapportMensuel(fichier.getAbsolutePath());
                    break;
                case "complet":
                    // Générer un rapport complet combinant hebdomadaire et mensuel
                    succes = exportService.exporterRapportHebdomadaire(fichier.getAbsolutePath()) &&
                             exportService.exporterRapportMensuel(fichier.getAbsolutePath().replace(".txt", "_mensuel.txt"));
                    break;
            }
            
            if (succes) {
                JOptionPane.showMessageDialog(this, 
                    "Rapport " + type + " généré avec succès !\nFichier sauvegardé dans: " + fichier.getName(),
                    "Rapport généré", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la génération du rapport " + type,
                    "Erreur de génération", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void seDeconnecter() {
        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Déconnexion", JOptionPane.YES_NO_OPTION);
        if (reponse == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView();
        }
    }
}