package views;

import dao.BatimentDAO;
import models.Batiment;
import models.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BatimentView extends JPanel {

    private BatimentDAO batimentDAO;
    private JTable tableau;
    private DefaultTableModel modeleTableau;
    private Utilisateur utilisateurConnecte;

    public BatimentView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.batimentDAO = new BatimentDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        initialiserInterface();
    }

    private void initialiserInterface() {
        // Titre
        JLabel titre = new JLabel("🏛️ Gestion des Bâtiments");
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titre, BorderLayout.NORTH);

        // Tableau
        String[] colonnes = {"ID", "Nom", "Code", "Localisation", "Étages", "Description"};
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

        boutonAjouter.addActionListener(e -> ajouterBatiment());
        boutonModifier.addActionListener(e -> modifierBatiment());
        boutonSupprimer.addActionListener(e -> supprimerBatiment());
        boutonActualiser.addActionListener(e -> chargerDonnees());

        // Cacher boutons si étudiant ou enseignant
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
        List<Batiment> batiments = batimentDAO.getTousLesBatiments();
        for (Batiment b : batiments) {
            modeleTableau.addRow(new Object[]{
                    b.getId(), b.getNom(), b.getCode(),
                    b.getLocalisation(), b.getNombreEtages(), b.getDescription()
            });
        }
    }

    private void ajouterBatiment() {
        JTextField champNom = new JTextField();
        JTextField champCode = new JTextField();
        JTextField champLocalisation = new JTextField();
        JTextField champEtages = new JTextField("1");

        Object[] champs = {
                "Nom :", champNom,
                "Code :", champCode,
                "Localisation :", champLocalisation,
                "Nombre d'étages :", champEtages
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Ajouter un bâtiment", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Batiment b = new Batiment();
            b.setNom(champNom.getText());
            b.setCode(champCode.getText());
            b.setLocalisation(champLocalisation.getText());
            b.setNombreEtages(Integer.parseInt(champEtages.getText()));

            if (batimentDAO.ajouter(b)) {
                JOptionPane.showMessageDialog(this, "Bâtiment ajouté avec succès !");
                chargerDonnees();
            }
        }
    }

    private void modifierBatiment() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un bâtiment à modifier !");
            return;
        }

        JTextField champNom = new JTextField(modeleTableau.getValueAt(ligne, 1).toString());
        JTextField champLocalisation = new JTextField(modeleTableau.getValueAt(ligne, 3).toString());
        JTextField champEtages = new JTextField(modeleTableau.getValueAt(ligne, 4).toString());

        Object[] champs = {
                "Nom :", champNom,
                "Localisation :", champLocalisation,
                "Nombre d'étages :", champEtages
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Modifier le bâtiment", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Batiment b = new Batiment();
            b.setId((int) modeleTableau.getValueAt(ligne, 0));
            b.setNom(champNom.getText());
            b.setLocalisation(champLocalisation.getText());
            b.setNombreEtages(Integer.parseInt(champEtages.getText()));

            if (batimentDAO.modifier(b)) {
                JOptionPane.showMessageDialog(this, "Bâtiment modifié avec succès !");
                chargerDonnees();
            }
        }
    }

    private void supprimerBatiment() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un bâtiment à supprimer !");
            return;
        }

        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce bâtiment ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            int id = (int) modeleTableau.getValueAt(ligne, 0);
            if (batimentDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Bâtiment supprimé !");
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