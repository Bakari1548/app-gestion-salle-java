# Manuel Utilisateur - UNIV-SCHEDULER

## Table des Matières

1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Premier Lancement](#premier-lancement)
4. [Interface Principale](#interface-principale)
5. [Fonctionnalités par Rôle](#fonctionnalités-par-rôle)
6. [Guide Détaillé](#guide-détaillé)
7. [Exports et Rapports](#exports-et-rapports)
8. [Dépannage](#dépannage)

---

## Introduction

UNIV-SCHEDULER est une application de gestion des salles universitaires conçue pour simplifier la planification et la réservation des espaces. Elle offre une interface intuitive pour gérer les bâtiments, salles, cours et emplois du temps.

### Points Clés
- **Interface Swing** : Simple et intuitive
- **Multi-rôles** : Admin, Gestionnaire, Enseignant, Étudiant
- **Rapports** : Exports multi-formats
- **Visualisation** : Dashboard avec statistiques

---

## Installation

### Prérequis
- Java 21 ou supérieur
- 2GB de RAM minimum
- 100MB d'espace disque

### Étapes d'Installation

1. **Décompresser l'archive**
   ```bash
   unzip univ-scheduler-v1.0.zip
   cd univ-scheduler
   ```

2. **Vérifier Java**
   ```bash
   java -version
   ```

3. **Lancer l'application**
   - **Windows** : Double-cliquer sur `run.bat`
   - **Linux/Mac** : Exécuter `./run.sh`
   - **Manuel** : `java -jar univ-scheduler.jar`

---

## Premier Lancement

### Écran de Connexion

Au premier lancement, vous verrez l'écran de connexion :

**Identifiants par défaut :**

| Rôle | Nom d'utilisateur | Mot de passe |
|------|------------------|--------------|
| Admin | admin | password123 |
| Gestionnaire | gestionnaire1 | password123 |
| Enseignant | prof1 | password123 |
| Étudiant | etudiant1 | password123 |

### Étapes de Connexion

1. Saisir le nom d'utilisateur
2. Saisir le mot de passe
3. Cliquer sur "Se connecter"

L'application vérifie les identifiants et affiche l'interface principale selon le rôle de l'utilisateur.

---

## Interface Principale

### Structure de l'Interface

L'interface principale se compose de :

1. **Menu Gauche** : Navigation principale
2. **Zone Centrale** : Contenu dynamique
3. **En-tête** : Informations utilisateur

### Menu de Navigation

- **Dashboard** : Vue d'ensemble avec statistiques (tous les rôles)
- **Historique** : Consultation des réservations (non accessible aux étudiants)
- **Bâtiments** : Gestion des bâtiments (non accessible aux étudiants)
- **Salles** : Gestion des salles (non accessible aux étudiants)
- **Cours** : Gestion des cours (non accessible aux étudiants)
- **Emplois du temps** : Planification (tous les rôles)
- **Rapports** : Exports et rapports (non accessible aux étudiants)
- **Utilisateurs** : Gestion des comptes (Admin seulement)
- **Déconnexion** : Quitter l'application

**Note** : Les étudiants ont un accès limité aux fonctionnalités de base pour des raisons de sécurité et de pertinence pédagogique.

---

## Fonctionnalités par Rôle

### Administrateur

Accès complet à toutes les fonctionnalités :
- Gestion des utilisateurs
- Configuration système
- Rapports avancés
- Maintenance des données

### Gestionnaire

Gestion opérationnelle :
- Bâtiments et salles
- Emplois du temps
- Réservations
- Rapports d'utilisation

### Enseignant

Fonctionnalités pédagogiques :
- Consultation des emplois du temps
- Réservation de salles
- Historique des cours

### Étudiant

Consultation uniquement :
- Dashboard avec statistiques générales
- Emplois du temps
- Informations salles (via emplois du temps)

---

## Guide Détaillé

### Dashboard

Le dashboard offre une vue synthétique de l'utilisation des salles :

**Statistiques affichées :**
- Nombre de bâtiments, salles, cours
- Taux d'occupation global
- Salles critiques (>80% d'occupation)
- Graphiques par bâtiment et par jour

**Actions possibles :**
- Actualiser les données
- Cliquer sur les cartes pour plus de détails

### Gestion des Bâtiments

**Ajouter un bâtiment :**
1. Cliquer sur "Bâtiments" dans le menu
2. Cliquer sur "Ajouter"
3. Remplir les champs :
   - Nom du bâtiment
   - Code unique
   - Localisation
   - Nombre d'étages
   - Description
4. Cliquer sur "Enregistrer"

**Modifier un bâtiment :**
1. Sélectionner le bâtiment dans la liste
2. Cliquer sur "Modifier"
3. Mettre à jour les informations
4. Cliquer sur "Sauvegarder"

**Supprimer un bâtiment :**
1. Sélectionner le bâtiment
2. Cliquer sur "Supprimer"
3. Confirmer la suppression

### Gestion des Salles

**Ajouter une salle :**
1. Cliquer sur "Salles" dans le menu
2. Cliquer sur "Ajouter"
3. Remplir les informations :
   - Bâtiment (sélection dans la liste)
   - Numéro de salle
   - Nom optionnel
   - Étage
   - Capacité
   - Type (TD, TP, AMPHI, REUNION)
4. Cliquer sur "Enregistrer"

**Types de salles :**
- **TD** : Salle de travaux dirigés (20-40 personnes)
- **TP** : Salle de travaux pratiques (15-30 personnes)
- **AMPHI** : Amphithéâtre (100+ personnes)
- **REUNION** : Salle de réunion (10-20 personnes)

### Gestion des Cours

**Créer un cours :**
1. Cliquer sur "Cours" dans le menu
2. Cliquer sur "Ajouter"
3. Remplir les informations :
   - Code unique du cours
   - Intitulé
   - Description
   - Enseignant responsable
   - Département
   - Crédits
   - Heures par semaine
4. Cliquer sur "Enregistrer"

### Emplois du Temps

**Créer une séance :**
1. Cliquer sur "Emplois du temps"
2. Cliquer sur "Ajouter"
3. Sélectionner :
   - Cours
   - Salle
   - Jour de la semaine (1-7)
   - Heure de début
   - Durée (en minutes)
   - Groupe classe
   - Année académique
   - Semestre (1 ou 2)
4. Cliquer sur "Enregistrer"

**Détection de conflits :**
L'application vérifie automatiquement :
- Conflits de salle (même salle, même heure)
- Disponibilité de la salle
- Chevauchement des horaires

### Historique des Réservations

**Consultation :**
1. Cliquer sur "Historique"
2. Utiliser les filtres :
   - Bâtiment
   - Salle
   - Période
   - Recherche textuelle
3. Consulter le tableau des réservations

**Export :**
1. Cliquer sur "Exporter"
2. Choisir le format (CSV)
3. Sélectionner l'emplacement
4. Enregistrer le fichier

---

## Exports et Rapports

### Formats Disponibles

#### CSV
- Compatible Excel
- Traitement de données facile
- Import dans d'autres systèmes

#### Excel (HTML)
- Format HTML ouvrable dans Excel
- Mise en forme automatique
- Graphiques possibles

#### PDF
- Format d'impression
- Partage facile
- Mise en forme professionnelle

#### Texte
- Rapports détaillés
- Analyse hebdomadaire/mensuelle
- Statistiques complètes

### Générer un Rapport

1. Cliquer sur "Rapports" dans le menu
2. Choisir le type de rapport :
   - Export CSV/Excel/PDF
   - Rapport hebdomadaire
   - Rapport mensuel
   - Rapport complet
3. Sélectionner l'emplacement de sauvegarde
4. Confirmer la génération

### Contenu des Rapports

**Rapport hebdomadaire :**
- Statistiques générales
- Occupation par bâtiment
- Salles critiques
- Répartition par jour

**Rapport mensuel :**
- Analyse tendances
- Top 10 salles utilisées
- Répartition par type
- Recommandations



## Conclusion

UNIV-SCHEDULER est conçu pour simplifier la gestion des salles universitaires. Avec une interface intuitive et des fonctionnalités complètes, il permet une planification efficace et un suivi détaillé de l'utilisation des ressources.

Pour toute question ou suggestion d'amélioration, n'hésitez pas à contacter l'équipe de développement.

---

**Version** : 1.0  
**Date** : Avril 2026  
