package views;

import dao.UtilisateurDAO;
import models.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UtilisateurView extends JPanel {

    private UtilisateurDAO utilisateurDAO;
    private JTable tableau;
    private DefaultTableModel modeleTableau;
    private Utilisateur utilisateurConnecte;

    public UtilisateurView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.utilisateurDAO = new UtilisateurDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        initialiserInterface();
    }

    private void initialiserInterface() {
        JLabel titre = new JLabel("👥 Gestion des Utilisateurs");
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titre, BorderLayout.NORTH);

        String[] colonnes = {"ID", "Nom utilisateur", "Prénom", "Nom", "Email", "Rôle", "Département"};
        modeleTableau = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableau = new JTable(modeleTableau);
        tableau.setRowHeight(30);
        tableau.setFont(new Font("Arial", Font.PLAIN, 13));
        tableau.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableau.getTableHeader().setBackground(new Color(30, 30, 50));
        tableau.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(tableau), BorderLayout.CENTER);

        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBoutons.setBackground(new Color(245, 245, 250));

        JButton boutonAjouter = new JButton("➕ Ajouter");
        JButton boutonSupprimer = new JButton("🗑️ Supprimer");
        JButton boutonActualiser = new JButton("🔄 Actualiser");

        styliserBouton(boutonAjouter, new Color(100, 200, 100));
        styliserBouton(boutonSupprimer, new Color(255, 100, 100));
        styliserBouton(boutonActualiser, new Color(200, 200, 200));

        boutonAjouter.addActionListener(e -> ajouterUtilisateur());
        boutonSupprimer.addActionListener(e -> supprimerUtilisateur());
        boutonActualiser.addActionListener(e -> chargerDonnees());

        panelBoutons.add(boutonAjouter);
        panelBoutons.add(boutonSupprimer);
        panelBoutons.add(boutonActualiser);
        add(panelBoutons, BorderLayout.SOUTH);

        chargerDonnees();
    }

    private void chargerDonnees() {
        modeleTableau.setRowCount(0);
        List<Utilisateur> utilisateurs = utilisateurDAO.getTousLesUtilisateurs();
        for (Utilisateur u : utilisateurs) {
            modeleTableau.addRow(new Object[]{
                    u.getId(), u.getNomUtilisateur(), u.getPrenom(),
                    u.getNom(), u.getEmail(), u.getRole(), u.getDepartement()
            });
        }
    }

    private void ajouterUtilisateur() {
        JTextField champNomUtil = new JTextField();
        JTextField champMotDePasse = new JTextField();
        JTextField champEmail = new JTextField();
        JTextField champPrenom = new JTextField();
        JTextField champNom = new JTextField();
        JComboBox<String> comboRole = new JComboBox<>(
                new String[]{"ADMIN", "GESTIONNAIRE", "ENSEIGNANT", "ETUDIANT"});
        JTextField champDepartement = new JTextField();

        Object[] champs = {
                "Nom utilisateur :", champNomUtil,
                "Mot de passe :", champMotDePasse,
                "Email :", champEmail,
                "Prénom :", champPrenom,
                "Nom :", champNom,
                "Rôle :", comboRole,
                "Département :", champDepartement
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Ajouter un utilisateur", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Utilisateur u = new Utilisateur();
            u.setNomUtilisateur(champNomUtil.getText());
            u.setMotDePasse(champMotDePasse.getText());
            u.setEmail(champEmail.getText());
            u.setPrenom(champPrenom.getText());
            u.setNom(champNom.getText());
            u.setRole(comboRole.getSelectedItem().toString());
            u.setDepartement(champDepartement.getText());

            if (utilisateurDAO.ajouter(u)) {
                JOptionPane.showMessageDialog(this, "Utilisateur ajouté avec succès !");
                chargerDonnees();
            }
        }
    }

    private void supprimerUtilisateur() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur à supprimer !");
            return;
        }

        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cet utilisateur ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            int id = (int) modeleTableau.getValueAt(ligne, 0);
            if (utilisateurDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Utilisateur supprimé !");
                chargerDonnees();
            }
        }
    }

    private void styliserBouton(JButton bouton, Color couleur) {
        bouton.setBackground(couleur);
        bouton.setForeground(Color.WHITE);
        bouton.setFont(new Font("Arial", Font.BOLD, 13));
        bouton.setBorderPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}