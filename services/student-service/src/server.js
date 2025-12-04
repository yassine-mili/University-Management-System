// Fichier : student-service/src/server.js

const express = require('express');
const { connectDB } = require('./config/prisma.config'); // Importation de la connexion Prisma
const cors = require('cors');

// Initialisation du serveur Express
const app = express();

// --- Configuration et Middlewares ---
// Établir la connexion à la base de données
connectDB(); 

// Middleware pour analyser les corps de requête JSON (très important pour les requêtes POST/PUT)
app.use(express.json());

// Middleware CORS (permet de gérer l'accès inter-domaines)
app.use(cors());

// --- Définition des Routes (temporaire, sera remplacé) ---
// Route de base (Santé/Health check)
app.get('/', (req, res) => {
    res.send('Service Étudiants (Node.js/REST) est en cours de construction avec Prisma/PostgreSQL.');
});


// --- Démarrage du Serveur ---
// Définir le port, en utilisant une variable d'environnement si elle existe
const PORT = process.env.PORT || 3000; 
app.listen(PORT, () => {
    console.log(`🚀 Serveur du Service Étudiants démarré sur le port ${PORT}`);
});