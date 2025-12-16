# 📋 Cahier des Charges - Système de Gestion Universitaire

## 1. Présentation du Projet

### 1.1 Contexte

Ce projet s'inscrit dans le cadre du cours **Architecture SOA et Services Web** en 3ème année Licence Génie Logiciel et Système d'Information. Il vise à concevoir et développer un système de gestion universitaire basé sur une architecture orientée services (SOA) utilisant plusieurs technologies et protocoles de communication.

### 1.2 Objectifs Généraux

- Concevoir une architecture microservices polyglotte
- Implémenter des services web REST et SOAP
- Mettre en place une passerelle API (API Gateway)
- Assurer la communication inter-services
- Gérer l'authentification et l'autorisation de manière centralisée
- Déployer l'ensemble du système via Docker

### 1.3 Portée du Système

Le système permet la gestion complète des opérations universitaires incluant :

- La gestion des étudiants
- La gestion des cours et des inscriptions
- La gestion des notes et bulletins
- La facturation des frais universitaires
- L'authentification et l'autorisation des utilisateurs

---

## 2. Architecture Technique

### 2.1 Architecture Globale

L'architecture adopte le pattern **Microservices** avec les composants suivants :

```
┌─────────────────────────────────────────────────────────┐
│              Interface Utilisateur (Frontend)           │
│                 React + TypeScript                      │
│                    Port: 3000                           │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              API Gateway (Spring Cloud)                 │
│                    Port: 8080                           │
│  - Routage centralisé                                   │
│  - Validation JWT                                       │
│  - Load balancing                                       │
└─────┬──────┬──────┬──────┬──────────────────────────────┘
      │      │      │      │
      ▼      ▼      ▼      ▼
   ┌─────┬─────┬─────┬─────┬──────────┐
   │Auth │Stud │Cours│Grade│ Billing  │
   │8081 │8082 │8083 │8084 │  5000    │
   └─────┴─────┴─────┴─────┴──────────┘
      │      │      │      │      │
      └──────┴──────┴──────┴──────┘
                    │
           ┌────────▼────────┐
           │  Bases de       │
           │  Données        │
           └─────────────────┘
```

### 2.2 Technologies par Service

| Service            | Technologie          | Protocole   | Base de Données | Port |
| ------------------ | -------------------- | ----------- | --------------- | ---- |
| **Authentication** | Spring Boot (Java)   | REST        | PostgreSQL      | 8081 |
| **Students**       | Node.js + Express    | REST        | PostgreSQL      | 8082 |
| **Courses**        | Spring Boot (Java)   | SOAP + REST | PostgreSQL      | 8083 |
| **Grades**         | Python + FastAPI     | REST        | PostgreSQL      | 8084 |
| **Billing**        | .NET Core (C#)       | SOAP + REST | PostgreSQL      | 5000 |
| **API Gateway**    | Spring Cloud Gateway | HTTP/REST   | Redis (cache)   | 8080 |
| **Frontend**       | React + TypeScript   | -           | -               | 3000 |

### 2.3 Protocoles de Communication

- **REST** : Communication principale entre services
- **SOAP** : Services Course et Billing (démonstration de polyvalence)
- **JWT** : Authentification et autorisation
- **HTTP/HTTPS** : Protocole de transport

---

## 3. Spécifications Fonctionnelles

### 3.1 Service d'Authentification

#### 3.1.1 Fonctionnalités

- Inscription de nouveaux utilisateurs (étudiants, enseignants, administrateurs)
- Connexion avec email et mot de passe
- Génération de tokens JWT
- Validation des tokens
- Gestion des rôles (STUDENT, TEACHER, ADMIN)

#### 3.1.2 Endpoints Principaux

```
POST   /api/v1/auth/register    - Inscription
POST   /api/v1/auth/login       - Connexion
POST   /api/v1/auth/validate    - Validation du token
GET    /api/v1/auth/me          - Profil utilisateur
```

#### 3.1.3 Règles Métier

- Email unique obligatoire
- Mot de passe minimum 8 caractères
- Token JWT valide pendant 24 heures
- Hash des mots de passe avec BCrypt

### 3.2 Service Étudiants

#### 3.2.1 Fonctionnalités

- CRUD complet des dossiers étudiants
- Recherche et filtrage des étudiants
- Pagination des résultats
- Validation des données (email unique, numéro étudiant unique)

#### 3.2.2 Endpoints Principaux

```
POST   /api/v1/students         - Créer un étudiant
GET    /api/v1/students         - Liste paginée
GET    /api/v1/students/:id     - Détails d'un étudiant
PUT    /api/v1/students/:id     - Mettre à jour
DELETE /api/v1/students/:id     - Supprimer
```

#### 3.2.3 Règles Métier

- Numéro étudiant au format STU000001
- Email unique dans le système
- Nom et prénom obligatoires
- Date de naissance valide

### 3.3 Service Cours

#### 3.3.1 Fonctionnalités

- Gestion du catalogue de cours
- Inscription/désinscription des étudiants
- Consultation des cours par enseignant
- Gestion des prérequis
- Support SOAP et REST

#### 3.3.2 Endpoints Principaux

**REST:**

```
POST   /api/v1/courses                    - Créer un cours
GET    /api/v1/courses                    - Liste des cours
GET    /api/v1/courses/:id                - Détails d'un cours
POST   /api/v1/courses/:id/enroll         - Inscrire un étudiant
DELETE /api/v1/courses/:id/enroll/:sid    - Désinscrire
GET    /api/v1/student/:sid/enrollments   - Cours d'un étudiant
```

**SOAP:**

```
getCourseDetails     - Détails d'un cours
getAllCourses        - Liste tous les cours
createCourse         - Créer un cours
updateCourse         - Mettre à jour
deleteCourse         - Supprimer
```

#### 3.3.3 Règles Métier

- Code cours unique (ex: CS101, MATH201)
- Capacité maximale par cours
- Enseignant assigné obligatoire
- Gestion des places disponibles

### 3.4 Service Notes

#### 3.4.1 Fonctionnalités

- Saisie et modification des notes
- Calcul automatique des moyennes
- Génération de bulletins
- Statistiques par cours et étudiant
- Historique des notes

#### 3.4.2 Endpoints Principaux

```
POST   /api/v1/grades              - Ajouter une note
GET    /api/v1/grades/student/:id  - Notes d'un étudiant
GET    /api/v1/grades/course/:id   - Notes d'un cours
PUT    /api/v1/grades/:id          - Modifier une note
GET    /api/v1/grades/transcript   - Bulletin complet
```

#### 3.4.3 Règles Métier

- Notes entre 0 et 20
- Moyenne pondérée selon les coefficients
- Validation enseignant obligatoire
- Historique des modifications

### 3.5 Service Facturation

#### 3.5.1 Fonctionnalités

- Génération des factures de scolarité
- Suivi des paiements
- Gestion des échéances
- Historique des transactions
- Support SOAP et REST

#### 3.5.2 Endpoints Principaux

**REST:**

```
GET    /api/v1/invoices/student/:id  - Factures d'un étudiant
POST   /api/v1/invoices              - Créer une facture
PUT    /api/v1/invoices/:id/pay      - Enregistrer un paiement
GET    /api/v1/invoices/:id          - Détails d'une facture
```

**SOAP:**

```
getInvoicesByStudent   - Factures d'un étudiant
createInvoice          - Créer une facture
processPayment         - Traiter un paiement
getInvoiceDetails      - Détails d'une facture
```

#### 3.5.3 Règles Métier

- Facture générée automatiquement à l'inscription
- Statuts: PENDING, PAID, OVERDUE, CANCELLED
- Calcul des pénalités de retard
- Traçabilité complète des paiements

### 3.6 API Gateway

#### 3.6.1 Fonctionnalités

- Routage centralisé vers les microservices
- Validation JWT pour les routes protégées
- Load balancing
- Rate limiting (limitation du débit)
- Logging centralisé
- Gestion CORS

#### 3.6.2 Routes Configurées

```
/api/v1/auth/**      → auth-service:8081
/api/v1/students/**  → student-service:8082
/api/v1/courses/**   → courses-service:8083
/api/v1/grades/**    → grades-service:8084
/api/v1/invoices/**  → billing-service:5000
```

#### 3.6.3 Sécurité

- Routes publiques: /auth/login, /auth/register
- Routes protégées: toutes les autres (JWT requis)
- Validation du rôle selon l'endpoint
- Timeout de 30 secondes par requête

### 3.7 Interface Utilisateur (Frontend)

#### 3.7.1 Fonctionnalités

- Tableau de bord selon le rôle (étudiant, enseignant, admin)
- Gestion du profil utilisateur
- Consultation des cours disponibles
- Inscription aux cours
- Consultation des notes et bulletins
- Consultation des factures
- Interface responsive (mobile-friendly)

#### 3.7.2 Pages Principales

```
/                    - Page d'accueil
/login               - Connexion
/register            - Inscription
/dashboard           - Tableau de bord
/profile             - Profil utilisateur
/courses             - Catalogue de cours
/enrollments         - Mes inscriptions
/grades              - Mes notes
/billing             - Mes factures
/teacher             - Gestion des cours (enseignants)
/admin               - Administration (admin)
```

---

## 4. Spécifications Non-Fonctionnelles

### 4.1 Performance

- Temps de réponse API < 500ms pour 95% des requêtes
- Support de 100 utilisateurs concurrent minimum
- Mise en cache Redis pour les données fréquentes
- Pagination systématique (limite: 50 éléments/page)

### 4.2 Sécurité

- Authentification JWT obligatoire
- Hash des mots de passe (BCrypt, coût 12)
- HTTPS en production
- Protection CSRF
- Validation des entrées utilisateur
- Logs d'audit pour les opérations sensibles

### 4.3 Disponibilité

- Taux de disponibilité cible: 99%
- Health checks pour chaque service
- Restart automatique des containers (Docker)
- Gestion gracieuse des erreurs

### 4.4 Scalabilité

- Architecture stateless pour scale horizontal
- Indépendance des services
- Base de données par service (Database per Service pattern)
- Load balancing via API Gateway

### 4.5 Maintenabilité

- Code source documenté (commentaires, README)
- Logs structurés (JSON)
- Tests unitaires et d'intégration
- Documentation API (Swagger/OpenAPI)
- Convention de nommage cohérente

### 4.6 Portabilité

- Déploiement Docker/Docker Compose
- Variables d'environnement pour la configuration
- Support multi-plateforme (Windows, Linux, macOS)
- Base de données PostgreSQL (standard)

---

## 5. Contraintes Techniques

### 5.1 Technologies Imposées

- ✅ Minimum 3 technologies backend différentes
- ✅ REST et SOAP obligatoires
- ✅ API Gateway requis
- ✅ Containerisation Docker
- ✅ Base de données relationnelle

### 5.2 Standards et Normes

- REST: Respect des principes RESTful (GET, POST, PUT, DELETE)
- SOAP: Format WSDL valide
- JSON: Format d'échange de données
- HTTP Status Codes: Usage standard (200, 201, 400, 401, 404, 500)
- JWT: Format standard (Header.Payload.Signature)

### 5.3 Environnement de Développement

- Git pour le versioning
- Docker Desktop 4.0+
- Node.js 18+
- Java 17+
- Python 3.10+
- .NET 7.0+

---

## 6. Modèle de Données

### 6.1 Entités Principales

#### User (Service Auth)

```sql
users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(20) CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN')),
  student_id VARCHAR(20) UNIQUE,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
)
```

#### Student (Service Students)

```sql
students (
  id SERIAL PRIMARY KEY,
  numero_etudiant VARCHAR(20) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  date_naissance DATE,
  adresse TEXT,
  telephone VARCHAR(20),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
)
```

#### Course (Service Courses)

```sql
courses (
  id SERIAL PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL,
  titre VARCHAR(200) NOT NULL,
  description TEXT,
  credits INTEGER DEFAULT 3,
  teacher_id VARCHAR(20),
  capacity INTEGER DEFAULT 30,
  enrolled_count INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
)

student_courses (
  id SERIAL PRIMARY KEY,
  student_id INTEGER REFERENCES students(id),
  course_id INTEGER REFERENCES courses(id),
  enrolled_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(student_id, course_id)
)
```

#### Grade (Service Grades)

```sql
grades (
  id SERIAL PRIMARY KEY,
  student_id VARCHAR(20) NOT NULL,
  course_code VARCHAR(20) NOT NULL,
  grade DECIMAL(5,2) CHECK (grade >= 0 AND grade <= 20),
  coefficient INTEGER DEFAULT 1,
  exam_date DATE,
  comments TEXT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
)
```

#### Invoice (Service Billing)

```sql
invoices (
  id SERIAL PRIMARY KEY,
  student_id VARCHAR(20) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  description TEXT,
  status VARCHAR(20) CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED')),
  due_date DATE,
  paid_date DATE,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP
)
```

### 6.2 Relations

- Un **utilisateur** peut être étudiant, enseignant ou administrateur
- Un **étudiant** peut s'inscrire à plusieurs **cours**
- Un **cours** peut avoir plusieurs **étudiants** inscrits
- Un **étudiant** a plusieurs **notes** pour différents **cours**
- Un **étudiant** a plusieurs **factures**
- Un **enseignant** gère plusieurs **cours**

---

## 7. Déploiement

### 7.1 Architecture Docker

```yaml
Services Docker:
├── student_db (PostgreSQL:5432)
├── auth_db (PostgreSQL:5433)
├── courses_db (PostgreSQL:5434)
├── grades_db (PostgreSQL:5435)
├── billing_db (PostgreSQL:5436)
├── redis (Redis:6379)
├── student_service (Node.js:8082)
├── auth_service (Spring Boot:8081)
├── courses_service (Spring Boot:8083)
├── grades_service (FastAPI:8084)
├── billing_service (.NET:5000)
├── api_gateway (Spring Cloud:8080)
└── frontend (React:3000)
```

### 7.2 Commandes de Déploiement

```bash
# Démarrer tous les services
docker compose up -d

# Arrêter tous les services
docker compose down

# Reconstruire et redémarrer
docker compose up -d --build

# Voir les logs
docker compose logs -f [service_name]

# Vérifier le statut
docker compose ps
```

### 7.3 Volumes Persistants

Les données sont persistées via Docker volumes:

- `student_db_data`
- `auth_db_data`
- `courses_db_data`
- `grades_db_data`
- `billing_db_data`

---

## 8. Tests et Validation

### 8.1 Tests Unitaires

- Couverture minimum: 70%
- Framework: JUnit (Java), Jest (Node.js), Pytest (Python), xUnit (.NET)

### 8.2 Tests d'Intégration

- Tests de bout en bout (E2E)
- Tests de communication inter-services
- Tests de l'API Gateway

### 8.3 Tests de Performance

- Tests de charge (Apache JMeter)
- Tests de stress
- Temps de réponse moyen < 500ms

### 8.4 Validation Fonctionnelle

- Scénarios utilisateur complets
- Tests manuels via Postman/Insomnia
- Validation de l'interface utilisateur

---

## 9. Documentation Livrables

### 9.1 Documentation Technique

- ✅ README.md principal
- ✅ README.md par service
- ✅ Guide de démarrage rapide (QUICK_START.md)
- ✅ Guide de déploiement
- ✅ Documentation API (Swagger)
- ✅ Architecture diagrams

### 9.2 Documentation Utilisateur

- Guide d'utilisation de l'interface
- Guide d'administration
- FAQ

### 9.3 Code Source

- Code commenté et formaté
- Conventions de nommage respectées
- Structure de projet claire
- Fichiers .env.example fournis

---

## 10. Planning et Jalons

### Phase 1: Architecture et Setup (Semaine 1-2)

- ✅ Conception de l'architecture SOA
- ✅ Configuration Docker Compose
- ✅ Setup des bases de données
- ✅ Structure des projets

### Phase 2: Développement Services Core (Semaine 3-4)

- ✅ Service d'Authentification
- ✅ Service Étudiants
- ✅ Service Cours
- ✅ Tests unitaires

### Phase 3: Services Complémentaires (Semaine 5)

- ✅ Service Notes
- ✅ Service Facturation
- ✅ Intégration SOAP

### Phase 4: API Gateway et Frontend (Semaine 6)

- ✅ Configuration Spring Cloud Gateway
- ✅ Développement Frontend React
- ✅ Intégration complète

### Phase 5: Tests et Documentation (Semaine 7)

- ✅ Tests d'intégration
- ✅ Tests de performance
- ✅ Documentation complète
- ✅ Préparation de la démonstration

---

## 11. Critères de Succès

### 11.1 Critères Techniques

- ✅ 5 microservices fonctionnels et indépendants
- ✅ Communication REST et SOAP opérationnelle
- ✅ API Gateway avec authentification JWT
- ✅ Déploiement Docker complet
- ✅ Base de données persistantes
- ✅ Interface utilisateur fonctionnelle

### 11.2 Critères Fonctionnels

- ✅ Inscription et connexion des utilisateurs
- ✅ Gestion complète des étudiants
- ✅ Inscription aux cours
- ✅ Saisie et consultation des notes
- ✅ Génération et paiement des factures
- ✅ Tableaux de bord par rôle

### 11.3 Critères Qualité

- ✅ Code propre et maintenable
- ✅ Documentation complète
- ✅ Tests fonctionnels validés
- ✅ Performance acceptable (<500ms)
- ✅ Sécurité implémentée (JWT, hash)

---

## 12. Risques et Mitigation

### 12.1 Risques Techniques

| Risque                         | Impact | Probabilité | Mitigation                                 |
| ------------------------------ | ------ | ----------- | ------------------------------------------ |
| Incompatibilité entre services | Élevé  | Moyen       | Contrats d'API clairs, tests d'intégration |
| Problèmes de performance       | Moyen  | Faible      | Mise en cache, optimisation requêtes DB    |
| Échec de déploiement Docker    | Élevé  | Faible      | Documentation détaillée, tests réguliers   |
| Problèmes de sécurité JWT      | Élevé  | Faible      | Bibliothèques standard, revue de code      |

### 12.2 Risques Projet

| Risque                      | Impact | Probabilité | Mitigation                           |
| --------------------------- | ------ | ----------- | ------------------------------------ |
| Retard de développement     | Moyen  | Moyen       | Planning réaliste, priorisation MVP  |
| Complexité technique élevée | Élevé  | Élevé       | Formation continue, documentation    |
| Bugs en production          | Moyen  | Moyen       | Tests rigoureux, staging environment |

---

## 13. Glossaire

- **SOA**: Service-Oriented Architecture - Architecture orientée services
- **API Gateway**: Point d'entrée unique pour tous les clients
- **JWT**: JSON Web Token - Standard d'authentification
- **REST**: Representational State Transfer - Style architectural web
- **SOAP**: Simple Object Access Protocol - Protocole de services web
- **Microservice**: Service indépendant et déployable séparément
- **CRUD**: Create, Read, Update, Delete - Opérations de base
- **ORM**: Object-Relational Mapping - Mapping objet-relationnel
- **Docker**: Plateforme de containerisation

---

## 14. Contacts et Support

**Équipe de Développement:**

- Architecture et Backend: [Nom développeur]
- Frontend: [Nom développeur]
- DevOps et Déploiement: [Nom développeur]

**Encadrants Académiques:**

- Ghada Feki
- Amel Mdimagh
- Dorra Kechrid

**Cours:** Architecture SOA et Services Web  
**Institution:** 3ème année Licence GL-SI  
**Période:** Décembre 2024

---

## 15. Annexes

### Annexe A: Variables d'Environnement

Voir fichiers `.env.example` dans chaque service

### Annexe B: Collections Postman

Disponibles dans `/tests` et `/services/*/test-requests`

### Annexe C: Diagrammes UML

Disponibles dans `/documentation`

### Annexe D: Scripts de Test

Disponibles dans `/tests/scripts`

---

**Version:** 1.0  
**Statut:** ✅ Validé et Implémenté
