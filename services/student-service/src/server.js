// Fichier : student-service/src/server.js

const express = require("express");
const { connectDB } = require("./config/prisma.config");
const cors = require("cors");
const swaggerUi = require("swagger-ui-express");
const swaggerSpecs = require("./config/swagger.config");
const studentRoutes = require("./routes/student.routes");
const traceMiddleware = require("./middleware/trace.middleware");
const {
  errorHandler,
  notFoundHandler,
} = require("./middleware/error.middleware");
const { seedSampleStudents } = require("./data/sample-data");

// Initialisation du serveur Express
const app = express();

// --- Configuration et Middlewares ---
// Établir la connexion à la base de données puis injecter des données de démonstration
connectDB()
  .then(async () => {
    await seedSampleStudents();
  })
  .catch((error) => {
    console.error("❌ Échec lors de la connexion à la base de données", error);
  });

// Trace ID middleware (must be first)
app.use(traceMiddleware);

// Middleware pour analyser les corps de requête JSON
app.use(express.json());

// Middleware CORS
app.use(cors());

// --- Documentation Swagger ---
app.use(
  "/api-docs",
  swaggerUi.serve,
  swaggerUi.setup(swaggerSpecs, {
    swaggerOptions: {
      persistAuthorization: true,
      displayOperationId: false,
    },
    customCss: ".swagger-ui .topbar { display: none }",
    customSiteTitle: "Student Service API Documentation",
    customfavIcon: "https://fastapi.tiangolo.com/img/favicon.png",
  })
);

// --- Définition des Routes ---
// Route de base (Santé/Health check)
app.get("/", (req, res) => {
  res.status(200).json({
    message: "✅ Service Étudiants (Node.js/REST) est opérationnel",
    status: "running",
    database: "PostgreSQL + Prisma",
    version: "1.0.0",
    documentation: "http://localhost:8082/api-docs",
    traceId: req.traceId,
  });
});

// Health check endpoint
app.get("/health", (req, res) => {
  res.status(200).json({
    status: "healthy",
    service: "student-service",
    timestamp: new Date().toISOString(),
    traceId: req.traceId,
  });
});

// Routes pour les étudiants avec versioning API
app.use("/api/v1/students", studentRoutes);

// 404 handler
app.use(notFoundHandler);

// Global error handler (must be last)
app.use(errorHandler);

// --- Démarrage du Serveur ---
const PORT = process.env.PORT || 8082;
app.listen(PORT, () => {
  console.log(`🚀 Serveur du Service Étudiants démarré sur le port ${PORT}`);
  console.log(`📚 API disponible sur http://localhost:${PORT}/api/v1/students`);
  console.log(
    `📖 Documentation Swagger disponible sur http://localhost:${PORT}/api-docs`
  );
  console.log(
    `🔐 JWT Secret configured: ${
      process.env.JWT_SECRET ? "Yes" : "Using default"
    }`
  );
});
