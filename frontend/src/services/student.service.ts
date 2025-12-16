import { studentApi, handleApiError } from "./api";
import { Student, CreateStudentRequest } from "../types";

export const studentService = {
  // Get all students
  async getAllStudents(): Promise<Student[]> {
    try {
      const response = await studentApi.get<Student[]>("/");
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get student by ID
  async getStudentById(id: string): Promise<Student> {
    try {
      const response = await studentApi.get<Student>(`/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Create student
  async createStudent(data: CreateStudentRequest): Promise<Student> {
    try {
      const response = await studentApi.post<Student>("/", data);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Update student
  async updateStudent(id: string, data: Partial<Student>): Promise<Student> {
    try {
      const response = await studentApi.put<Student>(`/${id}`, data);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Delete student
  async deleteStudent(id: string): Promise<void> {
    try {
      await studentApi.delete(`/${id}`);
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Search students
  async searchStudents(query: string): Promise<Student[]> {
    try {
      const response = await studentApi.get<Student[]>(`/search?q=${query}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get student enrollments
  async getStudentEnrollments(studentId: string): Promise<any[]> {
    try {
      const response = await studentApi.get(`/${studentId}/enrollments`);
      return response.data?.data ?? [];
    } catch (error) {
      console.warn("Student enrollments unavailable:", handleApiError(error));
      return [];
    }
  },
};
