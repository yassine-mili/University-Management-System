const { prisma } = require('../config/prisma.config');
const { Prisma } = require('@prisma/client');

// Fonction utilitaire pour trouver un étudiant par ID (numérique) ou par numero_etudiant/email (chaîne)
async function findStudentByIdentifier(identifier) {
    let student;
    
    // Tente de trouver par l'ID numérique primaire
    if (!isNaN(parseInt(identifier))) {
         student = await prisma.student.findUnique({
             where: { id: parseInt(identifier) }
         });
    }

    // Tente par numero_etudiant
    if (!student) {
        student = await prisma.student.findUnique({
            where: { numero_etudiant: identifier }
        });
    }

    // Tente par email
    if (!student) {
        student = await prisma.student.findUnique({
            where: { email: identifier }
        });
    }

    return student;
}


// --- 1. CREATE : POST /api/v1/students ---
exports.createStudent = async (req, res) => {
    try {
        const student = await prisma.student.create({
            data: req.body,
        });
        res.status(201).json(student);
    } catch (error) {
        if (error instanceof Prisma.PrismaClientKnownRequestError && error.code === 'P2002') {
            return res.status(400).json({ message: 'Le numéro étudiant ou l\'email existe déjà.' });
        }
        res.status(500).json({ message: 'Erreur lors de la création de l\'étudiant.', error: error.message });
    }
};

// --- 2. READ ALL : GET /api/v1/students ---
exports.getAllStudents = async (req, res) => {
    try {
        const students = await prisma.student.findMany(); 
        res.status(200).json(students);
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la récupération des étudiants.' });
    }
};

// --- 3. READ ONE : GET /api/v1/students/:id ---
exports.getStudentById = async (req, res) => {
    try {
        const student = await findStudentByIdentifier(req.params.id);
        
        if (!student) {
            return res.status(404).json({ message: 'Étudiant non trouvé.' });
        }
        res.status(200).json(student);
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la récupération de l\'étudiant.', error: error.message });
    }
};

// --- 4. UPDATE : PUT /api/v1/students/:id ---
exports.updateStudent = async (req, res) => {
    const idParam = req.params.id;
    try {
        const studentToUpdate = await findStudentByIdentifier(idParam);
        
        if (!studentToUpdate) {
            return res.status(404).json({ message: 'Étudiant non trouvé pour mise à jour.' });
        }

        const student = await prisma.student.update({
            where: { id: studentToUpdate.id }, 
            data: req.body,
        });
        res.status(200).json(student);
    } catch (error) {
        if (error instanceof Prisma.PrismaClientKnownRequestError && error.code === 'P2002') {
            return res.status(400).json({ message: 'Le numéro étudiant ou l\'email existe déjà après mise à jour.' });
        }
        res.status(500).json({ message: 'Erreur lors de la mise à jour de l\'étudiant.', error: error.message });
    }
};

// --- 5. DELETE : DELETE /api/v1/students/:id ---
exports.deleteStudent = async (req, res) => {
    const idParam = req.params.id;
    try {
        const studentToDelete = await findStudentByIdentifier(idParam);

        if (!studentToDelete) {
            return res.status(204).send(); 
        }

        await prisma.student.delete({
            where: { id: studentToDelete.id }, 
        });

        res.status(204).send(); 
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la suppression de l\'étudiant.', error: error.message });
    }
};