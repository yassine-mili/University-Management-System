// Fichier : student-service/src/config/swagger.config.js

const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Student Service API',
      version: '1.0.0',
      description: 'RESTful API for managing student records with full CRUD operations, PostgreSQL persistence, and Prisma ORM',
      contact: {
        name: 'University Management System',
        url: 'https://github.com/yassine-mili/University-Management-System'
      },
      license: {
        name: 'MIT',
        url: 'https://opensource.org/licenses/MIT'
      }
    },
    servers: [
      {
        url: 'http://localhost:8082/api/v1',
        description: 'Development server (Local)'
      },
      {
        url: 'http://localhost:3000/api/v1',
        description: 'Development server (Direct)'
      }
    ],
    components: {
      schemas: {
        Student: {
          type: 'object',
          required: ['numero_etudiant', 'email', 'nom', 'prenom', 'niveau'],
          properties: {
            id: {
              type: 'integer',
              description: 'Student ID (primary key)',
              example: 1
            },
            numero_etudiant: {
              type: 'string',
              description: 'Unique student number',
              example: 'STU001'
            },
            email: {
              type: 'string',
              format: 'email',
              description: 'Unique email address',
              example: 'alice@university.edu'
            },
            nom: {
              type: 'string',
              description: 'Last name',
              example: 'Martin'
            },
            prenom: {
              type: 'string',
              description: 'First name',
              example: 'Alice'
            },
            niveau: {
              type: 'string',
              description: 'Academic level',
              enum: ['L1', 'L2', 'L3', 'M1', 'M2'],
              example: 'L1'
            },
            filiere: {
              type: 'string',
              description: 'Field of study (optional)',
              example: 'Informatique'
            },
            date_naissance: {
              type: 'string',
              format: 'date',
              description: 'Date of birth (optional)',
              example: '2002-05-15'
            },
            date_inscription: {
              type: 'string',
              format: 'date-time',
              description: 'Registration date (auto-set)',
              example: '2025-12-06T16:54:25.833Z'
            },
            est_actif: {
              type: 'boolean',
              description: 'Active status',
              example: true
            },
            createdAt: {
              type: 'string',
              format: 'date-time',
              description: 'Creation timestamp',
              example: '2025-12-06T16:54:25.833Z'
            },
            updatedAt: {
              type: 'string',
              format: 'date-time',
              description: 'Last update timestamp',
              example: '2025-12-06T16:54:52.319Z'
            }
          }
        },
        CreateStudentRequest: {
          type: 'object',
          required: ['numero_etudiant', 'email', 'nom', 'prenom', 'niveau'],
          properties: {
            numero_etudiant: {
              type: 'string',
              example: 'STU001'
            },
            email: {
              type: 'string',
              format: 'email',
              example: 'alice@university.edu'
            },
            nom: {
              type: 'string',
              example: 'Martin'
            },
            prenom: {
              type: 'string',
              example: 'Alice'
            },
            niveau: {
              type: 'string',
              example: 'L1'
            },
            filiere: {
              type: 'string',
              example: 'Informatique'
            },
            date_naissance: {
              type: 'string',
              format: 'date',
              example: '2002-05-15'
            },
            est_actif: {
              type: 'boolean',
              example: true
            }
          }
        },
        UpdateStudentRequest: {
          type: 'object',
          properties: {
            numero_etudiant: {
              type: 'string'
            },
            email: {
              type: 'string',
              format: 'email'
            },
            nom: {
              type: 'string'
            },
            prenom: {
              type: 'string'
            },
            niveau: {
              type: 'string'
            },
            filiere: {
              type: 'string'
            },
            date_naissance: {
              type: 'string',
              format: 'date'
            },
            est_actif: {
              type: 'boolean'
            }
          }
        },
        SuccessResponse: {
          type: 'object',
          properties: {
            message: {
              type: 'string',
              example: 'Étudiant créé avec succès'
            },
            data: {
              $ref: '#/components/schemas/Student'
            }
          }
        },
        PaginatedResponse: {
          type: 'object',
          properties: {
            message: {
              type: 'string',
              example: 'Étudiants récupérés avec succès'
            },
            data: {
              type: 'array',
              items: {
                $ref: '#/components/schemas/Student'
              }
            },
            pagination: {
              type: 'object',
              properties: {
                total: {
                  type: 'integer',
                  example: 10
                },
                page: {
                  type: 'integer',
                  example: 1
                },
                limit: {
                  type: 'integer',
                  example: 10
                },
                pages: {
                  type: 'integer',
                  example: 1
                }
              }
            }
          }
        },
        ErrorResponse: {
          type: 'object',
          properties: {
            message: {
              type: 'string',
              example: 'Erreur lors de l\'opération'
            },
            error: {
              type: 'string',
              example: 'Not Found'
            }
          }
        },
        ValidationErrorResponse: {
          type: 'object',
          properties: {
            message: {
              type: 'string',
              example: 'Données invalides'
            },
            errors: {
              type: 'array',
              items: {
                type: 'string'
              },
              example: ['email est requis et doit être une adresse email valide']
            }
          }
        }
      }
    }
  },
  apis: ['./src/routes/*.js', './src/server.js']
};

const specs = swaggerJsdoc(options);
module.exports = specs;
