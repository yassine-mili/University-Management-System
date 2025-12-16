import axios from "axios";
import { handleApiError } from "./api";
import { Course, CreateCourseRequest, Enrollment } from "../types";

// SOAP service helper for Courses (JAX-WS)
const COURSES_SOAP_URL = "http://localhost:8083/CourseService";

// Helper to create SOAP request
const createSOAPEnvelope = (
  method: string,
  params: Record<string, any>
): string => {
  const paramsXml = Object.entries(params)
    .map(([key, value]) => `<${key}>${value}</${key}>`)
    .join("");

  return `<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
               xmlns:cour="http://courses.universite.com/">
  <soap:Header/>
  <soap:Body>
    <cour:${method}>
      ${paramsXml}
    </cour:${method}>
  </soap:Body>
</soap:Envelope>`;
};

// Helper to parse SOAP response
const parseSOAPResponse = (xml: string, method: string): any => {
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xml, "text/xml");
  const responseNode = xmlDoc.getElementsByTagName(`${method}Response`)[0];

  if (!responseNode) {
    throw new Error("Invalid SOAP response");
  }

  // Extract return value
  const returnNode = responseNode.getElementsByTagName("return")[0];
  if (!returnNode) {
    return null;
  }

  return returnNode.textContent;
};

export const courseService = {
  // Get all courses
  async getAllCourses(): Promise<Course[]> {
    try {
      const envelope = createSOAPEnvelope("getAllCourses", {});

      const response = await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "getAllCourses",
        },
      });

      // Parse SOAP response and convert to Course array
      // This is a simplified version - you may need to adjust based on actual response
      return JSON.parse(
        parseSOAPResponse(response.data, "getAllCourses") || "[]"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get course by ID
  async getCourseById(id: string): Promise<Course> {
    try {
      const envelope = createSOAPEnvelope("getCourseById", { id });

      const response = await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "getCourseById",
        },
      });

      return JSON.parse(
        parseSOAPResponse(response.data, "getCourseById") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Create course
  async createCourse(data: CreateCourseRequest): Promise<Course> {
    try {
      const envelope = createSOAPEnvelope("createCourse", {
        code: data.code,
        name: data.name,
        description: data.description,
        credits: data.credits,
        teacherId: data.teacherId,
        semester: data.semester,
        year: data.year,
        maxStudents: data.maxStudents,
      });

      const response = await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "createCourse",
        },
      });

      return JSON.parse(
        parseSOAPResponse(response.data, "createCourse") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Update course
  async updateCourse(id: string, data: Partial<Course>): Promise<Course> {
    try {
      const envelope = createSOAPEnvelope("updateCourse", {
        id,
        ...data,
      });

      const response = await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "updateCourse",
        },
      });

      return JSON.parse(
        parseSOAPResponse(response.data, "updateCourse") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Delete course
  async deleteCourse(id: string): Promise<void> {
    try {
      const envelope = createSOAPEnvelope("deleteCourse", { id });

      await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "deleteCourse",
        },
      });
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Enroll student in course
  async enrollStudent(
    courseId: string,
    studentId: string
  ): Promise<Enrollment> {
    try {
      const envelope = createSOAPEnvelope("enrollStudent", {
        courseId,
        studentId,
      });

      const response = await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "enrollStudent",
        },
      });

      return JSON.parse(
        parseSOAPResponse(response.data, "enrollStudent") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get course enrollments
  async getCourseEnrollments(courseId: string): Promise<Enrollment[]> {
    try {
      const envelope = createSOAPEnvelope("getCourseEnrollments", { courseId });

      const response = await axios.post(COURSES_SOAP_URL, envelope, {
        headers: {
          "Content-Type": "text/xml",
          SOAPAction: "getCourseEnrollments",
        },
      });

      return JSON.parse(
        parseSOAPResponse(response.data, "getCourseEnrollments") || "[]"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
};
