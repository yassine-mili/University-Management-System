import React from "react";
import { Container, Paper, Typography, Box, Alert } from "@mui/material";
import GroupIcon from "@mui/icons-material/Group";

export const StudentsPage: React.FC = () => {
  return (
    <Box
      sx={{
        bgcolor: "#f5f5f5",
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        py: 4,
      }}
    >
      <Container maxWidth="lg" sx={{ px: 3 }}>
        <Box sx={{ mb: 4 }}>
          <Typography
            variant="h4"
            gutterBottom
            sx={{ fontWeight: "bold", color: "#1e1e2d" }}
          >
            <GroupIcon sx={{ mr: 2, fontSize: 32, verticalAlign: "middle" }} />
            Students Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            View and manage student enrollments
          </Typography>
        </Box>

        <Paper elevation={3} sx={{ p: 4, borderRadius: 2 }}>
          <Alert severity="info">
            Students management functionality coming soon. Teachers will be able
            to view enrolled students, manage attendance, and grade assignments.
          </Alert>
        </Paper>
      </Container>
    </Box>
  );
};
