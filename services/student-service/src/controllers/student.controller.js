const { prisma } = require("../config/prisma.config");
const { Prisma } = require("@prisma/client");
const { getSampleEnrollments } = require("../data/sample-data");

// Fonction utilitaire pour trouver un étudiant par ID (numérique) ou par numero_etudiant/email (chaîne)
async function findStudentByIdentifier(identifier) {
  let student;

  // Tente de trouver par l'ID numérique primaire
  if (!isNaN(parseInt(identifier))) {
    student = await prisma.student.findUnique({
      where: { id: parseInt(identifier) },
    });
  }

  // Tente par numero_etudiant
  if (!student) {
    student = await prisma.student.findUnique({
      where: { numero_etudiant: identifier },
    });
  }

  // Tente par email
  if (!student) {
    student = await prisma.student.findUnique({
      where: { email: identifier },
    });
  }

  return student;
}

// Fonction de validation des données d'étudiant
function validateStudentData(data) {
  const errors = [];

  if (
    !data.numero_etudiant ||
    typeof data.numero_etudiant !== "string" ||
    !data.numero_etudiant.trim()
  ) {
    errors.push("numero_etudiant est requis et doit être une chaîne non vide");
  }
  if (
    !data.email ||
    typeof data.email !== "string" ||
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.email)
  ) {
    errors.push("email est requis et doit être une adresse email valide");
  }
  if (!data.nom || typeof data.nom !== "string" || !data.nom.trim()) {
    errors.push("nom est requis et doit être une chaîne non vide");
  }
  if (!data.prenom || typeof data.prenom !== "string" || !data.prenom.trim()) {
    errors.push("prenom est requis et doit être une chaîne non vide");
  }
  if (!data.niveau || typeof data.niveau !== "string" || !data.niveau.trim()) {
    errors.push("niveau est requis (ex: L1, L2, L3, M1, M2)");
  }

  return errors;
}

// --- 1. CREATE : POST /api/v1/students ---
exports.createStudent = async (req, res) => {
  try {
    // Validation des données
    const validationErrors = validateStudentData(req.body);
    if (validationErrors.length > 0) {
      return res.status(400).json({
        message: "Données invalides",
        errors: validationErrors,
      });
    }

    const student = await prisma.student.create({
      data: {
        numero_etudiant: req.body.numero_etudiant.trim(),
        email: req.body.email.trim().toLowerCase(),
        nom: req.body.nom.trim(),
        prenom: req.body.prenom.trim(),
        niveau: req.body.niveau.trim(),
        date_naissance: req.body.date_naissance
          ? new Date(req.body.date_naissance)
          : null,
        filiere: req.body.filiere ? req.body.filiere.trim() : null,
        est_actif: req.body.est_actif !== undefined ? req.body.est_actif : true,
      },
    });
    res.status(201).json({
      message: "Étudiant créé avec succès",
      data: student,
    });
  } catch (error) {
    if (
      error instanceof Prisma.PrismaClientKnownRequestError &&
      error.code === "P2002"
    ) {
      const field = error.meta?.target?.[0] || "champ unique";
      return res.status(409).json({
        message: `Le ${field} existe déjà.`,
        error: "Conflict",
      });
    }
    console.error("Erreur lors de la création:", error);
    res.status(500).json({
      message: "Erreur lors de la création de l'étudiant.",
      error: error.message,
    });
  }
};

// --- 2. READ ALL : GET /api/v1/students ---
exports.getAllStudents = async (req, res) => {
  try {
    const { page = 1, limit = 10, actif, niveau } = req.query;
    const skip = (parseInt(page) - 1) * parseInt(limit);

    // Construire les filtres
    const where = {};
    if (actif !== undefined) {
      where.est_actif = actif === "true";
    }
    if (niveau) {
      where.niveau = niveau;
    }

    const [students, total] = await Promise.all([
      prisma.student.findMany({
        where,
        skip,
        take: parseInt(limit),
        orderBy: { createdAt: "desc" },
      }),
      prisma.student.count({ where }),
    ]);

    res.status(200).json({
      message: "Étudiants récupérés avec succès",
      data: students,
      pagination: {
        total,
        page: parseInt(page),
        limit: parseInt(limit),
        pages: Math.ceil(total / parseInt(limit)),
      },
    });
  } catch (error) {
    console.error("Erreur lors de la récupération:", error);
    res.status(500).json({
      message: "Erreur lors de la récupération des étudiants.",
      error: error.message,
    });
  }
};

// Internal lookup: GET /api/v1/students/internal/by-email/:email
exports.getStudentByIdentifier = async (req, res) => {
  try {
    const student = await findStudentByIdentifier(req.params.email);

    if (!student) {
      return res.status(404).json({
        message: "Étudiant non trouvé.",
        error: "Not Found",
      });
    }

    return res.status(200).json({
      message: "Étudiant récupéré avec succès",
      data: student,
    });
  } catch (error) {
    console.error("Erreur lors de la récupération par identifiant:", error);
    return res.status(500).json({
      message: "Erreur lors de la récupération de l'étudiant.",
      error: error.message,
    });
  }
};

// Enrollments placeholder: GET /api/v1/students/:id/enrollments
exports.getStudentEnrollments = async (req, res) => {
  try {
    const student = await findStudentByIdentifier(req.params.id);

    if (!student) {
      return res.status(404).json({
        message: "Étudiant non trouvé.",
        error: "Not Found",
      });
    }

    const enrollments = getSampleEnrollments(student.numero_etudiant);

    return res.status(200).json({
      message: "Inscriptions de l'étudiant récupérées avec succès",
      data: enrollments,
    });
  } catch (error) {
    console.error("Erreur lors de la récupération des inscriptions:", error);
    return res.status(500).json({
      message: "Erreur lors de la récupération des inscriptions.",
      error: error.message,
    });
  }
};

// --- 3. READ ONE : GET /api/v1/students/:id ---
exports.getStudentById = async (req, res) => {
  try {
    const student = await findStudentByIdentifier(req.params.id);

    if (!student) {
      return res.status(404).json({
        message: "Étudiant non trouvé.",
        error: "Not Found",
      });
    }
    res.status(200).json({
      message: "Étudiant récupéré avec succès",
      data: student,
    });
  } catch (error) {
    console.error("Erreur lors de la récupération:", error);
    res.status(500).json({
      message: "Erreur lors de la récupération de l'étudiant.",
      error: error.message,
    });
  }
};

// --- 4. UPDATE : PUT /api/v1/students/:id ---
exports.updateStudent = async (req, res) => {
  const idParam = req.params.id;
  try {
    const studentToUpdate = await findStudentByIdentifier(idParam);

    if (!studentToUpdate) {
      return res.status(404).json({
        message: "Étudiant non trouvé pour mise à jour.",
        error: "Not Found",
      });
    }

    // Préparer les données à mettre à jour (seulement les champs fournis)
    const updateData = {};
    if (req.body.numero_etudiant !== undefined)
      updateData.numero_etudiant = req.body.numero_etudiant.trim();
    if (req.body.email !== undefined)
      updateData.email = req.body.email.trim().toLowerCase();
    if (req.body.nom !== undefined) updateData.nom = req.body.nom.trim();
    if (req.body.prenom !== undefined)
      updateData.prenom = req.body.prenom.trim();
    if (req.body.niveau !== undefined)
      updateData.niveau = req.body.niveau.trim();
    if (req.body.date_naissance !== undefined)
      updateData.date_naissance = req.body.date_naissance
        ? new Date(req.body.date_naissance)
        : null;
    if (req.body.filiere !== undefined)
      updateData.filiere = req.body.filiere ? req.body.filiere.trim() : null;
    if (req.body.est_actif !== undefined)
      updateData.est_actif = req.body.est_actif;

    const student = await prisma.student.update({
      where: { id: studentToUpdate.id },
      data: updateData,
    });
    res.status(200).json({
      message: "Étudiant mis à jour avec succès",
      data: student,
    });
  } catch (error) {
    if (
      error instanceof Prisma.PrismaClientKnownRequestError &&
      error.code === "P2002"
    ) {
      const field = error.meta?.target?.[0] || "champ unique";
      return res.status(409).json({
        message: `Le ${field} existe déjà après mise à jour.`,
        error: "Conflict",
      });
    }
    console.error("Erreur lors de la mise à jour:", error);
    res.status(500).json({
      message: "Erreur lors de la mise à jour de l'étudiant.",
      error: error.message,
    });
  }
};

// --- 5. DELETE : DELETE /api/v1/students/:id ---
exports.deleteStudent = async (req, res) => {
  const idParam = req.params.id;
  try {
    const studentToDelete = await findStudentByIdentifier(idParam);

    if (!studentToDelete) {
      return res.status(404).json({
        message: "Étudiant non trouvé pour suppression.",
        error: "Not Found",
      });
    }

    await prisma.student.delete({
      where: { id: studentToDelete.id },
    });

    res.status(200).json({
      message: "Étudiant supprimé avec succès",
      data: { id: studentToDelete.id },
    });
  } catch (error) {
    console.error("Erreur lors de la suppression:", error);
    res.status(500).json({
      message: "Erreur lors de la suppression de l'étudiant.",
      error: error.message,
    });
  }
};
