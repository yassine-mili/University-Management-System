import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Box,
  Typography,
  Button,
  Paper,
  Grid,
  Card,
  CardContent,
} from "@mui/material";
import SchoolIcon from "@mui/icons-material/School";
import PersonIcon from "@mui/icons-material/Person";
import AssignmentIcon from "@mui/icons-material/Assignment";
import PaymentIcon from "@mui/icons-material/Payment";
import { useAuth } from "../context/AuthContext";

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const features = [
    {
      icon: <SchoolIcon sx={{ fontSize: 48, color: "#1976d2" }} />,
      title: "Course Management",
      description:
        "Browse and enroll in courses, view schedules, and track your academic progress.",
    },
    {
      icon: <AssignmentIcon sx={{ fontSize: 48, color: "#1976d2" }} />,
      title: "Grade Tracking",
      description:
        "View your grades, calculate GPA, and monitor your academic performance in real-time.",
    },
    {
      icon: <PaymentIcon sx={{ fontSize: 48, color: "#1976d2" }} />,
      title: "Billing System",
      description:
        "Manage tuition payments, view invoices, and track your financial status.",
    },
    {
      icon: <PersonIcon sx={{ fontSize: 48, color: "#1976d2" }} />,
      title: "Student Portal",
      description:
        "Access your personal dashboard with all your academic information in one place.",
    },
  ];

  const handleGetStarted = () => {
    if (isAuthenticated) {
      navigate("/dashboard");
    } else {
      navigate("/login");
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        py: 8,
      }}
    >
      <Container maxWidth="lg">
        {/* Hero Section */}
        <Paper
          elevation={8}
          sx={{
            p: 6,
            mb: 6,
            textAlign: "center",
            background: "rgba(255, 255, 255, 0.95)",
            backdropFilter: "blur(10px)",
          }}
        >
          <SchoolIcon sx={{ fontSize: 80, color: "#1976d2", mb: 2 }} />
          <Typography
            variant="h2"
            component="h1"
            gutterBottom
            sx={{
              fontWeight: "bold",
              color: "#1976d2",
              mb: 2,
            }}
          >
            University Management System
          </Typography>
          <Typography
            variant="h5"
            sx={{
              mb: 4,
              color: "#555",
              maxWidth: "800px",
              mx: "auto",
            }}
          >
            A comprehensive platform for managing your academic journey. Access
            courses, grades, billing, and more in one unified system.
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={handleGetStarted}
            sx={{
              px: 6,
              py: 2,
              fontSize: "1.1rem",
              borderRadius: 2,
            }}
          >
            {isAuthenticated ? "Go to Dashboard" : "Get Started"}
          </Button>
        </Paper>

        {/* Features Section */}
        <Typography
          variant="h4"
          align="center"
          gutterBottom
          sx={{
            mb: 4,
            color: "white",
            fontWeight: "bold",
          }}
        >
          Key Features
        </Typography>

        <Grid container spacing={3}>
          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={3} key={index}>
              <Card
                elevation={4}
                sx={{
                  height: "100%",
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  p: 3,
                  transition: "transform 0.3s, box-shadow 0.3s",
                  "&:hover": {
                    transform: "translateY(-8px)",
                    boxShadow: 8,
                  },
                }}
              >
                <Box sx={{ mb: 2 }}>{feature.icon}</Box>
                <CardContent sx={{ textAlign: "center", p: 0 }}>
                  <Typography
                    variant="h6"
                    gutterBottom
                    sx={{ fontWeight: "bold" }}
                  >
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {/* Call to Action */}
        <Box sx={{ mt: 6, textAlign: "center" }}>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate("/register")}
            sx={{
              px: 4,
              py: 1.5,
              color: "white",
              borderColor: "white",
              fontSize: "1rem",
              "&:hover": {
                borderColor: "white",
                backgroundColor: "rgba(255, 255, 255, 0.1)",
              },
            }}
          >
            Create an Account
          </Button>
        </Box>
      </Container>
    </Box>
  );
};
