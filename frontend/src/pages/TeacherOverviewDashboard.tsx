import React from "react";
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Card,
  CardContent,
} from "@mui/material";
import SchoolIcon from "@mui/icons-material/School";
import PeopleIcon from "@mui/icons-material/People";
import AssignmentIcon from "@mui/icons-material/Assignment";
import { useAuth } from "../context/AuthContext";

export const TeacherOverviewDashboard: React.FC = () => {
  const { user } = useAuth();

  const stats = [
    {
      title: "My Courses",
      value: "0",
      icon: <SchoolIcon sx={{ fontSize: 40 }} />,
      color: "#667eea",
    },
    {
      title: "Total Students",
      value: "0",
      icon: <PeopleIcon sx={{ fontSize: 40 }} />,
      color: "#f5576c",
    },
    {
      title: "Assignments",
      value: "0",
      icon: <AssignmentIcon sx={{ fontSize: 40 }} />,
      color: "#4caf50",
    },
  ];

  return (
    <Box sx={{ bgcolor: "#f5f5f5", minHeight: "100vh", py: 4 }}>
      <Container maxWidth="lg">
        {/* Header */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h4" sx={{ fontWeight: "bold", mb: 1 }}>
            Welcome back, {user?.username}!
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Here's an overview of your teaching activities
          </Typography>
        </Box>

        {/* Stats Cards */}
        <Grid container spacing={3}>
          {stats.map((stat, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Card
                elevation={2}
                sx={{
                  height: "100%",
                  background: `linear-gradient(135deg, ${stat.color}15 0%, ${stat.color}05 100%)`,
                  borderLeft: `4px solid ${stat.color}`,
                }}
              >
                <CardContent>
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                    }}
                  >
                    <Box>
                      <Typography
                        variant="body2"
                        color="text.secondary"
                        gutterBottom
                      >
                        {stat.title}
                      </Typography>
                      <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                        {stat.value}
                      </Typography>
                    </Box>
                    <Box sx={{ color: stat.color }}>{stat.icon}</Box>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
};
