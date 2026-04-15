package views;

import dao.UtilisateurDAO;
import models.Utilisateur;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField champNomUtilisateur;
    private JPasswordField champMotDePasse;
    private JButton boutonConnexion;
    private UtilisateurDAO utilisateurDAO;

    public LoginView() {
        utilisateurDAO = new UtilisateurDAO();
        initialiserInterface();
    }

    private void initialiserInterface() {
        setTitle("UNIV-SCHEDULER - Connexion");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel titre = new JLabel("UNIV-SCHEDULER");
        titre.setFont(new Font("Arial", Font.BOLD, 22));
        titre.setForeground(new Color(100, 180, 255));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titre, gbc);

        // Sous-titre
        JLabel sousTitre = new JLabel("Gestion des Salles et Emplois du Temps");
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 11));
        sousTitre.setForeground(Color.GRAY);
        gbc.gridy = 1;
        panel.add(sousTitre, gbc);

        // Nom utilisateur
        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel labelNom = new JLabel("Utilisateur :");
        labelNom.setForeground(Color.WHITE);
        panel.add(labelNom, gbc);

        gbc.gridx = 1;
        champNomUtilisateur = new JTextField(15);
        panel.add(champNomUtilisateur, gbc);

        // Mot de passe
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel labelMdp = new JLabel("Mot de passe :");
        labelMdp.setForeground(Color.WHITE);
        panel.add(labelMdp, gbc);

        gbc.gridx = 1;
        champMotDePasse = new JPasswordField(15);
        panel.add(champMotDePasse, gbc);

        // Bouton connexion
        gbc.gridy = 4; gbc.gridx = 0;
        gbc.gridwidth = 2;
        boutonConnexion = new JButton("Se connecter");
        boutonConnexion.setBackground(new Color(100, 180, 255));
        boutonConnexion.setForeground(Color.WHITE);
        boutonConnexion.setFont(new Font("Arial", Font.BOLD, 14));
        boutonConnexion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(boutonConnexion, gbc);

        // Action bouton connexion
        boutonConnexion.addActionListener(e -> seConnecter());

        // Action touche Entrée
        champMotDePasse.addActionListener(e -> seConnecter());

        add(panel);
        setVisible(true);
    }

    private void seConnecter() {
        String nomUtilisateur = champNomUtilisateur.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword()).trim();

        if (nomUtilisateur.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs !",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Utilisateur utilisateur = utilisateurDAO.connexion(nomUtilisateur, motDePasse);

        if (utilisateur != null) {
            JOptionPane.showMessageDialog(this,
                    "Bienvenue " + utilisateur.getPrenom() + " !",
                    "Connexion réussie", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new MainView(utilisateur);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nom d'utilisateur ou mot de passe incorrect !",
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginView::new);
    }
}