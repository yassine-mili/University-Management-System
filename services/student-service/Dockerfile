# Fichier : student-service/Dockerfile

# Étape 1 : Image de base avec la version de Node.js supportée
FROM node:18-alpine AS base

# Définir le répertoire de travail
WORKDIR /usr/src/app

# Copier les fichiers de dépendance pour profiter du cache Docker
COPY package*.json ./
COPY prisma ./prisma/

# Installer les dépendances
# --only=production n'installe que les dépendances nécessaires à l'exécution (pas nodemon, prisma CLI)
# Pour les migrations, nous avons besoin de Prisma CLI, donc nous installerons toutes les dépendances.
RUN npm install

# Générer le client Prisma (doit se faire pendant le build)
RUN npx prisma generate

# Copier le reste du code source
COPY src ./src/
COPY entrypoint.sh ./entrypoint.sh

# Rendre le script exécutable
RUN chmod +x ./entrypoint.sh

# Étape de démarrage
# Le port 3000 doit être exposé et correspond au PORT défini dans server.js
EXPOSE 3000

# Commande de démarrage du service Node.js
CMD ["./entrypoint.sh"]