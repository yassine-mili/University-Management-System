// Fichier : student-service/src/config/prisma.config.js

const { PrismaClient } = require('@prisma/client');

// Créer une instance unique du client Prisma pour toute l'application
// Le client récupère l'URI de connexion depuis la variable d'environnement POSTGRES_URI
const prisma = new PrismaClient();

// Fonction de vérification de la connexion
async function connectDB() {
    try {
        await prisma.$connect();
        console.log('✅ Client Prisma connecté à PostgreSQL.');
    } catch (error) {
        console.error('❌ Erreur de connexion au Client Prisma:', error.message);
        // Quitter le processus avec échec
        process.exit(1);
    }
}

module.exports = {
    prisma,
    connectDB
};