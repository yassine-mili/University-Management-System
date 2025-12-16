import axios from "axios";
import { handleApiError } from "./api";
import { Invoice, CreateInvoiceRequest, Payment } from "../types";

// SOAP service helper for Billing (.NET)
// Route SOAP calls through the API Gateway to avoid browser CORS blocks
const BILLING_SOAP_URL =
  "http://localhost:8080/api/billing/BillingService.asmx";

// Create dedicated axios instance for SOAP calls
const soapClient = axios.create({
  timeout: 10000,
});

// Add request interceptor to include JWT token
soapClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const buildSoapHeaders = (soapAction: string) => ({
  "Content-Type": "text/xml; charset=utf-8",
  SOAPAction: soapAction,
});

// Helper to create SOAP request for .NET service
const createBillingSOAPEnvelope = (
  method: string,
  params: Record<string, any>
): string => {
  const paramsXml = Object.entries(params)
    .map(([key, value]) => `<${key}>${value}</${key}>`)
    .join("");

  return `<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
               xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
               xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <${method} xmlns="http://billing.universite.com/">
      ${paramsXml}
    </${method}>
  </soap:Body>
</soap:Envelope>`;
};

// Helper to parse .NET SOAP response
const parseBillingSOAPResponse = (xml: string, method: string): any => {
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xml, "text/xml");
  const responseNode = xmlDoc.getElementsByTagName(`${method}Response`)[0];

  if (!responseNode) {
    throw new Error("Invalid SOAP response");
  }

  const resultNode = responseNode.getElementsByTagName(`${method}Result`)[0];
  if (!resultNode) {
    return null;
  }

  return resultNode.textContent;
};

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

export const billingService = {
  // Get all invoices
  async getAllInvoices(): Promise<Invoice[]> {
    try {
      const envelope = createBillingSOAPEnvelope("GetAllInvoices", {});

      const response = await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers: buildSoapHeaders(
          "http://billing.universite.com/GetAllInvoices"
        ),
      });

      return JSON.parse(
        parseBillingSOAPResponse(response.data, "GetAllInvoices") || "[]"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get invoice by ID
  async getInvoiceById(id: string): Promise<Invoice> {
    try {
      const envelope = createBillingSOAPEnvelope("GetInvoiceById", {
        invoiceId: id,
      });

      const response = await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers: buildSoapHeaders(
          "http://billing.universite.com/GetInvoiceById"
        ),
      });

      return JSON.parse(
        parseBillingSOAPResponse(response.data, "GetInvoiceById") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get invoices by student ID
  async getInvoicesByStudent(studentId: string): Promise<Invoice[]> {
    try {
      const normalizedId = normalizeStudentId(studentId);
      const envelope = createBillingSOAPEnvelope("GetInvoicesByStudent", {
        studentId: normalizedId,
      });

      const headers = buildSoapHeaders(
        "http://billing.universite.com/GetInvoicesByStudent"
      );

      console.log("Billing request headers:", headers);
      console.log("Student ID:", normalizedId);

      const response = await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers,
      });

      return JSON.parse(
        parseBillingSOAPResponse(response.data, "GetInvoicesByStudent") || "[]"
      );
    } catch (error) {
      console.error("Billing service error:", error);
      throw new Error(handleApiError(error));
    }
  },

  // Create invoice
  async createInvoice(data: CreateInvoiceRequest): Promise<Invoice> {
    try {
      const envelope = createBillingSOAPEnvelope("CreateInvoice", {
        studentId: data.studentId,
        amount: data.amount,
        description: data.description,
        dueDate: data.dueDate,
      });

      const response = await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers: buildSoapHeaders(
          "http://billing.universite.com/CreateInvoice"
        ),
      });

      return JSON.parse(
        parseBillingSOAPResponse(response.data, "CreateInvoice") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Pay invoice
  async payInvoice(invoiceId: string, paymentMethod: string): Promise<Payment> {
    try {
      const envelope = createBillingSOAPEnvelope("PayInvoice", {
        invoiceId,
        paymentMethod,
      });

      const response = await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers: buildSoapHeaders("http://billing.universite.com/PayInvoice"),
      });

      return JSON.parse(
        parseBillingSOAPResponse(response.data, "PayInvoice") || "{}"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Cancel invoice
  async cancelInvoice(invoiceId: string): Promise<void> {
    try {
      const envelope = createBillingSOAPEnvelope("CancelInvoice", {
        invoiceId,
      });

      await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers: buildSoapHeaders(
          "http://billing.universite.com/CancelInvoice"
        ),
      });
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Get payment history
  async getPaymentHistory(studentId: string): Promise<Payment[]> {
    try {
      const envelope = createBillingSOAPEnvelope("GetPaymentHistory", {
        studentId,
      });

      const response = await soapClient.post(BILLING_SOAP_URL, envelope, {
        headers: buildSoapHeaders(
          "http://billing.universite.com/GetPaymentHistory"
        ),
      });

      return JSON.parse(
        parseBillingSOAPResponse(response.data, "GetPaymentHistory") || "[]"
      );
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
};
