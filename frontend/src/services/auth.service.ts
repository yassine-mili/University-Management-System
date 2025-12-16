import { authApi, handleApiError } from "./api";
import { LoginRequest, RegisterRequest, AuthResponse, User } from "../types";

export const authService = {
  // Login
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await authApi.post<{
        message: string;
        data: AuthResponse;
      }>("/login", credentials);
      const { accessToken, user } = response.data.data;

      // Store token and user in localStorage
      localStorage.setItem("token", accessToken);
      localStorage.setItem("user", JSON.stringify(user));

      return response.data.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Register
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await authApi.post<{
        message: string;
        data: AuthResponse;
      }>("/register", data);
      const { accessToken, user } = response.data.data;

      // Store token and user in localStorage
      localStorage.setItem("token", accessToken);
      localStorage.setItem("user", JSON.stringify(user));

      return response.data.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Logout
  logout(): void {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },

  // Get current user from localStorage
  getCurrentUser(): User | null {
    const userStr = localStorage.getItem("user");
    if (!userStr) return null;

    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  },

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!localStorage.getItem("token");
  },

  // Get token
  getToken(): string | null {
    return localStorage.getItem("token");
  },

  // Validate token (optional - call backend to verify)
  async validateToken(): Promise<boolean> {
    try {
      await authApi.get("/validate");
      return true;
    } catch {
      return false;
    }
  },
};
