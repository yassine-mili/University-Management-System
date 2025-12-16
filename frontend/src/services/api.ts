import axios, {
  AxiosInstance,
  AxiosError,
  InternalAxiosRequestConfig,
} from "axios";

const API_URLS = {
  AUTH: "/api/auth",
  STUDENT: "/api/students",
  COURSES: "/api/courses",
  GRADES: "/api/grades",
  BILLING: "/api/billing",
};

// Create axios instance with default config
const createApiClient = (baseURL: string): AxiosInstance => {
  const client = axios.create({
    baseURL,
    timeout: 10000,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Request interceptor - Add JWT token
  client.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = localStorage.getItem("token");
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error: AxiosError) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor - Handle errors
  client.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      if (error.response?.status === 401) {
        // Unauthorized - clear token and redirect to login
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
      }
      return Promise.reject(error);
    }
  );

  return client;
};

// API clients for each service
export const authApi = createApiClient(API_URLS.AUTH);
export const studentApi = createApiClient(API_URLS.STUDENT);
export const coursesApi = createApiClient(API_URLS.COURSES);
export const gradesApi = createApiClient(API_URLS.GRADES);
export const billingApi = createApiClient(API_URLS.BILLING);

// Helper function to handle API errors
export const handleApiError = (error: any): string => {
  if (error.response) {
    // Server responded with error
    const data = error.response.data;
    return data.message || data.error?.message || "An error occurred";
  } else if (error.request) {
    // Request made but no response
    return "No response from server. Please check your connection.";
  } else {
    // Error setting up request
    return error.message || "An unexpected error occurred";
  }
};

// Export API URLs for direct access if needed
export { API_URLS };
