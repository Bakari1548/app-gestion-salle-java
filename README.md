# UNIV-SCHEDULER - Application de Gestion des Salles

## Description

UNIV-SCHEDULER est une application Java Swing complète pour la gestion des salles universitaires. Elle permet de gérer les bâtiments, salles, cours, emplois du temps et réservations avec une interface utilisateur intuitive et des fonctionnalités avancées de visualisation et reporting.

## Fonctionnalités Principales

### 1. Gestion des Ressources
- **Bâtiments** : Ajout, modification, suppression des bâtiments du campus
- **Salles** : Gestion des salles avec capacité, type, et affectation aux bâtiments
- **Cours** : Création et gestion des cours avec enseignants et départements
- **Utilisateurs** : Système d'utilisateurs avec rôles (Admin, Gestionnaire, Enseignant, Étudiant)

### 2. Planification
- **Emplois du temps** : Création et gestion des plannings hebdomadaires
- **Détection de conflits** : Vérification automatique des conflits de réservation
- **Réservations ponctuelles** : Système de réservation pour événements spéciaux

### 3. Visualisation et Rapports
- **Dashboard** : Statistiques en temps réel avec taux d'occupation et alertes
- **Historique** : Consultation complète des réservations avec filtres avancés
- **Exports** : Exportation des données en CSV, Excel (HTML), et PDF
- **Rapports** : Génération de rapports hebdomadaires et mensuels d'utilisation

## Architecture Technique

### Technologies Utilisées
- **Langage** : Java 21
- **Interface** : Java Swing
- **Base de données** : SQLite
- **Architecture** : MVC (Modèle-Vue-Contrôleur)
- **JDBC Driver** : sqlite-jdbc-3.51.2.0.jar

### Structure du Projet
```
src/
|-- Main.java                    # Point d'entrée de l'application
|-- database/
|   `-- DatabaseConnection.java # Connexion à la base de données
|-- models/                      # Classes métier
|   |-- Batiment.java
|   |-- Salle.java
|   |-- Cours.java
|   |-- EmploiDuTemps.java
|   `-- Utilisateur.java
|-- dao/                         # Accès aux données (DAO)
|   |-- BatimentDAO.java
|   |-- SalleDAO.java
|   |-- CoursDAO.java
|   |-- EmploiDuTempsDAO.java
|   `-- UtilisateurDAO.java
|-- views/                       # Interface utilisateur
|   |-- MainView.java
|   |-- LoginView.java
|   |-- DashboardView.java
|   |-- HistoriqueView.java
|   |-- BatimentView.java
|   |-- SalleView.java
|   |-- CoursView.java
|   |-- EmploiDuTempsView.java
|   `-- UtilisateurView.java
|-- services/                    # Services métier
|   `-- ExportService.java
`-- resources/
    `-- sql/
        `-- init_database.sql    # Script de création de la base
```

## Installation et Configuration

### Prérequis

1. **Java Development Kit (JDK) 21 ou supérieur**
   ```bash
   # Vérification de la version Java
   java -version
   ```

2. **SQLite JDBC Driver**
   - Télécharger : [sqlite-jdbc-3.51.2.0.jar](https://github.com/xerial/sqlite-jdbc/releases)
   - Placer le fichier dans le répertoire `lib/` du projet

### Installation

1. **Cloner le projet**
   ```bash
   git clone <URL_DU_DEPOT_GITHUB>
   cd app-gestion-salle-java
   ```

2. **Créer le répertoire lib et ajouter le driver**
   ```bash
   mkdir lib
   # Copier sqlite-jdbc-3.51.2.0.jar dans lib/
   ```

3. **Compiler le projet**
   ```bash
   # Compilation
   javac -cp ".:lib/sqlite-jdbc-3.51.2.0.jar" -d out/production/univ-scheduler src/*.java src/*/*.java src/*/*/*.java
   
   # Ou avec un IDE (IntelliJ, Eclipse)
   ```

4. **Créer le JAR exécutable**
   ```bash
   # Créer le manifeste
   echo "Main-Class: Main" > MANIFEST.MF
   echo "Class-Path: lib/sqlite-jdbc-3.51.2.0.jar" >> MANIFEST.MF
   
   # Créer le JAR
   jar cvfm univ-scheduler.jar MANIFEST.MF -C out/production/univ-scheduler .
   ```

### Configuration de la Base de Données

La base de données SQLite est créée automatiquement au premier lancement avec le script `src/resources/sql/init_database.sql`.

**Utilisateurs par défaut :**
- **Admin** : `admin` / `password123`
- **Gestionnaire** : `gestionnaire1` / `password123`
- **Enseignant** : `prof1` / `password123`
- **Étudiant** : `etudiant1` / `password123`

## Utilisation

### Lancement de l'Application

1. **Via le JAR**
   ```bash
   java -jar univ-scheduler.jar
   ```

2. **Via l'IDE**
   - Exécuter la classe `Main.java`

### Guide d'Utilisation

#### 1. Connexion
- Utiliser les identifiants par défaut
- Le rôle détermine les fonctionnalités accessibles

#### 2. Navigation Principale
- **Dashboard** : Vue d'ensemble avec statistiques
- **Historique** : Consultation des réservations avec filtres (non accessible aux étudiants)
- **Bâtiments** : Gestion des bâtiments du campus (non accessible aux étudiants)
- **Salles** : Gestion des salles et équipements (non accessible aux étudiants)
- **Cours** : Gestion des cours et enseignants (non accessible aux étudiants)
- **Emplois du temps** : Planification des séances
- **Rapports** : Exports et rapports d'utilisation (non accessible aux étudiants)

#### 3. Fonctionnalités par Rôle
- **Admin** : Accès complet à toutes les fonctionnalités
- **Gestionnaire** : Gestion des salles, bâtiments, emplois du temps, rapports
- **Enseignant** : Consultation des emplois du temps, réservations, rapports
- **Étudiant** : Consultation des emplois du temps et dashboard uniquement

### Exports et Rapports

#### Formats Disponibles
- **CSV** : Compatible Excel, traitement de données
- **Excel (HTML)** : Format HTML ouvrable dans Excel
- **PDF** : Rapport formaté pour impression
- **Texte** : Rapports hebdomadaires/mensuels

#### Procédure d'Export
1. Accéder à la section "Rapports"
2. Choisir le format désiré
3. Sélectionner l'emplacement de sauvegarde
4. Le fichier est généré avec les données actuelles

## Développement

### Compilation et Tests

```bash
# Compilation complète
javac -cp ".:lib/sqlite-jdbc-3.51.2.0.jar" -d out src/**/*.java

# Tests unitaires (si disponibles)
java -cp ".:lib/sqlite-jdbc-3.51.2.0.jar:out" org.junit.runner.JUnitCore TestSuite
```

### Documentation

La Javadoc complète est générée avec :
```bash
javadoc -cp ".:lib/sqlite-jdbc-3.51.2.0.jar" -d doc src/**/*.java
```

### Extension du Projet

Pour ajouter de nouvelles fonctionnalités :

1. **Modèles** : Ajouter dans `src/models/`
2. **DAO** : Créer la classe d'accès aux données dans `src/dao/`
3. **Vues** : Créer l'interface dans `src/views/`
4. **Intégration** : Modifier `MainView.java` pour ajouter au menu

## Dépannage

### Problèmes Communs

1. **Erreur de connexion à la base**
   - Vérifier que le driver SQLite est dans `lib/`
   - Vérifier les permissions d'écriture

2. **ClassNotFoundException**
   - Assurer que le classpath inclut le driver SQLite
   - Vérifier le fichier MANIFEST.MF

3. **Interface ne s'affiche pas**
   - Vérifier la version Java (minimum 11)
   - Exécuter en mode graphique supporté

### Logs et Erreurs

Les erreurs sont affichées dans la console. Pour les logs détaillés :
```bash
java -jar univ-scheduler.jar > application.log 2>&1
```

## Maintenance

### Sauvegarde des Données

La base SQLite est dans `univscheduler.db`. Sauvegarder régulièrement :
```bash
cp univscheduler.db backup/univscheduler_$(date +%Y%m%d).db
```

### Mise à Jour

1. Sauvegarder la base de données
2. Remplacer les fichiers JAR
3. Mettre à jour la base si nécessaire

## Support et Contact

Pour toute question ou problème :
- **Documentation** : Consulter ce README et la Javadoc
- **Issues** : Créer un ticket sur le dépôt GitHub
- **Contact** : [email de l'équipe de développement]

## Licence

Ce projet est sous licence [MIT License](LICENSE).

## Remerciements

- Université pour les besoins en gestion des salles
- Équipe de développement pour l'implémentation
- Communauté open source pour les bibliothèques utilisées

---

**Version** : 1.0  
**Dernière mise à jour** : Avril 2026  
**Développeurs** : Équipe POO 1 - Semestre 4
