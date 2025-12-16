import React, { useEffect, useState } from "react";
import {
  Container,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Box,
  CircularProgress,
  Alert,
  Card,
  CardContent,
  Grid,
  LinearProgress,
} from "@mui/material";
import GradeIcon from "@mui/icons-material/Grade";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import { useAuth } from "../context/AuthContext";
import { gradeService } from "../services/grade.service";
import { Grade } from "../types";

export const GradesPage: React.FC = () => {
  const { user } = useAuth();
  const [grades, setGrades] = useState<Grade[]>([]);
  const [gpa, setGpa] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadGrades();
  }, []);

  const loadGrades = async () => {
    // Teachers and admins don't have studentId, show appropriate message
    if (user?.role !== "STUDENT" && !user?.studentId) {
      setError(
        "This page is for students. Teachers can view grades from their course pages."
      );
      setLoading(false);
      return;
    }

    if (!user?.studentId) {
      setError("Student ID not found");
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const gradesData = await gradeService.getGradesByStudent(user.studentId);
      setGrades(gradesData);

      if (gradesData.length > 0) {
        const avgGpa = await gradeService.calculateGPA(user.studentId);
        setGpa(avgGpa);
      }
    } catch (err: any) {
      setError(err.message || "Failed to load grades");
      console.error("Error loading grades:", err);
    } finally {
      setLoading(false);
    }
  };

  const getGradeColor = (letterGrade: string) => {
    if (letterGrade.startsWith("A")) return "success";
    if (letterGrade.startsWith("B")) return "primary";
    if (letterGrade.startsWith("C")) return "info";
    if (letterGrade.startsWith("D")) return "warning";
    return "error";
  };

  const getGPAColor = (gpa: number) => {
    if (gpa >= 3.5) return "#4caf50";
    if (gpa >= 3.0) return "#2196f3";
    if (gpa >= 2.5) return "#ff9800";
    return "#f44336";
  };

  const calculateStats = () => {
    if (grades.length === 0) return { highest: 0, lowest: 0, average: 0 };

    const scores = grades.map((g) => g.score);
    return {
      highest: Math.max(...scores),
      lowest: Math.min(...scores),
      average: scores.reduce((a, b) => a + b, 0) / scores.length,
    };
  };

  const stats = calculateStats();

  if (loading) {
    return (
      <Container sx={{ mt: 8, display: "flex", justifyContent: "center" }}>
        <CircularProgress size={60} />
      </Container>
    );
  }

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
            Academic Performance
          </Typography>
          <Typography variant="body1" color="text.secondary">
            View your grades and academic progress
          </Typography>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError("")}>
            {error}
          </Alert>
        )}

        {/* Stats Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} md={3}>
            <Card
              elevation={3}
              sx={{
                background: `linear-gradient(135deg, ${getGPAColor(
                  gpa
                )} 0%, ${getGPAColor(gpa)}dd 100%)`,
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <GradeIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">Current GPA</Typography>
                </Box>
                <Typography variant="h2" sx={{ fontWeight: "bold" }}>
                  {gpa.toFixed(2)}
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={(gpa / 4.0) * 100}
                  sx={{
                    mt: 2,
                    height: 8,
                    borderRadius: 4,
                    bgcolor: "rgba(255,255,255,0.3)",
                    "& .MuiLinearProgress-bar": {
                      bgcolor: "white",
                    },
                  }}
                />
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={3}>
            <Card elevation={3}>
              <CardContent>
                <Typography color="text.secondary" gutterBottom>
                  Highest Score
                </Typography>
                <Typography
                  variant="h3"
                  sx={{ fontWeight: "bold", color: "#4caf50" }}
                >
                  {stats.highest.toFixed(1)}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Out of 100
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={3}>
            <Card elevation={3}>
              <CardContent>
                <Typography color="text.secondary" gutterBottom>
                  Average Score
                </Typography>
                <Typography
                  variant="h3"
                  sx={{ fontWeight: "bold", color: "#2196f3" }}
                >
                  {stats.average.toFixed(1)}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Across all courses
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={3}>
            <Card elevation={3}>
              <CardContent>
                <Typography color="text.secondary" gutterBottom>
                  Courses Graded
                </Typography>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {grades.length}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total completed
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Grades Table */}
        <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            🎓 All Grades
          </Typography>

          {grades.length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>Course</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Course ID</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Score</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Letter Grade
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Credits</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Semester</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {grades.map((grade) => (
                    <TableRow
                      key={grade.id}
                      sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                    >
                      <TableCell sx={{ fontWeight: "500" }}>
                        {grade.courseName || `Course ${grade.courseId}`}
                      </TableCell>
                      <TableCell>
                        <Chip label={grade.courseId} size="small" />
                      </TableCell>
                      <TableCell>
                        <Box
                          sx={{ display: "flex", alignItems: "center", gap: 1 }}
                        >
                          <Typography
                            sx={{ fontWeight: "bold", fontSize: "1.1rem" }}
                          >
                            {grade.score}
                          </Typography>
                          <Typography color="text.secondary">/100</Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={grade.letterGrade}
                          color={getGradeColor(grade.letterGrade) as any}
                          sx={{ fontWeight: "bold", minWidth: 50 }}
                        />
                      </TableCell>
                      <TableCell>3</TableCell>
                      <TableCell>
                        {grade.semester} {grade.year}
                      </TableCell>
                      <TableCell>
                        {new Date(grade.gradedAt).toLocaleDateString()}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Alert severity="info">
              No grades available yet. Grades will appear here once your
              instructors post them.
            </Alert>
          )}
        </Paper>
      </Container>
    </Box>
  );
};
