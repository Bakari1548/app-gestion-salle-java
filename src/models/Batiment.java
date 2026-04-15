package models;

import java.awt.Rectangle;

public class Batiment {
    private int id;
    private String nom;
    private String code;
    private String localisation;
    private int nombreEtages;
    private String description;
    
    // Coordonnées pour la carte interactive
    private int x, y, largeur, hauteur;

    public Batiment() {}

    public Batiment(String nom, String code, String localisation, int nombreEtages) {
        this.nom = nom;
        this.code = code;
        this.localisation = localisation;
        this.nombreEtages = nombreEtages;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public int getNombreEtages() { return nombreEtages; }
    public void setNombreEtages(int nombreEtages) { this.nombreEtages = nombreEtages; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Getters et Setters pour les coordonnées
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getLargeur() { return largeur; }
    public void setLargeur(int largeur) { this.largeur = largeur; }

    public int getHauteur() { return hauteur; }
    public void setHauteur(int hauteur) { this.hauteur = hauteur; }

    // Méthode pour définir les coordonnées
    public void setCoordonnees(int x, int y, int largeur, int hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    // Méthode pour vérifier si un point est dans le rectangle
    public boolean contientPoint(int pointX, int pointY) {
        return pointX >= x && pointX <= x + largeur && 
               pointY >= y && pointY <= y + hauteur;
    }

    @Override
    public String toString() {
        return nom + " (" + code + ")";
    }
}