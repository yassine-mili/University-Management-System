// User and Authentication Types
export interface User {
  id: string;
  username: string;
  email: string;
  role: "STUDENT" | "TEACHER" | "ADMIN";
  createdAt: string;
  lastLogin?: string;
  studentId?: string; // Student number for students
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: "STUDENT" | "TEACHER" | "ADMIN";
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

// Student Types
export interface Student {
  id: string;
  studentId: string;
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  enrollmentDate: string;
  major: string;
  gpa?: number;
  status: "ACTIVE" | "INACTIVE" | "GRADUATED" | "SUSPENDED";
  createdAt: string;
  updatedAt: string;
}

export interface CreateStudentRequest {
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  major: string;
}

// Course Types
export interface Course {
  id: string;
  code: string;
  name: string;
  description: string;
  credits: number;
  teacherId: string;
  teacherName?: string;
  semester: string;
  year: number;
  maxStudents: number;
  enrolledStudents: number;
  status: "ACTIVE" | "INACTIVE" | "COMPLETED";
}

export interface CreateCourseRequest {
  code: string;
  name: string;
  description: string;
  credits: number;
  teacherId: string;
  semester: string;
  year: number;
  maxStudents: number;
}

export interface Enrollment {
  id: string;
  studentId: string;
  courseId: string;
  enrollmentDate: string;
  status: "ENROLLED" | "DROPPED" | "COMPLETED";
}

// Grade Types
export interface Grade {
  id: string;
  studentId: string;
  courseId: string;
  courseName?: string;
  teacherId: string;
  score: number;
  letterGrade: string;
  semester: string;
  year: number;
  comments?: string;
  gradedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateGradeRequest {
  studentId: string;
  courseId: string;
  teacherId: string;
  score: number;
  semester: string;
  year: number;
  comments?: string;
}

// Billing/Invoice Types
export interface Invoice {
  id: string;
  studentId: string;
  studentName?: string;
  amount: number;
  description: string;
  dueDate: string;
  status: "PENDING" | "PAID" | "OVERDUE" | "CANCELLED";
  createdAt: string;
  paidAt?: string;
}

export interface CreateInvoiceRequest {
  studentId: string;
  amount: number;
  description: string;
  dueDate: string;
}

export interface Payment {
  id: string;
  invoiceId: string;
  amount: number;
  paymentMethod: string;
  paymentDate: string;
  transactionId: string;
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: ApiError;
}

export interface ApiError {
  code: string;
  message: string;
  details?: any;
  timestamp: string;
  traceId?: string;
}

// Dashboard Types
export interface DashboardStats {
  totalStudents?: number;
  totalCourses?: number;
  totalTeachers?: number;
  totalRevenue?: number;
  pendingInvoices?: number;
  activeEnrollments?: number;
}

export interface StudentDashboard {
  enrolledCourses: Course[];
  recentGrades: Grade[];
  pendingInvoices: Invoice[];
  gpa: number;
}

export interface TeacherDashboard {
  courses: Course[];
  totalStudents: number;
  recentGrades: Grade[];
}

export interface AdminDashboard {
  stats: DashboardStats;
  recentStudents: Student[];
  recentCourses: Course[];
  revenueChart: any[];
}
