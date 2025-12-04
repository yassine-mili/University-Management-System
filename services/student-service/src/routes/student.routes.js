const express = require('express');
const router = express.Router();
const studentController = require('../controllers/student.controller');

// Le chemin de base pour ces routes sera /api/v1/students

// Route pour POST (CREATE) et GET (READ All)
router.route('/')
    .post(studentController.createStudent) 
    .get(studentController.getAllStudents); 

// Route pour GET (READ by ID/Numéro), PUT (UPDATE) et DELETE
router.route('/:id')
    .get(studentController.getStudentById) 
    .put(studentController.updateStudent) 
    .delete(studentController.deleteStudent); 

module.exports = router;