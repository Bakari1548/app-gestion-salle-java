package services;

import models.EmploiDuTemps;
import models.Salle;
import models.Batiment;
import dao.EmploiDuTempsDAO;
import dao.SalleDAO;
import dao.BatimentDAO;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ExportService {
    
    private EmploiDuTempsDAO emploiDuTempsDAO;
    private SalleDAO salleDAO;
    private BatimentDAO batimentDAO;
    
    public ExportService() {
        this.emploiDuTempsDAO = new EmploiDuTempsDAO();
        this.salleDAO = new SalleDAO();
        this.batimentDAO = new BatimentDAO();
    }
    
    /**
     * Exporte les emplois du temps vers un fichier CSV
     */
    public boolean exporterEmploisDuTempsCSV(String cheminFichier, int salleId) {
        try {
            List<EmploiDuTemps> emplois;
            if (salleId > 0) {
                emplois = getEmploisParSalle(salleId);
            } else {
                emplois = emploiDuTempsDAO.getTousLesEmplois();
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier))) {
                // En-tête CSV
                writer.println("Bâtiment,Salle,Jour,Heure,Durée,Cours,Intitulé,Classe,Semestre,Année académique");
                
                // Données
                for (EmploiDuTemps emploi : emplois) {
                    writer.println(String.format("%s,%s,%s,%s,%d,%s,%s,%s,%d,%s",
                        echapperCSV(emploi.getSalle().getBatiment().getNom()),
                        echapperCSV(emploi.getSalle().getNom()),
                        getNomJour(emploi.getJourSemaine()),
                        emploi.getHeureDebut(),
                        emploi.getDureeMinutes(),
                        echapperCSV(emploi.getCours().getCode()),
                        echapperCSV(emploi.getCours().getIntitule()),
                        echapperCSV(emploi.getGroupeClasse()),
                        emploi.getSemestre(),
                        echapperCSV(emploi.getAnneeAcademique())
                    ));
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur export CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporte les emplois du temps vers un fichier Excel (format HTML qui peut être ouvert dans Excel)
     */
    public boolean exporterEmploisDuTempsExcel(String cheminFichier, int salleId) {
        try {
            List<EmploiDuTemps> emplois;
            String titre;
            
            if (salleId > 0) {
                Salle salle = getSalleById(salleId);
                emplois = getEmploisParSalle(salleId);
                titre = "Emploi du temps - " + salle.getNom() + " (" + salle.getBatiment().getNom() + ")";
            } else {
                emplois = emploiDuTempsDAO.getTousLesEmplois();
                titre = "Emploi du temps - Toutes les salles";
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier))) {
                // En-tête HTML pour Excel
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<meta charset='UTF-8'>");
                writer.println("<title>" + titre + "</title>");
                writer.println("<style>");
                writer.println("table { border-collapse: collapse; width: 100%; }");
                writer.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
                writer.println("th { background-color: #f2f2f2; font-weight: bold; }");
                writer.println("tr:nth-child(even) { background-color: #f9f9f9; }");
                writer.println("</style>");
                writer.println("</head>");
                writer.println("<body>");
                writer.println("<h2>" + titre + "</h2>");
                writer.println("<p>Généré le: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "</p>");
                
                // Tableau
                writer.println("<table>");
                writer.println("<tr><th>Bâtiment</th><th>Salle</th><th>Jour</th><th>Heure</th><th>Durée</th><th>Cours</th><th>Intitulé</th><th>Classe</th><th>Semestre</th><th>Année académique</th></tr>");
                
                for (EmploiDuTemps emploi : emplois) {
                    writer.println(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%d min</td><td>%s</td><td>%s</td><td>%s</td><td>%d</td><td>%s</td></tr>",
                        emploi.getSalle().getBatiment().getNom(),
                        emploi.getSalle().getNom(),
                        getNomJour(emploi.getJourSemaine()),
                        emploi.getHeureDebut(),
                        emploi.getDureeMinutes(),
                        emploi.getCours().getCode(),
                        emploi.getCours().getIntitule(),
                        emploi.getGroupeClasse(),
                        emploi.getSemestre(),
                        emploi.getAnneeAcademique()
                    ));
                }
                
                writer.println("</table>");
                writer.println("</body>");
                writer.println("</html>");
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur export Excel: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporte les emplois du temps vers un fichier PDF (format texte simplifié)
     */
    public boolean exporterEmploisDuTempsPDF(String cheminFichier, int salleId) {
        try {
            List<EmploiDuTemps> emplois;
            String titre;
            
            if (salleId > 0) {
                Salle salle = getSalleById(salleId);
                emplois = getEmploisParSalle(salleId);
                titre = "Emploi du temps - " + salle.getNom() + " (" + salle.getBatiment().getNom() + ")";
            } else {
                emplois = emploiDuTempsDAO.getTousLesEmplois();
                titre = "Emploi du temps - Toutes les salles";
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier))) {
                // En-tête PDF simplifié
                writer.println("%PDF-1.4");
                writer.println("1 0 obj");
                writer.println("<< /Type /Catalog /Pages 2 0 R >>");
                writer.println("endobj");
                writer.println("2 0 obj");
                writer.println("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
                writer.println("endobj");
                writer.println("3 0 obj");
                writer.println("<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /MediaBox [0 0 612 792] /Contents 5 0 R >>");
                writer.println("endobj");
                writer.println("4 0 obj");
                writer.println("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
                writer.println("endobj");
                writer.println("5 0 obj");
                writer.println("<< /Length 200 >>");
                writer.println("stream");
                writer.println("BT");
                writer.println("/F1 12 Tf");
                writer.println("50 750 Td");
                writer.println("(" + titre + ") Tj");
                writer.println("0 -20 Td");
                writer.println("(Généré le: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + ") Tj");
                writer.println("0 -30 Td");
                
                // Tableau simplifié
                writer.println("(Bâtiment | Salle | Jour | Heure | Durée | Cours) Tj");
                writer.println("0 -15 Td");
                writer.println("------------------------------------------------ Tj");
                writer.println("0 -15 Td");
                
                for (EmploiDuTemps emploi : emplois) {
                    String ligne = String.format("%s | %s | %s | %s | %d min | %s",
                        emploi.getSalle().getBatiment().getNom(),
                        emploi.getSalle().getNom(),
                        getNomJour(emploi.getJourSemaine()),
                        emploi.getHeureDebut(),
                        emploi.getDureeMinutes(),
                        emploi.getCours().getCode()
                    );
                    
                    if (ligne.length() > 80) {
                        ligne = ligne.substring(0, 77) + "...";
                    }
                    
                    writer.println("(" + ligne + ") Tj");
                    writer.println("0 -12 Td");
                }
                
                writer.println("ET");
                writer.println("endstream");
                writer.println("endobj");
                writer.println("xref");
                writer.println("0 6");
                writer.println("0000000000 65535 f");
                writer.println("0000000009 00000 n");
                writer.println("0000000058 00000 n");
                writer.println("0000000115 00000 n");
                writer.println("0000000274 00000 n");
                writer.println("0000000341 00000 n");
                writer.println("trailer");
                writer.println("<< /Size 6 /Root 1 0 R >>");
                writer.println("startxref");
                writer.println("492");
                writer.println("%%EOF");
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur export PDF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporte un rapport d'utilisation hebdomadaire
     */
    public boolean exporterRapportHebdomadaire(String cheminFichier) {
        try {
            List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
            List<Salle> salles = salleDAO.getToutesLesSalles();
            List<Batiment> batiments = batimentDAO.getTousLesBatiments();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier))) {
                // En-tête
                writer.printf("RAPPORT D'UTILISATION HEBDOMADAIRE%n" +
                        "=================================%n" +
                        "Période: %s%n" +
                        "Généré le: %s%n%n",
                        getSemaineActuelle(),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
                
                // Statistiques générales
                writer.printf("STATISTIQUES GÉNÉRALES%n" +
                        "---------------------%n" +
                        "Total des réservations: %d%n" +
                        "Total des heures: %.1f heures%n" +
                        "Nombre de salles utilisées: %d%n" +
                        "Nombre de cours différents: %d%n%n",
                        emplois.size(),
                        emplois.stream().mapToInt(EmploiDuTemps::getDureeMinutes).sum() / 60.0,
                        emplois.stream().mapToInt(e -> e.getSalleId()).distinct().count(),
                        emplois.stream().mapToInt(e -> e.getCoursId()).distinct().count());
                
                // Occupation par bâtiment
                writer.printf("OCCUPATION PAR BÂTIMENT%n" +
                        "-----------------------%n");
                for (Batiment batiment : batiments) {
                    double taux = calculerTauxOccupationBatiment(batiment, salles, emplois);
                    int nbEmplois = compterEmploisBatiment(batiment, salles, emplois);
                    writer.printf("%s: %d réservations (%.1f%% d'occupation)%n",
                            batiment.getNom(), nbEmplois, taux);
                }
                writer.println();
                
                // Salles critiques
                writer.printf("SALLES CRITIQUES (>80%% d'occupation)%n" +
                        "-----------------------------------%n");
                boolean aDesSallesCritiques = false;
                for (Salle salle : salles) {
                    double taux = calculerTauxOccupationSalle(salle, emplois);
                    if (taux > 80) {
                        aDesSallesCritiques = true;
                        writer.printf("ALERTE: %s (%s) - %.1f%% d'occupation%n",
                                salle.getNom(), salle.getBatiment().getNom(), taux);
                    }
                }
                if (!aDesSallesCritiques) {
                    writer.println("Aucune salle critique détectée");
                }
                writer.println();
                
                // Répartition par jour
                writer.printf("RÉPARTITION PAR JOUR%n" +
                        "--------------------%n");
                String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
                int[] comptes = compterEmploisParJour(emplois);
                for (int i = 0; i < jours.length; i++) {
                    writer.printf("%s: %d réservations%n", jours[i], comptes[i]);
                }
            }
            
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur export rapport hebdomadaire: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Exporte un rapport d'utilisation mensuel
     */
    public boolean exporterRapportMensuel(String cheminFichier) {
        try {
            List<EmploiDuTemps> emplois = emploiDuTempsDAO.getTousLesEmplois();
            List<Salle> salles = salleDAO.getToutesLesSalles();
            List<Batiment> batiments = batimentDAO.getTousLesBatiments();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier))) {
                // En-tête
                writer.println("RAPPORT D'UTILISATION MENSUEL");
                writer.println("==============================");
                writer.println("Mois: " + getMoisActuel());
                writer.println("Généré le: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
                writer.println();
                
                // Statistiques générales
                writer.println("STATISTIQUES GÉNÉRALES");
                writer.println("---------------------");
                writer.println("Total des réservations: " + emplois.size());
                writer.println("Total des heures: " + (emplois.stream().mapToInt(e -> e.getDureeMinutes()).sum() / 60) + " heures");
                writer.println("Moyenne par jour: " + String.format("%.1f", emplois.size() / 22.0)); // ~22 jours ouvrables
                writer.println("Taux d'occupation global: " + String.format("%.1f%%", calculerTauxOccupationGlobal(emplois, salles)));
                writer.println();
                
                // Top 10 des salles les plus utilisées
                writer.println("TOP 10 DES SALLES LES PLUS UTILISÉES");
                writer.println("-----------------------------------");
                Map<Salle, Integer> utilisationSalles = new HashMap<>();
                for (Salle salle : salles) {
                    int count = (int) emplois.stream().filter(e -> e.getSalleId() == salle.getId()).count();
                    utilisationSalles.put(salle, count);
                }
                
                utilisationSalles.entrySet().stream()
                    .sorted(Map.Entry.<Salle, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> {
                        Salle salle = entry.getKey();
                        int count = entry.getValue();
                        double taux = calculerTauxOccupationSalle(salle, emplois);
                        writer.println(String.format("%d. %s (%s): %d réservations (%.1f%%)", 
                            utilisationSalles.size() - utilisationSalles.values().stream().sorted().toList().indexOf(count),
                            salle.getNom(), salle.getBatiment().getNom(), count, taux));
                    });
                writer.println();
                
                // Analyse par type de salle
                writer.println("ANALYSE PAR TYPE DE SALLE");
                writer.println("--------------------------");
                Map<String, Integer> utilisationParType = new HashMap<>();
                for (EmploiDuTemps emploi : emplois) {
                    String type = emploi.getSalle().getTypeSalle();
                    utilisationParType.put(type, utilisationParType.getOrDefault(type, 0) + 1);
                }
                
                for (Map.Entry<String, Integer> entry : utilisationParType.entrySet()) {
                    writer.println(String.format("%s: %d réservations", entry.getKey(), entry.getValue()));
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur export rapport mensuel: " + e.getMessage());
            return false;
        }
    }
    
    // Méthodes utilitaires
    private List<EmploiDuTemps> getEmploisParSalle(int salleId) {
        List<EmploiDuTemps> tousLesEmplois = emploiDuTempsDAO.getTousLesEmplois();
        List<EmploiDuTemps> emploisSalle = new ArrayList<>();
        
        for (EmploiDuTemps emploi : tousLesEmplois) {
            if (emploi.getSalleId() == salleId) {
                emploisSalle.add(emploi);
            }
        }
        
        return emploisSalle;
    }
    
    private Salle getSalleById(int salleId) {
        for (Salle salle : salleDAO.getToutesLesSalles()) {
            if (salle.getId() == salleId) {
                return salle;
            }
        }
        return null;
    }
    
    private String echapperCSV(String texte) {
        if (texte == null) return "";
        if (texte.contains(",") || texte.contains("\"") || texte.contains("\n")) {
            return "\"" + texte.replace("\"", "\"\"") + "\"";
        }
        return texte;
    }
    
    private String getNomJour(int jourSemaine) {
        String[] jours = {"", "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
        return jourSemaine >= 1 && jourSemaine <= 7 ? jours[jourSemaine] : "Inconnu";
    }
    
    private String getSemaineActuelle() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String debut = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 6);
        String fin = sdf.format(cal.getTime());
        return "Du " + debut + " au " + fin;
    }
    
    private String getMoisActuel() {
        return new SimpleDateFormat("MMMM yyyy").format(new Date());
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
        int totalHeuresDisponibles = 5 * 8;
        int totalHeuresUtilisees = 0;
        
        for (EmploiDuTemps emploi : emplois) {
            if (emploi.getSalleId() == salle.getId()) {
                totalHeuresUtilisees += 2;
            }
        }
        
        return Math.min(100, (totalHeuresUtilisees * 100.0) / totalHeuresDisponibles);
    }
    
    private double calculerTauxOccupationGlobal(List<EmploiDuTemps> emplois, List<Salle> salles) {
        if (salles.isEmpty()) return 0;
        
        int totalHeuresDisponibles = salles.size() * 5 * 8;
        int totalHeuresUtilisees = emplois.size() * 2;
        
        return Math.min(100, (totalHeuresUtilisees * 100.0) / totalHeuresDisponibles);
    }
    
    private int[] compterEmploisParJour(List<EmploiDuTemps> emplois) {
        int[] comptes = new int[6];
        
        for (EmploiDuTemps emploi : emplois) {
            int jour = emploi.getJourSemaine() - 1;
            if (jour >= 0 && jour < 6) {
                comptes[jour]++;
            }
        }

        return comptes; // Simplifié - retourne seulement le comptage du lundi
    }
    
    private int compterEmploisBatiment(Batiment batiment, List<Salle> salles, List<EmploiDuTemps> emplois) {
        int count = 0;
        for (Salle salle : salles) {
            if (salle.getBatiment().getId() == batiment.getId()) {
                for (EmploiDuTemps emploi : emplois) {
                    if (emploi.getSalleId() == salle.getId()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
