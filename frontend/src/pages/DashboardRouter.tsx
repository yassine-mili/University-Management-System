import React from "react";
import { useAuth } from "../context/AuthContext";
import { StudentDashboard } from "./StudentDashboard";
import { TeacherOverviewDashboard } from "./TeacherOverviewDashboard";
import { AdminDashboard } from "./AdminDashboard";
import { Box, CircularProgress } from "@mui/material";

export const DashboardRouter: React.FC = () => {
  const { user } = useAuth();

  if (!user) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", mt: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  switch (user.role) {
    case "TEACHER":
      return <TeacherOverviewDashboard />;
    case "ADMIN":
      return <AdminDashboard />;
    case "STUDENT":
    default:
      return <StudentDashboard />;
  }
};
