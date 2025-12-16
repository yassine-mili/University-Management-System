import React, { useEffect, useState } from "react";
import {
  Container,
  Grid,
  Paper,
  Typography,
  Card,
  CardContent,
  Button,
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
} from "@mui/material";
import SchoolIcon from "@mui/icons-material/School";
import GradeIcon from "@mui/icons-material/Grade";
import AssignmentIcon from "@mui/icons-material/Assignment";
import PaymentIcon from "@mui/icons-material/Payment";
import { useAuth } from "../context/AuthContext";
import { studentService } from "../services/student.service";
import { gradeService } from "../services/grade.service";
import { billingService } from "../services/billing.service";
import { Course, Grade, Invoice } from "../types";

export const StudentDashboard: React.FC = () => {
  const { user } = useAuth();
  const [courses, setCourses] = useState<Course[]>([]);
  const [grades, setGrades] = useState<Grade[]>([]);
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [gpa, setGpa] = useState<number>(0);
  const [loading, setLoading] = useState(true);

  const loadDashboardData = async () => {
    // Always start with clean state
    setCourses([]);
    setGrades([]);
    setInvoices([]);
    setGpa(0);

    try {
      if (!user?.studentId) {
        console.warn("No student ID found for user");
        setLoading(false);
        return;
      }

      // Load student's courses, grades, and invoices - handle errors gracefully
      try {
        const studentData = await studentService.getStudentEnrollments(
          user.studentId
        );
        setCourses(Array.isArray(studentData) ? studentData : []);
      } catch (error) {
        console.warn("Could not load courses:", error);
        setCourses([]);
      }

      try {
        const gradesData = await gradeService.getGradesByStudent(
          user.studentId
        );
        setGrades(gradesData);

        // Calculate GPA
        if (gradesData.length > 0) {
          const avgGpa = await gradeService.calculateGPA(user.studentId);
          setGpa(avgGpa);
        }
      } catch (error) {
        console.warn("Could not load grades:", error);
        setGrades([]);
      }

      try {
        const invoicesData = await billingService.getInvoicesByStudent(
          user.studentId
        );
        setInvoices(invoicesData);
      } catch (error) {
        console.warn("Could not load invoices:", error);
        setInvoices([]);
      }
    } catch (error) {
      console.error("Error loading dashboard:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
            Student Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Welcome back, <strong>{user?.username}</strong>! Here's your
            academic overview
          </Typography>
        </Box>

        {/* Stats Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                color: "white",
                transition: "transform 0.3s",
                "&:hover": { transform: "translateY(-8px)" },
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <SchoolIcon sx={{ mr: 1, fontSize: 32 }} />
                  <Typography variant="h6">Enrolled</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {courses.length}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9 }}>
                  Active Courses
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                color: "white",
                transition: "transform 0.3s",
                "&:hover": { transform: "translateY(-8px)" },
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <GradeIcon sx={{ mr: 1, fontSize: 32 }} />
                  <Typography variant="h6">GPA</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {gpa.toFixed(2)}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9 }}>
                  Current Average
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
                color: "white",
                transition: "transform 0.3s",
                "&:hover": { transform: "translateY(-8px)" },
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <AssignmentIcon sx={{ mr: 1, fontSize: 32 }} />
                  <Typography variant="h6">Completed</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {grades.length}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9 }}>
                  Graded Courses
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              sx={{
                background: "linear-gradient(135deg, #fa709a 0%, #fee140 100%)",
                color: "white",
                transition: "transform 0.3s",
                "&:hover": { transform: "translateY(-8px)" },
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <PaymentIcon sx={{ mr: 1, fontSize: 32 }} />
                  <Typography variant="h6">Pending</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {invoices.filter((inv) => inv.status === "PENDING").length}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9 }}>
                  Invoices Due
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Enrolled Courses */}
        <Paper elevation={3} sx={{ p: 3, mb: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            📚 My Courses
          </Typography>
          {courses.length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Course Code
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Course Name
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Credits</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Semester</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Teacher</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {courses.map((course) => (
                    <TableRow
                      key={course.id}
                      sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                    >
                      <TableCell>
                        <Chip
                          label={course.code}
                          color="primary"
                          size="small"
                        />
                      </TableCell>
                      <TableCell sx={{ fontWeight: "500" }}>
                        {course.name}
                      </TableCell>
                      <TableCell>{course.credits}</TableCell>
                      <TableCell>
                        {course.semester} {course.year}
                      </TableCell>
                      <TableCell>{course.teacherName || "N/A"}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Alert severity="info">
              You are not enrolled in any courses yet. Contact your academic
              advisor to enroll.
            </Alert>
          )}
        </Paper>

        {/* Recent Grades */}
        <Paper elevation={3} sx={{ p: 3, mb: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            🎓 Recent Grades
          </Typography>
          {grades.length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>Course</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Score</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Letter Grade
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Semester</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {grades.slice(0, 5).map((grade) => (
                    <TableRow
                      key={grade.id}
                      sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                    >
                      <TableCell sx={{ fontWeight: "500" }}>
                        {grade.courseName}
                      </TableCell>
                      <TableCell>
                        <Typography
                          sx={{ fontWeight: "bold", color: "#1976d2" }}
                        >
                          {grade.score}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={grade.letterGrade}
                          color={
                            grade.letterGrade.startsWith("A")
                              ? "success"
                              : "primary"
                          }
                          size="small"
                          sx={{ fontWeight: "bold" }}
                        />
                      </TableCell>
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

        {/* Pending Invoices */}
        <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            💳 Pending Invoices
          </Typography>
          {invoices.filter((inv) => inv.status === "PENDING").length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Description
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Amount</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Due Date</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {invoices
                    .filter((inv) => inv.status === "PENDING")
                    .map((invoice) => (
                      <TableRow
                        key={invoice.id}
                        sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                      >
                        <TableCell sx={{ fontWeight: "500" }}>
                          {invoice.description}
                        </TableCell>
                        <TableCell>
                          <Typography
                            sx={{ fontWeight: "bold", color: "#d32f2f" }}
                          >
                            ${invoice.amount.toFixed(2)}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          {new Date(invoice.dueDate).toLocaleDateString()}
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={invoice.status}
                            color="warning"
                            size="small"
                          />
                        </TableCell>
                        <TableCell>
                          <Button
                            variant="contained"
                            size="small"
                            sx={{
                              background:
                                "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                              "&:hover": {
                                background:
                                  "linear-gradient(135deg, #764ba2 0%, #667eea 100%)",
                              },
                            }}
                          >
                            Pay Now
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Alert severity="success">
              ✅ Great! You have no pending invoices at this time.
            </Alert>
          )}
        </Paper>
      </Container>
    </Box>
  );
};
