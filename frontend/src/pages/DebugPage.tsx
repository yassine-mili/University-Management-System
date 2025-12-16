import React, { useState } from "react";
import {
  Container,
  Typography,
  Button,
  Box,
  Paper,
  Alert,
} from "@mui/material";
import axios from "axios";

export const DebugPage: React.FC = () => {
  const [results, setResults] = useState<string[]>([]);

  const testService = async (name: string, url: string) => {
    try {
      const response = await axios.get(url, { timeout: 5000 });
      setResults((prev) => [...prev, `✅ ${name}: OK (${response.status})`]);
    } catch (error: any) {
      setResults((prev) => [...prev, `❌ ${name}: ${error.message}`]);
    }
  };

  const runAllTests = async () => {
    setResults(["Starting service tests..."]);

    await testService("Auth Service", "http://localhost:8081/actuator/health");
    await testService("Student Service", "http://localhost:8082/health");
    await testService(
      "Courses Service",
      "http://localhost:8083/CourseService?wsdl"
    );
    await testService("Grades Service", "http://localhost:8084/health");
    await testService(
      "Billing Service",
      "http://localhost:5000/BillingService.asmx?wsdl"
    );

    setResults((prev) => [...prev, "\nTests complete!"]);
  };

  const testAuth = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8081/api/v1/auth/login",
        {
          username: "test",
          password: "test123",
        }
      );
      setResults((prev) => [
        ...prev,
        `✅ Login test: ${JSON.stringify(response.data)}`,
      ]);
    } catch (error: any) {
      setResults((prev) => [...prev, `❌ Login test failed: ${error.message}`]);
      if (error.response) {
        setResults((prev) => [
          ...prev,
          `   Response: ${JSON.stringify(error.response.data)}`,
        ]);
      }
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Service Debug Page
      </Typography>

      <Box sx={{ mb: 2 }}>
        <Button variant="contained" onClick={runAllTests} sx={{ mr: 2 }}>
          Test All Services
        </Button>
        <Button variant="contained" color="secondary" onClick={testAuth}>
          Test Login API
        </Button>
        <Button
          variant="outlined"
          onClick={() => setResults([])}
          sx={{ ml: 2 }}
        >
          Clear
        </Button>
      </Box>

      <Paper
        sx={{ p: 2, bgcolor: "#000", color: "#0f0", fontFamily: "monospace" }}
      >
        {results.map((result, idx) => (
          <Typography key={idx} variant="body2" sx={{ whiteSpace: "pre-wrap" }}>
            {result}
          </Typography>
        ))}
      </Paper>

      <Alert severity="info" sx={{ mt: 2 }}>
        <Typography variant="body2">
          This page helps debug connectivity to backend services.
          <br />
          Expected ports: Auth (8081), Student (8082), Courses (8083), Grades
          (8084), Billing (5000)
        </Typography>
      </Alert>
    </Container>
  );
};
