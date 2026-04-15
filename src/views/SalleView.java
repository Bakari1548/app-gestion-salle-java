package views;

import dao.SalleDAO;
import dao.BatimentDAO;
import models.Salle;
import models.Batiment;
import models.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalleView extends JPanel {

    private SalleDAO salleDAO;
    private BatimentDAO batimentDAO;
    private JTable tableau;
    private DefaultTableModel modeleTableau;
    private Utilisateur utilisateurConnecte;

    public SalleView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.salleDAO = new SalleDAO();
        this.batimentDAO = new BatimentDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        initialiserInterface();
    }

    private void initialiserInterface() {
        // Titre
        JLabel titre = new JLabel("🚪 Gestion des Salles");
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titre, BorderLayout.NORTH);

        // Tableau
        String[] colonnes = {"ID", "Bâtiment", "Numéro", "Nom", "Étage", "Capacité", "Type"};
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
        JButton boutonSupprimer = new JButton("🗑️ Supprimer");
        JButton boutonActualiser = new JButton("🔄 Actualiser");
        JButton boutonRechercher = new JButton("🔍 Rechercher disponible");

        styliserBouton(boutonAjouter, new Color(100, 200, 100));
        styliserBouton(boutonSupprimer, new Color(255, 100, 100));
        styliserBouton(boutonActualiser, new Color(200, 200, 200));
        styliserBouton(boutonRechercher, new Color(100, 180, 255));

        boutonAjouter.addActionListener(e -> ajouterSalle());
        boutonSupprimer.addActionListener(e -> supprimerSalle());
        boutonActualiser.addActionListener(e -> chargerDonnees());
        boutonRechercher.addActionListener(e -> rechercherDisponible());

        if (utilisateurConnecte.getRole().equals("ETUDIANT")) {
            boutonAjouter.setVisible(false);
            boutonSupprimer.setVisible(false);
        }

        panelBoutons.add(boutonAjouter);
        panelBoutons.add(boutonSupprimer);
        panelBoutons.add(boutonActualiser);
        panelBoutons.add(boutonRechercher);
        add(panelBoutons, BorderLayout.SOUTH);

        chargerDonnees();
    }

    private void chargerDonnees() {
        modeleTableau.setRowCount(0);
        List<Salle> salles = salleDAO.getToutesLesSalles();
        List<Batiment> batiments = batimentDAO.getTousLesBatiments();
        for (Salle s : salles) {
            String nomBatiment = batiments.stream()
                    .filter(b -> b.getId() == s.getBatimentId())
                    .map(Batiment::getNom)
                    .findFirst().orElse("Inconnu");
            modeleTableau.addRow(new Object[]{
                    s.getId(), nomBatiment, s.getNumeroSalle(),
                    s.getNom(), s.getEtage(), s.getCapacite(), s.getTypeSalle()
            });
        }
    }

    private void ajouterSalle() {
        List<Batiment> batiments = batimentDAO.getTousLesBatiments();
        String[] nomsBatiments = batiments.stream()
                .map(Batiment::getNom).toArray(String[]::new);

        JComboBox<String> comboBatiment = new JComboBox<>(nomsBatiments);
        JTextField champNumero = new JTextField();
        JTextField champNom = new JTextField();
        JTextField champEtage = new JTextField("0");
        JTextField champCapacite = new JTextField();
        JComboBox<String> comboType = new JComboBox<>(
                new String[]{"TD", "TP", "AMPHI", "REUNION"});

        Object[] champs = {
                "Bâtiment :", comboBatiment,
                "Numéro :", champNumero,
                "Nom :", champNom,
                "Étage :", champEtage,
                "Capacité :", champCapacite,
                "Type :", comboType
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Ajouter une salle", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Salle s = new Salle();
            s.setBatimentId(batiments.get(comboBatiment.getSelectedIndex()).getId());
            s.setNumeroSalle(champNumero.getText());
            s.setNom(champNom.getText());
            s.setEtage(Integer.parseInt(champEtage.getText()));
            s.setCapacite(Integer.parseInt(champCapacite.getText()));
            s.setTypeSalle(comboType.getSelectedItem().toString());

            if (salleDAO.ajouter(s)) {
                JOptionPane.showMessageDialog(this, "Salle ajoutée avec succès !");
                chargerDonnees();
            }
        }
    }

    private void supprimerSalle() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une salle à supprimer !");
            return;
        }

        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cette salle ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            int id = (int) modeleTableau.getValueAt(ligne, 0);
            if (salleDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Salle supprimée !");
                chargerDonnees();
            }
        }
    }

    private void rechercherDisponible() {
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        JComboBox<String> comboJour = new JComboBox<>(jours);
        JTextField champHeure = new JTextField("08:00");

        Object[] champs = {
                "Jour :", comboJour,
                "Heure (HH:MM) :", champHeure
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Rechercher salle disponible", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int jour = comboJour.getSelectedIndex() + 1;
            List<Salle> disponibles = salleDAO.getSallesDisponibles(
                    jour, champHeure.getText(), 120);

            modeleTableau.setRowCount(0);
            List<Batiment> batiments = batimentDAO.getTousLesBatiments();
            for (Salle s : disponibles) {
                String nomBatiment = batiments.stream()
                        .filter(b -> b.getId() == s.getBatimentId())
                        .map(Batiment::getNom)
                        .findFirst().orElse("Inconnu");
                modeleTableau.addRow(new Object[]{
                        s.getId(), nomBatiment, s.getNumeroSalle(),
                        s.getNom(), s.getEtage(), s.getCapacite(), s.getTypeSalle()
                });
            }

            JOptionPane.showMessageDialog(this,
                    disponibles.size() + " salle(s) disponible(s) trouvée(s) !");
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