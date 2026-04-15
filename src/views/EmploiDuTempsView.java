package views;

import dao.EmploiDuTempsDAO;
import dao.CoursDAO;
import dao.SalleDAO;
import models.EmploiDuTemps;
import models.Cours;
import models.Salle;
import models.Utilisateur;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmploiDuTempsView extends JPanel {

    private EmploiDuTempsDAO emploiDAO;
    private CoursDAO coursDAO;
    private SalleDAO salleDAO;
    private JTable tableau;
    private DefaultTableModel modeleTableau;
    private Utilisateur utilisateurConnecte;

    public EmploiDuTempsView(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.emploiDAO = new EmploiDuTempsDAO();
        this.coursDAO = new CoursDAO();
        this.salleDAO = new SalleDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));
        initialiserInterface();
    }

    private void initialiserInterface() {
        JLabel titre = new JLabel("📅 Emplois du Temps");
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titre, BorderLayout.NORTH);

        String[] colonnes = {"ID", "Cours", "Salle", "Jour", "Heure", "Durée", "Groupe", "Semestre"};
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

        boutonAjouter.addActionListener(e -> ajouterEmploi());
        boutonSupprimer.addActionListener(e -> supprimerEmploi());
        boutonActualiser.addActionListener(e -> chargerDonnees());

        if (utilisateurConnecte.getRole().equals("ETUDIANT") ||
                utilisateurConnecte.getRole().equals("ENSEIGNANT")) {
            boutonAjouter.setVisible(false);
            boutonSupprimer.setVisible(false);
        }

        panelBoutons.add(boutonAjouter);
        panelBoutons.add(boutonSupprimer);
        panelBoutons.add(boutonActualiser);
        add(panelBoutons, BorderLayout.SOUTH);

        chargerDonnees();
    }

    private void chargerDonnees() {
        modeleTableau.setRowCount(0);
        List<EmploiDuTemps> emplois = emploiDAO.getTousLesEmplois();
        for (EmploiDuTemps e : emplois) {
            String[] jours = {"", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
            modeleTableau.addRow(new Object[]{
                    e.getId(),
                    e.getCours() != null ? e.getCours().getIntitule() : "N/A",
                    e.getSalle() != null ? e.getSalle().getNom() : "N/A",
                    jours[e.getJourSemaine()],
                    e.getHeureDebut(),
                    e.getDureeMinutes() + " min",
                    e.getGroupeClasse(),
                    e.getSemestre()
            });
        }
    }

    private void ajouterEmploi() {
        List<Cours> cours = coursDAO.getTousLesCours();
        List<Salle> salles = salleDAO.getToutesLesSalles();

        String[] nomsCours = cours.stream()
                .map(c -> c.getCode() + " - " + c.getIntitule())
                .toArray(String[]::new);
        String[] nomsSalles = salles.stream()
                .map(Salle::getNom)
                .toArray(String[]::new);

        JComboBox<String> comboCours = new JComboBox<>(nomsCours);
        JComboBox<String> comboSalle = new JComboBox<>(nomsSalles);
        JComboBox<String> comboJour = new JComboBox<>(
                new String[]{"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"});
        JTextField champHeure = new JTextField("08:00");
        JTextField champDuree = new JTextField("120");
        JTextField champGroupe = new JTextField("L2-INFO-A");
        JComboBox<String> comboSemestre = new JComboBox<>(new String[]{"1", "2"});

        Object[] champs = {
                "Cours :", comboCours,
                "Salle :", comboSalle,
                "Jour :", comboJour,
                "Heure début :", champHeure,
                "Durée (minutes) :", champDuree,
                "Groupe :", champGroupe,
                "Semestre :", comboSemestre
        };

        int result = JOptionPane.showConfirmDialog(this, champs,
                "Ajouter un emploi du temps", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Vérifier les conflits
            int jourIndex = comboJour.getSelectedIndex() + 1;
            boolean conflit = emploiDAO.verifierConflit(
                    salles.get(comboSalle.getSelectedIndex()).getId(),
                    jourIndex, champHeure.getText());

            if (conflit) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Conflit détecté ! Cette salle est déjà occupée !",
                        "Conflit", JOptionPane.WARNING_MESSAGE);
                return;
            }

            EmploiDuTemps e = new EmploiDuTemps();
            e.setCoursId(cours.get(comboCours.getSelectedIndex()).getId());
            e.setSalleId(salles.get(comboSalle.getSelectedIndex()).getId());
            e.setJourSemaine(jourIndex);
            e.setHeureDebut(champHeure.getText());
            e.setDureeMinutes(Integer.parseInt(champDuree.getText()));
            e.setGroupeClasse(champGroupe.getText());
            e.setAnneeAcademique("2025-2026");
            e.setSemestre(Integer.parseInt(comboSemestre.getSelectedItem().toString()));

            if (emploiDAO.ajouter(e)) {
                JOptionPane.showMessageDialog(this, "Emploi du temps ajouté avec succès !");
                chargerDonnees();
            }
        }
    }

    private void supprimerEmploi() {
        int ligne = tableau.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un emploi à supprimer !");
            return;
        }

        int reponse = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cet emploi du temps ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            int id = (int) modeleTableau.getValueAt(ligne, 0);
            if (emploiDAO.supprimer(id)) {
                JOptionPane.showMessageDialog(this, "Emploi supprimé !");
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