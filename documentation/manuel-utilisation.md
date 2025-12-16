# 📖 Manuel d'Utilisation - Système de Gestion Universitaire

## Table des Matières

1. [Introduction](#1-introduction)
2. [Premiers Pas](#2-premiers-pas)
3. [Guide Étudiant](#3-guide-étudiant)
4. [Guide Enseignant](#4-guide-enseignant)
5. [Guide Administrateur](#5-guide-administrateur)
6. [Fonctionnalités Communes](#6-fonctionnalités-communes)
7. [FAQ et Dépannage](#7-faq-et-dépannage)

---

## 1. Introduction

### 1.1 Qu'est-ce que le Système de Gestion Universitaire ?

Le Système de Gestion Universitaire est une plateforme web complète qui permet de gérer l'ensemble des activités académiques et administratives de votre établissement. Le système offre différentes fonctionnalités selon votre rôle :

- **Étudiants** : Consulter les cours, s'inscrire, voir les notes et gérer les factures
- **Enseignants** : Gérer les cours, consulter les étudiants inscrits et saisir les notes
- **Administrateurs** : Gérer les utilisateurs et superviser l'ensemble du système

### 1.2 Accès au Système

**URL de l'application :** http://localhost:3000

**Navigateurs supportés :**

- Google Chrome (recommandé)
- Mozilla Firefox
- Microsoft Edge
- Safari

**Configuration minimale requise :**

- Connexion Internet stable
- Résolution d'écran minimale : 1280x720
- JavaScript activé

---

## 2. Premiers Pas

### 2.1 Création de Compte

#### Étape 1 : Accéder à la page d'inscription

1. Ouvrez votre navigateur web
2. Accédez à l'URL : http://localhost:3000
3. Cliquez sur le bouton **"S'inscrire"** ou **"Register"**

#### Étape 2 : Remplir le formulaire d'inscription

Le formulaire d'inscription comprend les champs suivants :

| Champ            | Description                  | Exemple                       |
| ---------------- | ---------------------------- | ----------------------------- |
| **Email**        | Votre adresse email (unique) | `marie.dupont@university.com` |
| **Mot de passe** | Minimum 8 caractères         | `SecurePass123!`              |
| **Prénom**       | Votre prénom                 | `Marie`                       |
| **Nom**          | Votre nom de famille         | `Dupont`                      |
| **Rôle**         | STUDENT, TEACHER ou ADMIN    | `STUDENT`                     |

#### Étape 3 : Validation du compte

1. Vérifiez que toutes les informations sont correctes
2. Cliquez sur le bouton **"Créer un compte"**
3. Un message de confirmation apparaît
4. Vous êtes automatiquement connecté au système
5. Votre identifiant étudiant (ex: STU000001) est généré automatiquement

**Important :** Conservez bien vos identifiants de connexion en lieu sûr.

### 2.2 Connexion au Système

#### Connexion Standard

1. Accédez à la page d'accueil : http://localhost:3000
2. Cliquez sur **"Se connecter"** ou **"Login"**
3. Saisissez votre **email** et votre **mot de passe**
4. Cliquez sur **"Connexion"**
5. Vous êtes redirigé vers votre tableau de bord

#### Tableau de Bord

Après connexion, vous accédez à votre tableau de bord personnalisé selon votre rôle :

- **Étudiants** : Vue d'ensemble de vos cours, notes et factures
- **Enseignants** : Statistiques sur vos cours et étudiants
- **Administrateurs** : Vue globale du système

### 2.3 Navigation dans l'Interface

#### Menu Latéral (Sidebar)

Le menu latéral vous permet d'accéder rapidement aux différentes sections :

**Pour les Étudiants :**

```
📊 Tableau de bord    - Vue d'ensemble
📚 Mes Cours          - Catalogue et inscriptions
📝 Mes Notes          - Consultation des notes
💰 Facturation        - Factures et paiements
👤 Profil             - Informations personnelles
🚪 Déconnexion        - Sortir du système
```

**Pour les Enseignants :**

```
📊 Tableau de bord    - Vue d'ensemble
📚 Mes Cours          - Gestion des cours
👥 Étudiants          - Liste des étudiants
📝 Notes              - Saisie des notes
👤 Profil             - Informations personnelles
🚪 Déconnexion        - Sortir du système
```

**Pour les Administrateurs :**

```
📊 Tableau de bord    - Vue d'ensemble
👥 Utilisateurs       - Gestion des utilisateurs
📚 Cours              - Gestion des cours
📝 Notes              - Consultation des notes
💰 Facturation        - Gestion financière
👤 Profil             - Informations personnelles
🚪 Déconnexion        - Sortir du système
```

---

## 3. Guide Étudiant

### 3.1 Tableau de Bord Étudiant

Le tableau de bord étudiant affiche un résumé de vos activités académiques :

#### Widgets Disponibles

**📚 Mes Cours Inscrits**

- Nombre total de cours dans lesquels vous êtes inscrit
- Accès rapide à la liste complète de vos cours

**📝 Moyenne Générale**

- Votre moyenne calculée sur tous les cours
- Mise à jour automatique après chaque nouvelle note

**💰 Factures en Attente**

- Nombre de factures non payées
- Montant total à régler
- Lien direct vers la section facturation

**📅 Activités Récentes**

- Dernières inscriptions aux cours
- Nouvelles notes publiées
- Factures récemment générées

### 3.2 Consultation du Catalogue de Cours

#### Accéder au Catalogue

1. Cliquez sur **"Mes Cours"** dans le menu latéral
2. La page affiche deux sections :
   - **Cours Disponibles** : Tous les cours offerts
   - **Mes Inscriptions** : Cours dans lesquels vous êtes inscrit

#### Informations sur un Cours

Chaque carte de cours affiche :

```
┌─────────────────────────────────────┐
│ CS101 - Introduction à la           │
│ Programmation                       │
├─────────────────────────────────────┤
│ 📖 Description:                     │
│ Cours d'introduction à la           │
│ programmation avec Python           │
│                                     │
│ 🎓 Crédits: 3                       │
│ 👨‍🏫 Enseignant: TEA000001           │
│ 👥 Places: 25/30                    │
│                                     │
│ [S'inscrire] ou [Inscrit ✓]        │
└─────────────────────────────────────┘
```

### 3.3 Inscription à un Cours

#### Processus d'Inscription

1. **Parcourir les cours disponibles**

   - Consultez la liste des cours dans la section "Cours Disponibles"
   - Utilisez la barre de recherche pour trouver un cours spécifique

2. **Vérifier les prérequis**

   - Assurez-vous de remplir les conditions requises
   - Vérifiez la disponibilité des places

3. **S'inscrire**

   - Cliquez sur le bouton **"S'inscrire"** sur la carte du cours
   - Une fenêtre de confirmation apparaît
   - Confirmez votre inscription

4. **Confirmation**
   - Un message de succès s'affiche : "Inscription réussie !"
   - Le cours apparaît maintenant dans "Mes Inscriptions"
   - Le bouton devient **"Inscrit ✓"**

#### Annulation d'Inscription

1. Accédez à la section **"Mes Inscriptions"**
2. Trouvez le cours à annuler
3. Cliquez sur le bouton **"Se désinscrire"**
4. Confirmez l'action dans la fenêtre de dialogue
5. Le cours revient dans la liste des cours disponibles

**Note :** Certaines restrictions peuvent s'appliquer selon les règles de l'université.

### 3.4 Consultation des Notes

#### Accéder à vos Notes

1. Cliquez sur **"Mes Notes"** dans le menu latéral
2. La page affiche toutes vos notes par cours

#### Interface de Consultation des Notes

```
┌────────────────────────────────────────────────────────┐
│ 📊 Mes Notes - Moyenne Générale: 15.5/20              │
├────────────────────────────────────────────────────────┤
│                                                        │
│ CS101 - Introduction à la Programmation               │
│ ├─ Examen Mi-session      | 14.5/20 | Coef: 1        │
│ ├─ Projet Final           | 16.0/20 | Coef: 2        │
│ └─ Moyenne du cours: 15.5/20                          │
│                                                        │
│ MATH201 - Mathématiques Avancées                      │
│ ├─ Contrôle Continu       | 13.0/20 | Coef: 1        │
│ ├─ Examen Final           | 15.5/20 | Coef: 2        │
│ └─ Moyenne du cours: 14.7/20                          │
│                                                        │
└────────────────────────────────────────────────────────┘
```

#### Détails d'une Note

Pour chaque note, vous pouvez consulter :

- **Cours** : Code et titre du cours
- **Note** : Note obtenue sur 20
- **Coefficient** : Poids de la note dans la moyenne
- **Date d'examen** : Date de l'évaluation
- **Commentaires** : Remarques de l'enseignant (si disponibles)

#### Calcul de la Moyenne

La moyenne est calculée automatiquement :

```
Moyenne du cours = (Note1 × Coef1 + Note2 × Coef2 + ...) / (Coef1 + Coef2 + ...)
Moyenne générale = Moyenne de toutes les moyennes de cours
```

### 3.5 Gestion des Factures

#### Accéder à vos Factures

1. Cliquez sur **"Facturation"** dans le menu latéral
2. La page affiche toutes vos factures

#### Types de Factures

**Factures de Scolarité**

- Frais d'inscription semestriels
- Frais de cours individuels
- Frais de services complémentaires

#### Statuts de Facture

| Statut           | Description            | Action              |
| ---------------- | ---------------------- | ------------------- |
| 🟡 **PENDING**   | En attente de paiement | Payer maintenant    |
| 🟢 **PAID**      | Payée                  | Télécharger le reçu |
| 🔴 **OVERDUE**   | En retard              | Payer immédiatement |
| ⚫ **CANCELLED** | Annulée                | Aucune action       |

#### Processus de Paiement

1. **Sélectionner une facture**

   - Cliquez sur la facture à payer dans la liste
   - Vérifiez le montant et la description

2. **Initier le paiement**

   - Cliquez sur le bouton **"Payer"**
   - Une fenêtre de paiement s'ouvre

3. **Saisir les informations de paiement**

   - Choisissez le mode de paiement (carte bancaire, virement, etc.)
   - Saisissez les informations nécessaires

4. **Confirmer le paiement**

   - Vérifiez le récapitulatif
   - Confirmez la transaction

5. **Reçu de paiement**
   - Le statut de la facture passe à **PAID**
   - Un reçu est généré automatiquement
   - Vous pouvez télécharger le reçu en PDF

#### Historique des Paiements

```
┌──────────────────────────────────────────────────────┐
│ Facture #001 - Scolarité Semestre 1                 │
│ Montant: 5,000.00 €                                 │
│ Date d'émission: 01/09/2024                         │
│ Date d'échéance: 31/12/2024                         │
│ Statut: ✅ PAYÉE                                    │
│ Date de paiement: 15/09/2024                        │
│ [Télécharger le reçu] [Voir les détails]           │
└──────────────────────────────────────────────────────┘
```

### 3.6 Gestion du Profil

#### Accéder à votre Profil

1. Cliquez sur **"Profil"** dans le menu latéral
2. La page affiche vos informations personnelles

#### Informations Affichées

**Informations Personnelles**

- Nom complet
- Email
- Numéro étudiant (ex: STU000001)
- Rôle (STUDENT)

**Informations de Compte**

- Date de création du compte
- Dernière connexion
- Statut du compte (Actif/Inactif)

#### Modifier vos Informations

1. Cliquez sur le bouton **"Modifier"**
2. Les champs deviennent éditables
3. Modifiez les informations souhaitées :
   - Prénom
   - Nom
   - Email (doit rester unique)
4. Cliquez sur **"Enregistrer"**
5. Un message de confirmation apparaît

**Champs non modifiables :**

- Numéro étudiant
- Rôle
- Date de création

#### Modifier le Mot de Passe

1. Dans la page Profil, cliquez sur **"Changer le mot de passe"**
2. Saisissez votre **mot de passe actuel**
3. Saisissez votre **nouveau mot de passe**
4. Confirmez le **nouveau mot de passe**
5. Cliquez sur **"Mettre à jour"**

**Critères de sécurité du mot de passe :**

- Minimum 8 caractères
- Au moins une lettre majuscule
- Au moins une lettre minuscule
- Au moins un chiffre
- Au moins un caractère spécial (recommandé)

---

## 4. Guide Enseignant

### 4.1 Tableau de Bord Enseignant

Le tableau de bord enseignant affiche vos statistiques d'enseignement :

#### Cartes Statistiques

**📚 Mes Cours**

- Nombre de cours que vous enseignez
- Accès rapide à la gestion des cours

**👥 Total Étudiants**

- Nombre total d'étudiants dans tous vos cours
- Répartition par cours

**📝 Devoirs/Examens**

- Nombre d'évaluations à venir
- Notes en attente de saisie

### 4.2 Gestion des Cours

#### Accéder à vos Cours

1. Cliquez sur **"Mes Cours"** dans le menu latéral
2. La page affiche tous les cours dont vous êtes responsable

#### Affichage des Cours

```
┌─────────────────────────────────────────────────────┐
│ CS101 - Introduction à la Programmation            │
├─────────────────────────────────────────────────────┤
│ 📖 Crédits: 3                                       │
│ 👥 Étudiants inscrits: 25/30                        │
│ 📅 Date de création: 01/09/2024                     │
│                                                     │
│ [Voir les étudiants] [Gérer les notes]             │
│ [Modifier] [Supprimer]                              │
└─────────────────────────────────────────────────────┘
```

#### Créer un Nouveau Cours

1. Cliquez sur le bouton **"+ Nouveau Cours"**
2. Remplissez le formulaire :

| Champ           | Description            | Exemple                           |
| --------------- | ---------------------- | --------------------------------- |
| **Code**        | Code unique du cours   | `CS101`                           |
| **Titre**       | Nom complet du cours   | `Introduction à la Programmation` |
| **Description** | Description détaillée  | `Cours d'introduction...`         |
| **Crédits**     | Nombre de crédits      | `3`                               |
| **Capacité**    | Nombre max d'étudiants | `30`                              |

3. Cliquez sur **"Créer le cours"**
4. Le cours apparaît dans votre liste

#### Modifier un Cours Existant

1. Trouvez le cours dans la liste
2. Cliquez sur **"Modifier"**
3. Modifiez les informations nécessaires
4. Cliquez sur **"Enregistrer"**

**Note :** Vous ne pouvez modifier que vos propres cours.

#### Supprimer un Cours

1. Trouvez le cours dans la liste
2. Cliquez sur **"Supprimer"**
3. Une fenêtre de confirmation apparaît
4. Confirmez la suppression

**Attention :** La suppression d'un cours entraîne :

- Désinscription automatique de tous les étudiants
- Suppression de toutes les notes associées
- Cette action est **irréversible**

### 4.3 Consultation des Étudiants

#### Voir les Étudiants d'un Cours

1. Dans la page **"Mes Cours"**, trouvez le cours
2. Cliquez sur **"Voir les étudiants"**
3. La liste des étudiants inscrits s'affiche

#### Informations sur les Étudiants

```
┌────────────────────────────────────────────────────┐
│ Étudiants inscrits - CS101 (25 étudiants)         │
├────────────────────────────────────────────────────┤
│                                                    │
│ 1. Marie Dupont (STU000001)                       │
│    📧 marie.dupont@university.com                 │
│    📝 Moyenne: 15.5/20                            │
│    📅 Inscrit le: 01/09/2024                      │
│                                                    │
│ 2. Jean Martin (STU000002)                        │
│    📧 jean.martin@university.com                  │
│    📝 Moyenne: 14.0/20                            │
│    📅 Inscrit le: 02/09/2024                      │
│                                                    │
└────────────────────────────────────────────────────┘
```

#### Exporter la Liste des Étudiants

1. Cliquez sur **"Exporter"**
2. Choisissez le format :
   - CSV (pour Excel)
   - PDF (pour impression)
3. Le fichier est téléchargé automatiquement

### 4.4 Saisie des Notes

#### Accéder à la Saisie des Notes

1. Cliquez sur **"Notes"** dans le menu latéral
2. Sélectionnez le cours concerné
3. La liste des étudiants apparaît

#### Ajouter une Note

1. **Sélectionner l'étudiant**

   - Trouvez l'étudiant dans la liste
   - Cliquez sur **"Ajouter une note"**

2. **Remplir le formulaire**

| Champ             | Description           | Exemple            |
| ----------------- | --------------------- | ------------------ |
| **Note**          | Note sur 20           | `15.5`             |
| **Coefficient**   | Poids de la note      | `2`                |
| **Date d'examen** | Date de l'évaluation  | `15/12/2024`       |
| **Type**          | Type d'évaluation     | `Examen Final`     |
| **Commentaires**  | Remarques (optionnel) | `Très bon travail` |

3. **Valider la note**
   - Vérifiez les informations
   - Cliquez sur **"Enregistrer"**
   - La note est immédiatement visible par l'étudiant

#### Modifier une Note

1. Trouvez la note dans la liste
2. Cliquez sur l'icône **"Modifier" (✏️)**
3. Modifiez les informations
4. Cliquez sur **"Enregistrer"**

**Important :** Toutes les modifications sont tracées dans l'historique.

#### Saisie Groupée de Notes

Pour saisir plusieurs notes simultanément :

1. Cliquez sur **"Saisie groupée"**
2. Un tableau Excel-like s'affiche
3. Saisissez les notes directement dans le tableau
4. Cliquez sur **"Enregistrer tout"**

```
┌───────────────┬────────┬──────┬──────────────┐
│ Étudiant      │ Note   │ Coef │ Date         │
├───────────────┼────────┼──────┼──────────────┤
│ STU000001     │ 15.5   │ 2    │ 15/12/2024   │
│ STU000002     │ 14.0   │ 2    │ 15/12/2024   │
│ STU000003     │ 16.5   │ 2    │ 15/12/2024   │
└───────────────┴────────┴──────┴──────────────┘
```

#### Statistiques du Cours

Après la saisie des notes, consultez les statistiques :

- **Moyenne de classe** : 15.0/20
- **Note la plus haute** : 18.5/20
- **Note la plus basse** : 10.0/20
- **Taux de réussite** : 95% (note ≥ 10)
- **Distribution des notes** : Graphique en barres

### 4.5 Communication avec les Étudiants

#### Envoyer un Message

1. Accédez à la liste des étudiants d'un cours
2. Sélectionnez un ou plusieurs étudiants
3. Cliquez sur **"Envoyer un message"**
4. Rédigez votre message
5. Cliquez sur **"Envoyer"**

**Types de messages :**

- Annonces générales
- Rappels d'échéances
- Informations sur les évaluations
- Réponses aux questions

---

## 5. Guide Administrateur

### 5.1 Tableau de Bord Administrateur

Le tableau de bord administrateur offre une vue globale du système :

#### Statistiques Générales

**👥 Utilisateurs**

- Total des utilisateurs : 250
- Étudiants : 200
- Enseignants : 45
- Administrateurs : 5

**📚 Cours**

- Total des cours : 50
- Cours actifs : 45
- Taux d'occupation moyen : 85%

**📝 Notes**

- Total des notes saisies : 5,000
- Moyenne générale : 14.5/20

**💰 Facturation**

- Factures émises : 200
- Factures payées : 180
- Montant total collecté : 1,000,000 €

### 5.2 Gestion des Utilisateurs

#### Accéder à la Gestion des Utilisateurs

1. Cliquez sur **"Utilisateurs"** dans le menu latéral
2. La liste de tous les utilisateurs s'affiche

#### Liste des Utilisateurs

```
┌────────────────────────────────────────────────────────┐
│ Gestion des Utilisateurs (250 utilisateurs)           │
├────────────────────────────────────────────────────────┤
│ 🔍 Rechercher: [_____________] [Filtrer ▼]            │
│                                                        │
│ ┌──────────────────────────────────────────────────┐  │
│ │ Marie Dupont (STU000001)                         │  │
│ │ 📧 marie.dupont@university.com                   │  │
│ │ 🎓 Rôle: STUDENT                                 │  │
│ │ 📅 Créé le: 01/09/2024                          │  │
│ │ ✅ Actif                                         │  │
│ │ [Modifier] [Désactiver] [Supprimer]             │  │
│ └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

#### Créer un Nouvel Utilisateur

1. Cliquez sur **"+ Nouvel utilisateur"**
2. Remplissez le formulaire :
   - Email
   - Mot de passe temporaire
   - Prénom et Nom
   - Rôle (STUDENT, TEACHER, ADMIN)
3. Cliquez sur **"Créer"**
4. Un email de bienvenue est envoyé automatiquement

#### Modifier un Utilisateur

1. Trouvez l'utilisateur dans la liste
2. Cliquez sur **"Modifier"**
3. Modifiez les informations :
   - Informations personnelles
   - Rôle
   - Statut (Actif/Inactif)
4. Cliquez sur **"Enregistrer"**

#### Désactiver/Réactiver un Utilisateur

**Désactiver :**

1. Cliquez sur **"Désactiver"**
2. L'utilisateur ne peut plus se connecter
3. Les données sont conservées

**Réactiver :**

1. Cliquez sur **"Réactiver"**
2. L'utilisateur peut à nouveau se connecter

#### Supprimer un Utilisateur

1. Cliquez sur **"Supprimer"**
2. Une confirmation est demandée
3. Confirmez la suppression

**Attention :** La suppression est **irréversible** et entraîne :

- Suppression du compte
- Désinscription de tous les cours
- Suppression de toutes les notes
- Suppression de toutes les factures

### 5.3 Gestion des Cours

#### Vue d'Ensemble des Cours

1. Cliquez sur **"Cours"** dans le menu latéral
2. Tous les cours du système s'affichent

#### Actions Administrateur

**Créer un cours :**

- Identique au processus enseignant
- Possibilité d'assigner n'importe quel enseignant

**Modifier un cours :**

- Modification de tous les cours (pas seulement les siens)
- Changement d'enseignant responsable

**Supprimer un cours :**

- Suppression de n'importe quel cours
- Notification automatique aux étudiants inscrits

**Gérer les inscriptions :**

- Inscrire/désinscrire manuellement des étudiants
- Augmenter la capacité d'un cours
- Fermer les inscriptions

### 5.4 Supervision de la Facturation

#### Tableau de Bord Facturation

```
┌────────────────────────────────────────────────────┐
│ Vue d'Ensemble Financière                         │
├────────────────────────────────────────────────────┤
│ 💰 Revenus Totaux: 1,000,000 €                    │
│ ✅ Factures Payées: 180 (90%)                     │
│ 🟡 Factures En Attente: 15 (7.5%)                │
│ 🔴 Factures En Retard: 5 (2.5%)                  │
└────────────────────────────────────────────────────┘
```

#### Générer des Factures en Masse

1. Cliquez sur **"Générer factures"**
2. Sélectionnez les critères :
   - Période (Semestre 1, Semestre 2, Année complète)
   - Type de frais (Scolarité, Cours spécifiques, etc.)
   - Étudiants concernés (Tous, par année, etc.)
3. Prévisualisez les factures
4. Confirmez la génération
5. Les factures sont créées et envoyées par email

#### Gérer les Paiements

**Enregistrer un paiement manuel :**

1. Trouvez la facture
2. Cliquez sur **"Enregistrer paiement"**
3. Saisissez :
   - Montant payé
   - Mode de paiement
   - Référence de transaction
   - Date de paiement
4. Cliquez sur **"Valider"**

**Annuler une facture :**

1. Sélectionnez la facture
2. Cliquez sur **"Annuler"**
3. Saisissez la raison
4. Confirmez

**Relancer un étudiant :**

1. Trouvez la facture en retard
2. Cliquez sur **"Envoyer relance"**
3. Un email de rappel est envoyé

### 5.5 Rapports et Statistiques

#### Générer un Rapport

1. Cliquez sur **"Rapports"** dans le menu
2. Choisissez le type de rapport :

   - Statistiques d'inscription
   - Résultats académiques
   - Rapport financier
   - Rapport d'activité

3. Sélectionnez la période
4. Cliquez sur **"Générer"**
5. Le rapport s'affiche à l'écran
6. Options : Exporter en PDF ou Excel

#### Types de Rapports Disponibles

**Rapport d'Inscriptions :**

- Évolution des inscriptions par semestre
- Taux de remplissage des cours
- Cours les plus populaires

**Rapport Académique :**

- Moyennes par cours
- Taux de réussite
- Distribution des notes
- Comparaisons inter-semestres

**Rapport Financier :**

- Revenus par période
- Taux de recouvrement
- Factures en retard
- Prévisions

---

## 6. Fonctionnalités Communes

### 6.1 Recherche et Filtrage

#### Barre de Recherche

Disponible sur la plupart des pages listant des données :

```
🔍 Rechercher: [_______________] [🔍]
```

**Champs de recherche selon le contexte :**

- **Étudiants** : Nom, prénom, email, numéro étudiant
- **Cours** : Code, titre, enseignant
- **Factures** : Numéro, montant, statut

#### Filtres Avancés

Cliquez sur **"Filtres ▼"** pour accéder aux options :

**Filtres disponibles :**

- Par date (Date de création, Date de modification)
- Par statut (Actif, Inactif, En attente, etc.)
- Par rôle (pour les utilisateurs)
- Par type (pour les factures)

**Exemple d'utilisation :**

```
Filtrer par:
☑ Statut: En attente
☑ Date: Dernier mois
☐ Montant: > 1000€
[Appliquer] [Réinitialiser]
```

### 6.2 Tri des Résultats

Cliquez sur les en-têtes de colonnes pour trier :

```
┌─────────────┬──────────────┬──────────────┐
│ Nom ▲       │ Email        │ Date ▼       │
├─────────────┼──────────────┼──────────────┤
│ ...         │ ...          │ ...          │
└─────────────┴──────────────┴──────────────┘
```

- **▲** : Tri croissant (A-Z, 0-9, ancien→récent)
- **▼** : Tri décroissant (Z-A, 9-0, récent→ancien)

### 6.3 Pagination

Pour les listes longues, utilisez la pagination :

```
← Précédent | 1 [2] 3 4 5 | Suivant →
Affichage: 10 par page ▼
```

**Options d'affichage :**

- 10 éléments par page
- 25 éléments par page
- 50 éléments par page
- 100 éléments par page

### 6.4 Notifications

#### Types de Notifications

**🔔 Notifications en temps réel :**

- Nouvelle note publiée
- Facture générée
- Message reçu
- Inscription confirmée
- Échéance approchante

#### Accéder aux Notifications

1. Cliquez sur l'icône 🔔 dans le header
2. La liste des notifications s'affiche
3. Cliquez sur une notification pour la consulter
4. Les notifications non lues apparaissent en gras

```
┌────────────────────────────────────────┐
│ Notifications (3 non lues)            │
├────────────────────────────────────────┤
│ • Nouvelle note en CS101              │
│   Il y a 5 minutes                    │
│                                        │
│ • Facture #025 générée                │
│   Il y a 1 heure                      │
│                                        │
│ ○ Inscription confirmée - MATH201     │
│   Il y a 2 jours                      │
└────────────────────────────────────────┘
```

### 6.5 Paramètres de Langue

1. Cliquez sur l'icône de langue (🌐) dans le header
2. Sélectionnez votre langue préférée :
   - Français
   - English
   - Español
   - العربية
3. L'interface se met à jour automatiquement

### 6.6 Mode Sombre/Clair

1. Cliquez sur l'icône thème (🌙/☀️) dans le header
2. Le thème bascule automatiquement
3. Votre préférence est sauvegardée

---

## 7. FAQ et Dépannage

### 7.1 Questions Fréquentes

#### Compte et Connexion

**Q : J'ai oublié mon mot de passe, que faire ?**
R : Cliquez sur "Mot de passe oublié ?" sur la page de connexion. Saisissez votre email et suivez les instructions reçues par email pour réinitialiser votre mot de passe.

**Q : Je n'arrive pas à me connecter**
R : Vérifiez que :

- Votre email est correctement saisi
- Votre mot de passe est correct (attention à la casse)
- Votre compte est activé (contactez l'administration si nécessaire)
- Votre navigateur accepte les cookies

**Q : Comment changer mon adresse email ?**
R : Accédez à votre profil, cliquez sur "Modifier", changez votre email et enregistrez. Vous recevrez un email de confirmation sur la nouvelle adresse.

#### Inscriptions aux Cours

**Q : Je ne peux pas m'inscrire à un cours, pourquoi ?**
R : Plusieurs raisons possibles :

- Le cours est complet (capacité atteinte)
- Vous ne remplissez pas les prérequis
- La période d'inscription est fermée
- Vous êtes déjà inscrit à ce cours

**Q : Comment annuler mon inscription à un cours ?**
R : Accédez à "Mes Cours", trouvez le cours dans "Mes Inscriptions" et cliquez sur "Se désinscrire". Note : des restrictions peuvent s'appliquer selon les règles de l'université.

**Q : Puis-je m'inscrire à un nombre illimité de cours ?**
R : Non, selon les règles de l'université, il peut y avoir un nombre maximum de crédits par semestre (généralement 30 crédits).

#### Notes et Évaluations

**Q : Quand puis-je consulter mes notes ?**
R : Les notes sont visibles dès qu'elles sont publiées par l'enseignant. Vous recevez une notification lors de la publication.

**Q : Ma note semble incorrecte, que faire ?**
R : Contactez directement votre enseignant via le système de messagerie ou en personne. Les enseignants peuvent corriger les notes en cas d'erreur.

**Q : Comment est calculée ma moyenne ?**
R : Chaque note a un coefficient. La moyenne du cours = (Note1 × Coef1 + Note2 × Coef2 + ...) / Somme des coefficients.

#### Facturation et Paiements

**Q : Quand les factures sont-elles générées ?**
R : Les factures de scolarité sont générées automatiquement au début de chaque semestre. D'autres factures peuvent être générées selon les services utilisés.

**Q : Quels modes de paiement sont acceptés ?**
R :

- Carte bancaire (Visa, MasterCard)
- Virement bancaire
- Chèque (selon les règles de l'université)
- Paiement en espèces (à l'administration)

**Q : Que se passe-t-il si je ne paie pas dans les délais ?**
R :

- Le statut de la facture passe à "OVERDUE"
- Des pénalités de retard peuvent s'appliquer
- Votre inscription peut être suspendue
- Contactez l'administration pour un échéancier de paiement

**Q : Puis-je obtenir un reçu de paiement ?**
R : Oui, après chaque paiement, un reçu est généré automatiquement. Vous pouvez le télécharger en PDF depuis la section Facturation.

### 7.2 Problèmes Techniques

#### Problèmes d'Affichage

**Problème : La page ne s'affiche pas correctement**
Solutions :

1. Rafraîchissez la page (F5 ou Ctrl+R)
2. Videz le cache du navigateur (Ctrl+Shift+Delete)
3. Essayez un autre navigateur
4. Vérifiez votre connexion Internet
5. Désactivez temporairement les extensions de navigateur

**Problème : Les images ne se chargent pas**
Solutions :

1. Vérifiez votre connexion Internet
2. Désactivez les bloqueurs de publicités
3. Rafraîchissez la page

#### Problèmes de Performance

**Problème : Le système est lent**
Solutions :

1. Vérifiez votre connexion Internet (vitesse minimale : 1 Mbps)
2. Fermez les onglets inutiles
3. Redémarrez votre navigateur
4. Essayez à un moment moins chargé (évitez les heures de pointe)

**Problème : Le téléchargement de fichiers échoue**
Solutions :

1. Vérifiez l'espace disque disponible
2. Désactivez temporairement l'antivirus
3. Essayez avec un autre navigateur
4. Contactez le support technique

#### Erreurs Courantes

**Erreur 401 - Non autorisé**

- Votre session a expiré
- Reconnectez-vous au système

**Erreur 404 - Page non trouvée**

- Le lien est incorrect ou obsolète
- Retournez à la page d'accueil et naviguez normalement

**Erreur 500 - Erreur serveur**

- Problème temporaire du serveur
- Attendez quelques minutes et réessayez
- Si le problème persiste, contactez le support

**Erreur 503 - Service indisponible**

- Le système est en maintenance
- Consultez les annonces pour connaître la durée
- Réessayez plus tard

### 7.3 Bonnes Pratiques

#### Sécurité

✅ **À FAIRE :**

- Utilisez un mot de passe fort et unique
- Ne partagez jamais vos identifiants
- Déconnectez-vous après chaque session (surtout sur ordinateur public)
- Vérifiez l'URL avant de saisir vos identifiants
- Changez régulièrement votre mot de passe

❌ **À ÉVITER :**

- Utiliser le même mot de passe que d'autres sites
- Enregistrer votre mot de passe sur un ordinateur public
- Laisser votre session ouverte sans surveillance
- Partager votre compte avec d'autres personnes

#### Navigation

✅ **Conseils :**

- Utilisez les raccourcis du menu latéral
- Utilisez la fonction de recherche pour trouver rapidement
- Marquez vos pages fréquentes en favoris
- Consultez régulièrement vos notifications
- Gardez vos informations de profil à jour

#### Performances

✅ **Optimisation :**

- Utilisez un navigateur moderne et à jour
- Activez le cache du navigateur
- Fermez les onglets inutiles
- Utilisez une connexion Internet stable
- Évitez les heures de forte affluence pour les opérations lourdes

### 7.4 Support et Contact

#### Obtenir de l'Aide

**Support Technique :**

- 📧 Email : support@university.com
- 📞 Téléphone : +33 1 23 45 67 89
- 💬 Chat en ligne : Disponible sur le site (9h-18h)
- 🎫 Système de tickets : Via la page "Support"

**Heures d'ouverture :**

- Lundi - Vendredi : 9h00 - 18h00
- Samedi : 9h00 - 12h00
- Dimanche et jours fériés : Fermé

**Délai de réponse :**

- Chat en ligne : Immédiat (pendant les heures d'ouverture)
- Email : Sous 24h ouvrées
- Téléphone : Immédiat (pendant les heures d'ouverture)
- Ticket : Sous 48h ouvrées

#### Signaler un Bug

1. Accédez à la page "Support"
2. Cliquez sur "Signaler un bug"
3. Remplissez le formulaire :
   - Description détaillée du problème
   - Étapes pour reproduire
   - Captures d'écran (si possible)
   - Navigateur et version
4. Soumettez le rapport
5. Vous recevrez un numéro de ticket

#### Demander une Nouvelle Fonctionnalité

1. Accédez à la page "Support"
2. Cliquez sur "Suggérer une fonctionnalité"
3. Décrivez votre suggestion
4. Soumettez la demande

Les suggestions sont étudiées et priorisées par l'équipe de développement.

---

## 8. Mises à Jour et Nouveautés

### Version 1.0.0 (Décembre 2024)

**Nouvelles fonctionnalités :**

- ✅ Système complet de gestion des cours
- ✅ Module de notation
- ✅ Système de facturation
- ✅ Tableau de bord personnalisé par rôle
- ✅ Interface responsive (mobile-friendly)

**Améliorations :**

- Performance accrue (temps de chargement divisé par 2)
- Interface utilisateur modernisée
- Meilleure accessibilité

**Corrections :**

- Correction de bugs mineurs d'affichage
- Amélioration de la stabilité

---

## 9. Glossaire

**API** : Interface de programmation permettant la communication entre services

**Cache** : Mémoire temporaire stockant les données fréquemment utilisées

**Coefficient** : Poids d'une note dans le calcul de la moyenne

**CORS** : Mécanisme de sécurité pour les requêtes HTTP cross-origin

**Crédit** : Unité de mesure de la charge de travail d'un cours

**Dashboard** : Tableau de bord affichant un résumé des informations

**JWT** : Token d'authentification sécurisé

**Pagination** : Division d'une liste en plusieurs pages

**Session** : Période pendant laquelle un utilisateur est connecté

**Statut** : État actuel d'une entité (facture, cours, utilisateur, etc.)

**Token** : Jeton d'authentification permettant l'accès sécurisé

**UI/UX** : Interface utilisateur / Expérience utilisateur

---

## 10. Annexes

### Annexe A : Raccourcis Clavier

| Raccourci  | Action                             |
| ---------- | ---------------------------------- |
| `Alt + D`  | Aller au tableau de bord           |
| `Alt + C`  | Aller aux cours                    |
| `Alt + G`  | Aller aux notes                    |
| `Alt + P`  | Aller au profil                    |
| `Ctrl + S` | Enregistrer (dans les formulaires) |
| `Échap`    | Fermer la fenêtre modale           |
| `Ctrl + F` | Rechercher dans la page            |

### Annexe B : Formats de Données

**Format de date :** JJ/MM/AAAA (ex: 15/12/2024)

**Format de note :** X.XX/20 (ex: 15.5/20)

**Format de montant :** X,XXX.XX € (ex: 5,000.00 €)

**Format d'email :** nom@domaine.extension

**Format de numéro étudiant :** STU000XXX (ex: STU000001)

**Format de code cours :** XXXX### (ex: CS101, MATH201)

---

**Version du Manuel :** 1.0  
**Date de Publication :** 16 Décembre 2024  
**Dernière Mise à Jour :** 16 Décembre 2024

**© 2024 Université - Système de Gestion Universitaire**  
**Tous droits réservés**
