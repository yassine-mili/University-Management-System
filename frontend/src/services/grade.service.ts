import axios from "axios";
import { gradesApi, handleApiError } from "./api";
import { Grade, CreateGradeRequest } from "../types";

const normalizeStudentId = (studentId: string): string => {
  if (!studentId) {
    return studentId;
  }

  const digits = studentId.match(/\d+/g);
  if (!digits) {
    return studentId;
  }

  const numericId = parseInt(digits.join(""), 10);
  return Number.isNaN(numericId) ? studentId : `${numericId}`;
};

export const gradeService = {
  // Get all grades
  async getAllGrades(): Promise<Grade[]> {
    try {
      const response = await gradesApi.get<Grade[]>("/");
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get grade by ID
  async getGradeById(id: string): Promise<Grade> {
    try {
      const response = await gradesApi.get<Grade>(`/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get grades by student ID
  async getGradesByStudent(studentId: string): Promise<Grade[]> {
    try {
      const normalizedId = normalizeStudentId(studentId);
      const response = await gradesApi.get(`/student/${normalizedId}`);
      const grades = response.data?.data?.grades ?? [];
      return grades;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        if (status === 404 || status === 422) {
          return [];
        }
      }
      throw new Error(handleApiError(error));
    }
  },

  // Get grades by course ID
  async getGradesByCourse(courseId: string): Promise<Grade[]> {
    try {
      const response = await gradesApi.get<Grade[]>(`/course/${courseId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get grades by teacher ID
  async getGradesByTeacher(teacherId: string): Promise<Grade[]> {
    try {
      const response = await gradesApi.get<Grade[]>(`/teacher/${teacherId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Create grade
  async createGrade(data: CreateGradeRequest): Promise<Grade> {
    try {
      const response = await gradesApi.post<Grade>("/", data);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Update grade
  async updateGrade(id: string, data: Partial<Grade>): Promise<Grade> {
    try {
      const response = await gradesApi.put<Grade>(`/${id}`, data);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Delete grade
  async deleteGrade(id: string): Promise<void> {
    try {
      await gradesApi.delete(`/${id}`);
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Calculate GPA for student
  async calculateGPA(studentId: string): Promise<number> {
    try {
      const normalizedId = normalizeStudentId(studentId);
      const response = await gradesApi.get(`/student/${normalizedId}/gpa`);
      return response.data?.data?.gpa ?? 0;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        if (status === 404 || status === 422) {
          return 0;
        }
      }
      throw new Error(handleApiError(error));
    }
  },
};
