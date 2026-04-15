package views;

import dao.CoursDAO;
import dao.UtilisateurDAO;
import models.Cours;
import models.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoursView extends JPanel {

    private CoursDAO coursDAO;
    private UtilisateurDAO utilisateurDAO;
    private JTable tableau;
    private DefaultTableModel modeleTableau;
    private Utilisateur utilisateurConnecte;

    public CoursView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.coursDAO = new CoursDAO();
        this.utilisateurDAO = new UtilisateurDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        initialiserInterface();
    }

    private void initialiserInterface() {
        // Titre
        JLabel titre = new JLabel("📚 Gestion des Cours");
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titre, BorderLayout.NORTH);

        // Tableau
        String[] colonnes = {"ID", "Code", "Intitulé", "Enseignant", "Département", "Crédits", "H/Semaine"};
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

        // Boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBoutons.setBackground(new Color(245, 245, 250));

        JButton boutonAjouter = new JButton("➕ Ajouter");
        JButton boutonModifier = new JButton("✏️ Modifier");
        JButton boutonSupprimer = new JButton("🗑️ Supprimer");
        JButton boutonActualiser = new JButton("🔄 Actualiser");

        styliserBouton(boutonAjouter, new Color(100, 200, 100));
        styliserBouton(boutonModifier, new Color(100, 180, 255));
        styliserBouton(boutonSupprimer, new Color(255, 100, 100));
        styliserBouton(boutonActualiser, new Color(200, 200, 200));

        boutonAjouter.addActionListener(e -> ajouterCours());
        boutonModifier.addActionListener(e -> modifierCours());
        boutonSupprimer.addActionListener(e -> supprimerCours());
        boutonActualiser.addActionListener(e -> chargerDonnees());

        if (utilisateurConnecte.getRole().equals("ETUDIANT") ||
                utilisateurConnecte.getRole().equals("ENSEIGNANT")) {
            boutonAjouter.setVisible(false);
            boutonModifier.setVisible(false);
            boutonSupprimer.setVisible(false);
        }

        panelBoutons.add(boutonAjouter);
        panelBoutons.add(boutonModifier);
        panelBoutons.add(boutonSupprimer);
        panelBoutons.add(boutonActualiser);
        add(panelBoutons, BorderLayout.SOUTH);

        chargerDonnees();
    }

    private void chargerDonnees() {
        modeleTableau.setRowCount(0);
        List<Cours> cours = coursDAO.getTousLesCours();
        for (Cours c : cours) {
            String enseignant = c.getEnseignant() != null ?
                    c.getEnseignant().getPrenom() + " " + c.getEnseignant().getNom() : "Non assigné";
            modeleTableau.addRow(new Object[]{
                    c.getId(), c.getCode(), c.getIntitule(),
                    enseignant, c.getDepartement(), c.getCredits(), c.getHeuresParSemaine()
            });
        }
    }

    private void ajouterCours() {
        List<Utilisateur> enseignants = utilisateurDAO.getTousLesUtilisateurs()
                .stream().filter(u -> u.getRole().equals("ENSEIGNANT"))
                .collect(java.util.stream.Collectors.toList());

        String[] nomsEnseignants = enseignants.stream()
                .map(u -> u.getPrenom() + " " + u.getNom())
                .toArray(String[]::new);

        JTextField champCode = new JTextField();
        JTextField champIntitule = new JTextField();
        JTextField champDepartement = new JTextField();
        JTextField champCredits = new JTextField("3");
        JTextField champHeures = new JTextField("2");
        JComboBox<String> comboEnseignant = new JComboBox<>(nomsEnseignants);

        Object[] champs = {
                "Code :", champCode,
                "Intitulé :", champIntitule,
                "Enseignant :", comboEnseignant,
                "Département :", champDepartement,
                "Crédits :", champCredits,
                "Heures/Semaine :", champHeures
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Ajouter un cours", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Cours c = new Cours();
            c.setCode(champCode.getText());
            c.setIntitule(champIntitule.getText());
            c.setDepartement(champDepartement.getText());
            c.setCredits(Integer.parseInt(champCredits.getText()));
            c.setHeuresParSemaine(Integer.parseInt(champHeures.getText()));
            c.setEnseignantId(enseignants.get(comboEnseignant.getSelectedIndex()).getId());

            if (coursDAO.ajouter(c)) {
                JOptionPane.showMessageDialog(this, "Cours ajouté avec succès !");
                chargerDonnees();
            }
        }
    }

    private void modifierCours() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un cours à modifier !");
            return;
        }

        JTextField champIntitule = new JTextField(modeleTableau.getValueAt(ligne, 2).toString());
        JTextField champDepartement = new JTextField(modeleTableau.getValueAt(ligne, 4).toString());
        JTextField champCredits = new JTextField(modeleTableau.getValueAt(ligne, 5).toString());
        JTextField champHeures = new JTextField(modeleTableau.getValueAt(ligne, 6).toString());

        Object[] champs = {
                "Intitulé :", champIntitule,
                "Département :", champDepartement,
                "Crédits :", champCredits,
                "Heures/Semaine :", champHeures
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Modifier le cours", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Cours c = new Cours();
            c.setId((int) modeleTableau.getValueAt(ligne, 0));
            c.setIntitule(champIntitule.getText());
            c.setDepartement(champDepartement.getText());
            c.setCredits(Integer.parseInt(champCredits.getText()));
            c.setHeuresParSemaine(Integer.parseInt(champHeures.getText()));

            if (coursDAO.modifier(c)) {
                JOptionPane.showMessageDialog(this, "Cours modifié avec succès !");
                chargerDonnees();
            }
        }
    }

    private void supprimerCours() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un cours à supprimer !");
            return;
        }

        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce cours ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            int id = (int) modeleTableau.getValueAt(ligne, 0);
            if (coursDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Cours supprimé !");
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
