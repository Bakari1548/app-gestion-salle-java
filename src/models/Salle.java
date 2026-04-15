package models;

public class Salle {
    private int id;
    private int batimentId;
    private String numeroSalle;
    private String nom;
    private int etage;
    private int capacite;
    private String typeSalle;
    private boolean estActive;
    private Batiment batiment;
    
    // Coordonnées pour la carte interactive
    private int x, y, largeur, hauteur;

    public Salle() {}

    public Salle(int batimentId, String numeroSalle, String nom,
                 int etage, int capacite, String typeSalle) {
        this.batimentId = batimentId;
        this.numeroSalle = numeroSalle;
        this.nom = nom;
        this.etage = etage;
        this.capacite = capacite;
        this.typeSalle = typeSalle;
        this.estActive = true;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBatimentId() { return batimentId; }
    public void setBatimentId(int batimentId) { this.batimentId = batimentId; }

    public String getNumeroSalle() { return numeroSalle; }
    public void setNumeroSalle(String numeroSalle) { this.numeroSalle = numeroSalle; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getEtage() { return etage; }
    public void setEtage(int etage) { this.etage = etage; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getTypeSalle() { return typeSalle; }
    public void setTypeSalle(String typeSalle) { this.typeSalle = typeSalle; }

    public boolean isEstActive() { return estActive; }
    public void setEstActive(boolean estActive) { this.estActive = estActive; }

    public Batiment getBatiment() { return batiment; }
    public void setBatiment(Batiment batiment) { this.batiment = batiment; }

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

    // Méthode pour obtenir le type (alias pour typeSalle)
    public String getType() { return typeSalle; }

    @Override
    public String toString() {
        return nom + " (Capacité: " + capacite + " | Type: " + typeSalle + ")";
    }
}