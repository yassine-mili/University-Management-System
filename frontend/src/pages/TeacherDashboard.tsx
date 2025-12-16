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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  IconButton,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import GroupIcon from "@mui/icons-material/Group";
import MenuBookIcon from "@mui/icons-material/MenuBook";
import AssignmentIcon from "@mui/icons-material/Assignment";
import { useAuth } from "../context/AuthContext";
import { coursesApi } from "../services/api";

interface Course {
  id: number;
  code: string;
  name: string;
  credits: number;
  semester: string;
  year: number;
  teacherName: string;
  description?: string;
  maxStudents?: number;
  enrolledStudents?: number;
}

export const TeacherDashboard: React.FC = () => {
  const { user } = useAuth();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [addDialogOpen, setAddDialogOpen] = useState(false);
  const [newCourse, setNewCourse] = useState({
    code: "",
    name: "",
    description: "",
    credits: 3,
    semester: "Fall",
    year: 2025,
    maxStudents: 30,
  });

  useEffect(() => {
    loadCourses();
  }, []);

  const loadCourses = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await coursesApi.get("");
      const allCourses = response.data || [];

      // Map the course data from backend to frontend format
      const mappedCourses = allCourses.map((course: any) => ({
        id: course.id?.toString() || course.code,
        code: course.code,
        name: course.name,
        description: course.description,
        credits: course.credits,
        semester: course.semester?.split(" ")[0] || "Fall",
        year: parseInt(course.semester?.split(" ")[1]) || 2024,
        teacherName: user?.username || "Teacher",
        maxStudents: course.capacity || 30,
        enrolledStudents: course.enrolledCount || 0,
        status: course.isActive ? "ACTIVE" : "INACTIVE",
      }));

      setCourses(mappedCourses);
    } catch (err: any) {
      setError(err.message || "Failed to load courses");
      console.error("Error in loadCourses:", err);
      setCourses([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddCourse = async () => {
    try {
      setError("");
      setSuccess("");

      // Create course via API
      await coursesApi.post("", {
        code: newCourse.code,
        name: newCourse.name,
        description: newCourse.description,
        credits: newCourse.credits,
        semester: `${newCourse.semester} ${newCourse.year}`,
        capacity: newCourse.maxStudents,
        prerequisites: "",
        enrollmentStartDate: new Date().toISOString().split("T")[0],
        enrollmentEndDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
          .toISOString()
          .split("T")[0],
      });

      setSuccess("Course created successfully!");
      setAddDialogOpen(false);
      setNewCourse({
        code: "",
        name: "",
        description: "",
        credits: 3,
        semester: "Fall",
        year: 2025,
        maxStudents: 30,
      });
      loadCourses();

      setTimeout(() => setSuccess(""), 3000);
    } catch (err: any) {
      setError(err.message || "Failed to create course");
    }
  };

  const stats = {
    totalCourses: courses.length,
    totalStudents: courses.reduce(
      (sum, c) => sum + (c.enrolledStudents || 0),
      0
    ),
    avgEnrollment:
      courses.length > 0
        ? Math.round(
            courses.reduce((sum, c) => sum + (c.enrolledStudents || 0), 0) /
              courses.length
          )
        : 0,
  };

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
        <Box
          sx={{
            mb: 4,
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Box>
            <Typography
              variant="h4"
              gutterBottom
              sx={{ fontWeight: "bold", color: "#1e1e2d" }}
            >
              Teacher Dashboard
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Welcome back, {user?.username}!
            </Typography>
          </Box>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setAddDialogOpen(true)}
            sx={{
              background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            }}
          >
            Create Course
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError("")}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert
            severity="success"
            sx={{ mb: 3 }}
            onClose={() => setSuccess("")}
          >
            {success}
          </Alert>
        )}

        {/* Stats Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} md={4}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <MenuBookIcon sx={{ mr: 1, fontSize: 40 }} />
                  <Typography variant="h6">My Courses</Typography>
                </Box>
                <Typography variant="h2" sx={{ fontWeight: "bold" }}>
                  {stats.totalCourses}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  Active this semester
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #fa709a 0%, #fee140 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <GroupIcon sx={{ mr: 1, fontSize: 40 }} />
                  <Typography variant="h6">Total Students</Typography>
                </Box>
                <Typography variant="h2" sx={{ fontWeight: "bold" }}>
                  {stats.totalStudents}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  Across all courses
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <AssignmentIcon sx={{ mr: 1, fontSize: 40 }} />
                  <Typography variant="h6">Avg Enrollment</Typography>
                </Box>
                <Typography variant="h2" sx={{ fontWeight: "bold" }}>
                  {stats.avgEnrollment}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  Students per course
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Courses Table */}
        <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            📚 My Courses
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                  <TableCell sx={{ fontWeight: "bold" }}>Code</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Course Name</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Credits</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Semester</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Enrollment</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {courses.map((course) => (
                  <TableRow
                    key={course.id}
                    sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                  >
                    <TableCell>
                      <Chip label={course.code} color="primary" size="small" />
                    </TableCell>
                    <TableCell sx={{ fontWeight: "500" }}>
                      {course.name}
                    </TableCell>
                    <TableCell>{course.credits}</TableCell>
                    <TableCell>
                      {course.semester} {course.year}
                    </TableCell>
                    <TableCell>
                      <Box
                        sx={{ display: "flex", alignItems: "center", gap: 1 }}
                      >
                        <Typography variant="body2">
                          {course.enrolledStudents || 0} /{" "}
                          {course.maxStudents || 30}
                        </Typography>
                        <Chip
                          label={`${Math.round(
                            ((course.enrolledStudents || 0) /
                              (course.maxStudents || 30)) *
                              100
                          )}%`}
                          size="small"
                          color={
                            (course.enrolledStudents || 0) /
                              (course.maxStudents || 30) >
                            0.8
                              ? "error"
                              : "success"
                          }
                        />
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: "flex", gap: 1 }}>
                        <IconButton size="small" color="primary">
                          <EditIcon fontSize="small" />
                        </IconButton>
                        <IconButton size="small" color="error">
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          {courses.length === 0 && (
            <Alert severity="info" sx={{ mt: 2 }}>
              No courses yet. Create your first course to get started!
            </Alert>
          )}
        </Paper>

        {/* Add Course Dialog */}
        <Dialog
          open={addDialogOpen}
          onClose={() => setAddDialogOpen(false)}
          maxWidth="md"
          fullWidth
        >
          <DialogTitle>Create New Course</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Course Code"
                  placeholder="CS101"
                  value={newCourse.code}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, code: e.target.value })
                  }
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Credits"
                  type="number"
                  value={newCourse.credits}
                  onChange={(e) =>
                    setNewCourse({
                      ...newCourse,
                      credits: parseInt(e.target.value),
                    })
                  }
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Course Name"
                  placeholder="Introduction to Computer Science"
                  value={newCourse.name}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, name: e.target.value })
                  }
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  label="Description"
                  value={newCourse.description}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, description: e.target.value })
                  }
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  select
                  label="Semester"
                  value={newCourse.semester}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, semester: e.target.value })
                  }
                >
                  <MenuItem value="Fall">Fall</MenuItem>
                  <MenuItem value="Spring">Spring</MenuItem>
                  <MenuItem value="Summer">Summer</MenuItem>
                </TextField>
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Year"
                  type="number"
                  value={newCourse.year}
                  onChange={(e) =>
                    setNewCourse({
                      ...newCourse,
                      year: parseInt(e.target.value),
                    })
                  }
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  label="Max Students"
                  type="number"
                  value={newCourse.maxStudents}
                  onChange={(e) =>
                    setNewCourse({
                      ...newCourse,
                      maxStudents: parseInt(e.target.value),
                    })
                  }
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setAddDialogOpen(false)}>Cancel</Button>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleAddCourse}
              disabled={!newCourse.code || !newCourse.name}
            >
              Create Course
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
};
