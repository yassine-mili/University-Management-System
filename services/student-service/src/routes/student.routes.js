const express = require("express");
const router = express.Router();
const studentController = require("../controllers/student.controller");
const {
  authenticateToken,
  authorize,
} = require("../middleware/auth.middleware");

// Le chemin de base pour ces routes sera /api/v1/students
// Protected routes require JWT authentication

/**
 * @swagger
 * /students:
 *   post:
 *     summary: Create a new student
 *     description: Creates a new student record with validation (Requires authentication)
 *     tags:
 *       - Students
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateStudentRequest'
 *     responses:
 *       201:
 *         description: Student created successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       400:
 *         description: Validation error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ValidationErrorResponse'
 *       401:
 *         description: Unauthorized - Invalid or missing token
 *       409:
 *         description: Duplicate unique field (email or numero_etudiant)
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *   get:
 *     summary: Get all students
 *     description: Retrieve a list of all students with pagination and filtering (Requires authentication)
 *     tags:
 *       - Students
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: query
 *         name: page
 *         schema:
 *           type: integer
 *           default: 1
 *         description: Page number for pagination
 *       - in: query
 *         name: limit
 *         schema:
 *           type: integer
 *           default: 10
 *         description: Records per page
 *       - in: query
 *         name: actif
 *         schema:
 *           type: boolean
 *         description: Filter by active status
 *       - in: query
 *         name: niveau
 *         schema:
 *           type: string
 *           enum: ['L1', 'L2', 'L3', 'M1', 'M2']
 *         description: Filter by academic level
 *     responses:
 *       200:
 *         description: List of students retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/PaginatedResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router
  .route("/")
  .post(
    authenticateToken,
    authorize("ADMIN", "STAFF"),
    studentController.createStudent
  )
  .get(authenticateToken, studentController.getAllStudents);

// Internal endpoint for service-to-service communication (no auth required)
router.post("/internal", studentController.createStudent);
router.get(
  "/internal/by-email/:email",
  studentController.getStudentByIdentifier
);

/**
 * @swagger
 * /students/{id}:
 *   get:
 *     summary: Get a single student
 *     description: Retrieve a specific student by ID, numero_etudiant, or email
 *     tags:
 *       - Students
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: Student ID, numero_etudiant, or email
 *         example: "1"
 *     responses:
 *       200:
 *         description: Student retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       404:
 *         description: Student not found
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *   put:
 *     summary: Update a student
 *     description: Update an existing student's information (partial update supported)
 *     tags:
 *       - Students
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: Student ID, numero_etudiant, or email
 *         example: "1"
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateStudentRequest'
 *     responses:
 *       200:
 *         description: Student updated successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SuccessResponse'
 *       404:
 *         description: Student not found
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       409:
 *         description: Duplicate unique field
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *   delete:
 *     summary: Delete a student
 *     description: Delete a student record from the database
 *     tags:
 *       - Students
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: Student ID, numero_etudiant, or email
 *         example: "1"
 *     responses:
 *       200:
 *         description: Student deleted successfully
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                 data:
 *                   type: object
 *                   properties:
 *                     id:
 *                       type: integer
 *       404:
 *         description: Student not found
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Internal server error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
router
  .route("/:id")
  .get(authenticateToken, studentController.getStudentById)
  .put(
    authenticateToken,
    authorize("ADMIN", "STAFF"),
    studentController.updateStudent
  )
  .delete(
    authenticateToken,
    authorize("ADMIN"),
    studentController.deleteStudent
  );

router.get(
  "/:id/enrollments",
  authenticateToken,
  studentController.getStudentEnrollments
);

module.exports = router;
