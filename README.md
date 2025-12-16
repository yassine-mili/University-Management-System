# 🎓 University Management System - SOA Architecture

> Système complet de gestion universitaire basé sur une architecture orientée services (SOA) avec microservices polyglotte, API Gateway, et interface web moderne.

[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-61DAFB?logo=react&logoColor=black)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.9.5-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)

## 📋 Vue d'Ensemble

Projet académique démontrant une architecture microservices complète pour la gestion des opérations universitaires : inscriptions, cours, notes, et facturation. Le système utilise 5 technologies backend différentes, supporte REST et SOAP, et inclut une interface React moderne.

### ✨ Fonctionnalités Principales

- 🔐 **Authentification JWT** - Connexion sécurisée avec gestion des rôles (Étudiant, Enseignant, Admin)
- 📚 **Gestion des Cours** - Catalogue de cours, inscriptions, et gestion des capacités
- 📝 **Système de Notes** - Saisie, consultation, et calcul automatique des moyennes
- 💰 **Facturation** - Génération et suivi des factures, gestion des paiements
- 👥 **Gestion Utilisateurs** - CRUD complet avec rôles et permissions
- 📊 **Tableaux de Bord** - Interfaces personnalisées par rôle
- 🌐 **Interface Responsive** - Application web React optimisée mobile

## 🏗️ Architecture Technique

### Architecture Globale

```
┌─────────────────────────────────────────────────────────┐
│           Frontend (React + TypeScript)                 │
│              http://localhost:3000                      │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│         API Gateway (Spring Cloud Gateway)              │
│              http://localhost:8080                      │
│  • JWT Validation  • Rate Limiting  • CORS              │
└──────┬──────┬──────┬──────┬──────────────────────────────┘
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
            │   PostgreSQL    │
            │  (5 Databases)  │
            └─────────────────┘
```

### Stack Technologique

| Composant           | Technologie                           | Port      | Protocole   |
| ------------------- | ------------------------------------- | --------- | ----------- |
| **Frontend**        | React 18.2 + TypeScript + Material-UI | 3000      | HTTP        |
| **API Gateway**     | Spring Cloud Gateway + Redis          | 8080      | HTTP        |
| **Auth Service**    | Spring Boot 3.1.5 + Spring Security   | 8081      | REST        |
| **Student Service** | Node.js 18 + Express + Prisma         | 8082      | REST        |
| **Course Service**  | Spring Boot 3.1.5 + Apache CXF        | 8083      | REST + SOAP |
| **Grade Service**   | Python 3.10 + FastAPI                 | 8084      | REST        |
| **Billing Service** | .NET 7.0 + ASP.NET Core               | 5000      | REST + SOAP |
| **Databases**       | PostgreSQL 14 (×5)                    | 5432-5436 | SQL         |
| **Cache**           | Redis 7.0                             | 6379      | Redis       |

## 🎯 Microservices

## 🎯 Services Détaillés

### 1. 🔐 Authentication Service (Spring Boot)

**Responsabilités :**

- Inscription et connexion des utilisateurs
- Génération et validation de tokens JWT (HS512)
- Gestion des rôles : STUDENT, TEACHER, ADMIN
- Hash des mots de passe avec BCrypt (coût 12)

**Technologies :** Spring Boot 3.1.5, Spring Security 6, PostgreSQL, JWT (jjwt 0.11.5)

**Endpoints :** `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/validate`

### 2. 👥 Student Service (Node.js + Express)

**Responsabilités :**

- CRUD complet des dossiers étudiants
- Recherche et filtrage avec pagination
- Validation des données (email unique, numéro étudiant unique)
- Documentation Swagger/OpenAPI

**Technologies :** Node.js 18, Express.js 4.18, Prisma ORM 5.0, PostgreSQL

**Endpoints :** `/api/v1/students` (GET, POST, PUT, DELETE)

### 3. 📚 Course Service (Spring Boot + SOAP)

**Responsabilités :**

- Gestion du catalogue de cours
- Inscriptions/désinscriptions des étudiants
- Gestion des capacités et prérequis
- Support REST et SOAP (Apache CXF)

**Technologies :** Spring Boot 3.1.5, Apache CXF 4.0, Spring Data JPA, PostgreSQL

**SOAP WSDL :** `http://localhost:8083/soap/courses?wsdl`

### 4. 📝 Grade Service (Python + FastAPI)

**Responsabilités :**

- Saisie et modification des notes
- Calcul automatique des moyennes pondérées
- Génération de bulletins et statistiques

**Technologies :** Python 3.10, FastAPI 0.104, SQLAlchemy 2.0, PostgreSQL

**Documentation :** `http://localhost:8084/docs` (Swagger UI)

### 5. 💰 Billing Service (.NET Core)

**Responsabilités :**

- Génération automatique des factures
- Gestion des paiements et échéances
- Suivi des statuts (PENDING, PAID, OVERDUE, CANCELLED)
- Support REST et SOAP

**Technologies :** .NET 7.0, ASP.NET Core, Entity Framework Core, PostgreSQL

**SOAP WSDL :** `http://localhost:5000/soap/billing?wsdl`

### 6. 🚪 API Gateway (Spring Cloud Gateway)

**Responsabilités :**

- Point d'entrée unique pour tous les clients
- Routage intelligent vers les microservices
- Validation JWT pour routes protégées
- Rate limiting avec Redis (100 req/min)
- Gestion CORS et logging centralisé

**Technologies :** Spring Cloud Gateway 4.0.7, Spring Boot 3.1.5, Redis 7.0

### 7. 💻 Frontend (React + TypeScript)

**Responsabilités :**

- Interface utilisateur moderne et responsive
- Tableaux de bord personnalisés par rôle
- Gestion des cours, notes, et factures
- Authentification et gestion de session

**Technologies :** React 18.2, TypeScript 4.9, Material-UI 5.14, Axios, React Router v6

## 🛠️ Technologies et Outils

**Backend Frameworks:**

- Spring Boot 3.1.5 (Java 17)
- Node.js 18 + Express.js 4.18
- FastAPI 0.104 (Python 3.10)
- .NET 7.0 + ASP.NET Core

**Frontend:**

- React 18.2.0 + TypeScript 4.9.5
- Material-UI 5.14.0
- Axios pour les appels API
- React Router v6

**Protocoles API:**

- REST APIs (JSON)
- SOAP Web Services (XML/WSDL)
- JWT Authentication (HS512)

**Bases de Données:**

- PostgreSQL 14 (5 bases séparées)
- Redis 7.0 (cache et rate limiting)

**Infrastructure:**

- Docker 24.0+ & Docker Compose 2.20+
- Nginx (serving frontend)
- Multi-container orchestration

**Sécurité:**

- JWT Authentication (HS512)
- BCrypt password hashing (coût 12)
- Spring Security 6
- CORS configuration
- Rate limiting (Redis)

## 🔑 Fonctionnalités Clés

### Pour les Étudiants 👨‍🎓

- ✅ Inscription et connexion sécurisée
- ✅ Consultation du catalogue de cours
- ✅ Inscription/désinscription aux cours
- ✅ Consultation des notes et moyennes
- ✅ Gestion des factures et paiements
- ✅ Profil personnalisé et paramètres

### Pour les Enseignants 👨‍🏫

- ✅ Gestion des cours (création, modification)
- ✅ Consultation de la liste des étudiants inscrits
- ✅ Saisie et modification des notes
- ✅ Statistiques et analytics par cours
- ✅ Communication avec les étudiants

### Pour les Administrateurs 👨‍💼

- ✅ Gestion complète des utilisateurs
- ✅ Supervision de tous les cours
- ✅ Gestion de la facturation globale
- ✅ Rapports et statistiques détaillés
- ✅ Configuration du système

### Fonctionnalités Techniques 🛠️

- ✅ Architecture microservices polyglotte
- ✅ API Gateway centralisée avec routage intelligent
- ✅ Authentification JWT distribuée
- ✅ Support REST et SOAP
- ✅ Containerisation Docker complète
- ✅ Load balancing et rate limiting
- ✅ Documentation technique exhaustive
- ✅ Interface responsive (mobile-first)

## 🚀 Démarrage Rapide

### Prérequis

- **Docker Desktop** 24.0+ ([Télécharger](https://www.docker.com/products/docker-desktop))
- **Docker Compose** 2.20+
- **Git** pour cloner le projet
- **8GB RAM minimum** recommandé

### Installation et Lancement

1. **Cloner le projet**

```bash
git clone https://github.com/votre-repo/University-Management-System.git
cd University-Management-System
```

2. **Démarrer tous les services avec Docker**

```bash
cd docker
docker compose up -d
```

3. **Vérifier le statut des services**

```bash
docker compose ps
```

4. **Accéder à l'application**

- **Frontend** : http://localhost:3000
- **API Gateway** : http://localhost:8080
- **Swagger Docs** : http://localhost:8084/docs (Grade Service)

### Premiers Pas

1. **Créer un compte**

   - Ouvrez http://localhost:3000
   - Cliquez sur "S'inscrire"
   - Remplissez le formulaire (rôle: STUDENT, TEACHER ou ADMIN)

2. **Se connecter**

   - Utilisez vos identifiants
   - Vous serez redirigé vers votre tableau de bord

3. **Compte Admin par défaut** (si configuré)
   ```
   Email: admin@university.com
   Password: Admin123!
   ```

### Arrêt et Nettoyage

```bash
# Arrêter tous les services (données conservées)
docker compose down

# Arrêter et supprimer les volumes (réinitialisation complète)
docker compose down -v

# Voir les logs d'un service spécifique
docker compose logs -f [service_name]
```

## 📊 Endpoints API Principaux

### Authentication Service (Port 8081)

```http
POST   /api/v1/auth/register    # Inscription
POST   /api/v1/auth/login       # Connexion
GET    /api/v1/auth/me          # Profil utilisateur
```

### Student Service (Port 8082)

```http
GET    /api/v1/students         # Liste des étudiants (paginée)
POST   /api/v1/students         # Créer un étudiant
GET    /api/v1/students/:id     # Détails d'un étudiant
PUT    /api/v1/students/:id     # Mettre à jour
DELETE /api/v1/students/:id     # Supprimer
```

### Course Service (Port 8083)

```http
GET    /api/v1/courses                    # Liste des cours
POST   /api/v1/courses/:id/enroll         # Inscrire un étudiant
GET    /api/v1/student/:id/enrollments    # Cours d'un étudiant
DELETE /api/v1/courses/:id/enroll/:sid    # Se désinscrire
```

**SOAP :** `http://localhost:8083/soap/courses?wsdl`

### Grade Service (Port 8084)

```http
POST   /api/v1/grades                 # Ajouter une note
GET    /api/v1/grades/student/:id     # Notes d'un étudiant
GET    /api/v1/grades/course/:code    # Notes d'un cours
PUT    /api/v1/grades/:id             # Modifier une note
```

**Documentation :** `http://localhost:8084/docs`

### Billing Service (Port 5000)

```http
GET    /api/v1/invoices/student/:id    # Factures d'un étudiant
POST   /api/v1/invoices                # Créer une facture
POST   /api/v1/invoices/:id/pay        # Enregistrer un paiement
```

**SOAP :** `http://localhost:5000/soap/billing?wsdl`

## 📚 Documentation

Le projet inclut une documentation complète dans le dossier `/documentation` :

- **[Cahier des Charges](documentation/cahier-des-charges.md)** - Spécifications fonctionnelles complètes
- **[Spécifications Techniques](documentation/specifications-techniques.md)** - Architecture détaillée et API
- **[Manuel d'Utilisation](documentation/manuel-utilisation.md)** - Guide utilisateur complet
- **[Guide de Démarrage](QUICK_START.md)** - Démarrage rapide du projet
- **[Documentation d'Intégration](documentation/integration/)** - Guides d'intégration détaillés

## 🏗️ Structure du Projet

```
University-Management-System/
├── docker/
│   └── docker-compose.yml          # Orchestration des services
├── frontend/                        # Application React + TypeScript
│   ├── src/
│   │   ├── components/             # Composants réutilisables
│   │   ├── pages/                  # Pages de l'application
│   │   ├── services/               # Appels API
│   │   └── context/                # Context API (auth, etc.)
│   └── Dockerfile
├── services/
│   ├── auth-service/               # Spring Boot - Authentication
│   ├── student-service/            # Node.js - Gestion étudiants
│   ├── course-service/             # Spring Boot - Gestion cours (SOAP+REST)
│   ├── grade-service/              # Python FastAPI - Notes
│   ├── billing-service/            # .NET Core - Facturation (SOAP+REST)
│   └── api-gateway/                # Spring Cloud Gateway
├── documentation/
│   ├── cahier-des-charges.md
│   ├── specifications-techniques.md
│   ├── manuel-utilisation.md
│   └── integration/
└── README.md
```

## 🧪 Tests

Chaque service inclut ses propres tests :

```bash
# Tests unitaires Auth Service (Spring Boot)
cd services/auth-service
./mvnw test

# Tests Student Service (Node.js)
cd services/student-service
npm test

# Tests Grade Service (Python)
cd services/grade-service
pytest

# Tests Billing Service (.NET)
cd services/billing-service
dotnet test
```

## 🎓 Objectifs Pédagogiques

## 🚀 Running the Project

```bash
# Using Docker Compose
docker-compose up -d

# Services will be available at:
# Gateway: http://localhost:8080
# Auth: http://localhost:8081
# Students: http://localhost:8082
# Grades: http://localhost:8083
# Billing: http://localhost:8084
```

## 🎓 Objectifs Pédagogiques

Ce projet démontre la maîtrise de :

### Architecture et Design Patterns

- ✅ Architecture orientée services (SOA)
- ✅ Microservices pattern avec polyglot persistence
- ✅ API Gateway pattern
- ✅ Database per Service pattern
- ✅ Circuit Breaker pattern (pour la résilience)

### Technologies Backend

- ✅ Spring Boot (Java) - Framework enterprise
- ✅ Node.js + Express - Runtime JavaScript
- ✅ Python + FastAPI - Framework moderne asynchrone
- ✅ .NET Core (C#) - Écosystème Microsoft
- ✅ Spring Cloud Gateway - API Gateway

### Protocoles et Communication

- ✅ RESTful API design
- ✅ SOAP Web Services (WSDL)
- ✅ HTTP/HTTPS
- ✅ JSON et XML
- ✅ Communication inter-services

### Sécurité

- ✅ JWT Authentication (HS512)
- ✅ Spring Security
- ✅ BCrypt password hashing
- ✅ CORS configuration
- ✅ Rate limiting

### Bases de Données

- ✅ PostgreSQL (relationnel)
- ✅ Prisma ORM (Node.js)
- ✅ Spring Data JPA (Java)
- ✅ SQLAlchemy (Python)
- ✅ Entity Framework Core (.NET)

### DevOps et Infrastructure

- ✅ Containerisation Docker
- ✅ Docker Compose orchestration
- ✅ Multi-stage builds
- ✅ Health checks
- ✅ Volume persistence

### Frontend

- ✅ React 18 avec Hooks
- ✅ TypeScript pour le typage statique
- ✅ Material-UI pour le design
- ✅ State management (Context API)
- ✅ Routing (React Router)

## 📈 Métriques du Projet

- **Lignes de Code :** ~15,000+ lignes
- **Services Backend :** 5 microservices + 1 API Gateway
- **Technologies :** 7 technologies différentes
- **Endpoints API :** 40+ endpoints REST et SOAP
- **Composants Frontend :** 30+ composants React
- **Tests :** Tests unitaires et d'intégration
- **Documentation :** 4 documents complets (500+ pages)

## 🤝 Contribution

Ce projet est un projet académique, mais les contributions sont les bienvenues pour améliorer le code et la documentation.

### Comment Contribuer

1. Forkez le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Pushez vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 🐛 Signalement de Bugs

Si vous trouvez un bug, veuillez :

1. Vérifier qu'il n'a pas déjà été signalé dans les Issues
2. Créer une nouvelle Issue avec :
   - Description détaillée du problème
   - Étapes pour reproduire
   - Comportement attendu vs comportement actuel
   - Captures d'écran si pertinent
   - Environnement (OS, version Docker, etc.)

## 📝 Roadmap et Améliorations Futures

- [ ] Ajout de tests E2E avec Cypress
- [ ] Intégration CI/CD avec GitHub Actions
- [ ] Monitoring avec Prometheus + Grafana
- [ ] Logging centralisé avec ELK Stack
- [ ] Authentification OAuth2/OpenID Connect
- [ ] Support multi-langue (i18n)
- [ ] Notifications en temps réel (WebSocket)
- [ ] Export de rapports en PDF
- [ ] Application mobile (React Native)
- [ ] Déploiement Kubernetes

## 👥 Équipe

Projet académique réalisé dans le cadre du cours **Architecture SOA et Services Web** en 3ème année Licence Génie Logiciel et Système d'Information.

**Répartition des rôles :**

- Architecture et design du système
- Développement des microservices
- Développement frontend
- Intégration et tests
- Documentation et présentation

## 👤 Auteurs

**Mili Yassine**

- Portfolio: [yassinemili.me](https://yassinemili.me)
- LinkedIn: [mili-yassine](https://linkedin.com/in/mili-yassine)

**Battikh Youssef**

- Portfolio: -
- LinkedIn: [ysf-battikh](https://www.linkedin.com/in/ysf-battikh)

**Ksouri Fahmi**

- Portfolio: -
- LinkedIn: -

## 🎯 Contexte Académique

**Cours :** Architecture SOA et Services Web  
**Institution :** 3ème année Licence GL-SI  
**Encadrants :**

- Ghada Feki
- Amel Mdimagh
- Dorra Kechrid

**Période :** Décembre 2024  
**Évaluation :** Semaine du 15 Décembre 2024

## 📄 Licence

Projet Académique - Licence MIT

```
MIT License

Copyright (c) 2024 University Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

**⭐ Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile sur GitHub !**

**📧 Contact :** Pour toute question, ouvrez une Issue ou contactez l'équipe via LinkedIn.
