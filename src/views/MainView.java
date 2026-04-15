package views;

import models.Utilisateur;
import javax.swing.*;
import java.awt.*;

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
        panelContenu.add(creerDashboard(), BorderLayout.CENTER);
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
        ajouterBoutonMenu(menu, "🏠 Dashboard", () -> afficherDashboard());
        ajouterBoutonMenu(menu, "🏛️ Bâtiments", () -> afficherBatiments());
        ajouterBoutonMenu(menu, "🚪 Salles", () -> afficherSalles());
        ajouterBoutonMenu(menu, "📚 Cours", () -> afficherCours());
        ajouterBoutonMenu(menu, "📅 Emplois du temps", () -> afficherEmploisDuTemps());

        // Boutons admin seulement
        if (utilisateurConnecte.getRole().equals("ADMIN")) {
            ajouterBoutonMenu(menu, "👥 Utilisateurs", () -> afficherUtilisateurs());
        }

        menu.add(Box.createVerticalGlue());

        // Bouton déconnexion
        ajouterBoutonMenu(menu, "🚪 Déconnexion", () -> seDeconnecter());

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
        afficherContenu(creerDashboard());
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

    private void afficherUtilisateurs() {
        afficherContenu(new UtilisateurView(utilisateurConnecte));
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