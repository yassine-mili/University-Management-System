// Fichier : student-service/src/config/prisma.config.js

const { PrismaClient } = require('@prisma/client');

// Créer une instance unique du client Prisma pour toute l'application
// Le client récupère l'URI de connexion depuis la variable d'environnement DATABASE_URL (ou POSTGRES_URI en fallback)
const prisma = new PrismaClient({
    log: process.env.NODE_ENV === 'development' ? ['query', 'info', 'warn', 'error'] : ['warn', 'error'],
});

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

// Graceful shutdown
process.on('SIGTERM', async () => {
    console.log('SIGTERM reçu, fermeture des connexions...');
    await prisma.$disconnect();
    process.exit(0);
});

module.exports = {
    prisma,
    connectDB
};