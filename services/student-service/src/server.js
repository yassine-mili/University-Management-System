// Fichier : student-service/src/server.js

const express = require('express');
const { connectDB } = require('./config/prisma.config'); // Importation de la connexion Prisma
const cors = require('cors');
const swaggerUi = require('swagger-ui-express');
const swaggerSpecs = require('./config/swagger.config');
const studentRoutes = require('./routes/student.routes'); // Importation des routes étudiants

// Initialisation du serveur Express
const app = express();

// --- Configuration et Middlewares ---
// Établir la connexion à la base de données
connectDB(); 

// Middleware pour analyser les corps de requête JSON (très important pour les requêtes POST/PUT)
app.use(express.json());

// Middleware CORS (permet de gérer l'accès inter-domaines)
app.use(cors());

// --- Documentation Swagger ---
// Swagger UI disponible sur /api-docs
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpecs, {
    swaggerOptions: {
        persistAuthorization: true,
        displayOperationId: false,
    },
    customCss: '.swagger-ui .topbar { display: none }',
    customSiteTitle: 'Student Service API Documentation',
    customfavIcon: 'https://fastapi.tiangolo.com/img/favicon.png'
}));

// --- Définition des Routes ---
// Route de base (Santé/Health check)
app.get('/', (req, res) => {
    res.status(200).json({ 
        message: '✅ Service Étudiants (Node.js/REST) est opérationnel',
        status: 'running',
        database: 'PostgreSQL + Prisma',
        version: '1.0.0',
        documentation: 'http://localhost:8082/api-docs'
    });
});

// Routes pour les étudiants avec versioning API
app.use('/api/v1/students', studentRoutes);

// Route pour les non-trouvés (404)
app.use((req, res) => {
    res.status(404).json({ 
        message: 'Endpoint non trouvé',
        path: req.path,
        method: req.method,
        documentation: 'Voir http://localhost:8082/api-docs pour la documentation'
    });
});

// --- Gestion des erreurs globales ---
app.use((err, req, res, next) => {
    console.error('Erreur non gérée:', err);
    res.status(500).json({ 
        message: 'Erreur interne du serveur',
        error: process.env.NODE_ENV === 'production' ? 'Une erreur est survenue' : err.message
    });
});

// --- Démarrage du Serveur ---
// Définir le port, en utilisant une variable d'environnement si elle existe
const PORT = process.env.PORT || 8082; 
app.listen(PORT, () => {
    console.log(`🚀 Serveur du Service Étudiants démarré sur le port ${PORT}`);
    console.log(`📚 API disponible sur http://localhost:${PORT}/api/v1/students`);
    console.log(`📖 Documentation Swagger disponible sur http://localhost:${PORT}/api-docs`);
});