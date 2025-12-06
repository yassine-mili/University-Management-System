-- CreateTable
CREATE TABLE "students" (
    "id" SERIAL NOT NULL,
    "numero_etudiant" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "nom" TEXT NOT NULL,
    "prenom" TEXT NOT NULL,
    "niveau" TEXT NOT NULL,
    "date_naissance" DATE,
    "filiere" TEXT,
    "date_inscription" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "est_actif" BOOLEAN NOT NULL DEFAULT true,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "students_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "students_numero_etudiant_key" ON "students"("numero_etudiant");

-- CreateIndex
CREATE UNIQUE INDEX "students_email_key" ON "students"("email");
