import React, { useEffect, useState } from "react";
import {
  Container,
  Grid,
  Paper,
  Typography,
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
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  InputAdornment,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import AddIcon from "@mui/icons-material/Add";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
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
  capacity?: number;
  enrolled?: number;
}

export const CoursesPage: React.FC = () => {
  const { user } = useAuth();
  const [courses, setCourses] = useState<Course[]>([]);
  const [enrolledCourses, setEnrolledCourses] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
  const [detailsOpen, setDetailsOpen] = useState(false);

  useEffect(() => {
    loadCourses();
  }, []);

  const loadCourses = async () => {
    try {
      setLoading(true);
      setError("");

      // Load all available courses
      const response = await coursesApi.get("");
      setCourses(response.data.data || response.data || []);

      // Load student's enrolled courses if studentId exists
      if (user?.studentId) {
        try {
          const enrolledResponse = await coursesApi.get(
            `/student/${user.studentId}/enrollments`
          );
          const enrolled =
            enrolledResponse.data.data || enrolledResponse.data || [];
          setEnrolledCourses(enrolled.map((c: Course) => c.id));
        } catch (err) {
          console.warn("Could not load enrolled courses:", err);
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to load courses");
      console.error("Error loading courses:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async (courseId: number) => {
    if (!user?.studentId) {
      setError("Student ID not found");
      return;
    }

    try {
      setError("");
      setSuccess("");

      await coursesApi.post(`/${courseId}/enroll`, {
        studentId: user.studentId,
      });

      // Reload enrollments from server to ensure sync
      try {
        const enrolledResponse = await coursesApi.get(
          `/student/${user.studentId}/enrollments`
        );
        const enrolled =
          enrolledResponse.data.data || enrolledResponse.data || [];
        setEnrolledCourses(enrolled.map((c: Course) => c.id));
      } catch (err) {
        // Fallback to adding courseId if API fails
        setEnrolledCourses([...enrolledCourses, courseId]);
      }

      setSuccess("Successfully enrolled in course!");
      setDetailsOpen(false);

      setTimeout(() => setSuccess(""), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to enroll in course");
    }
  };

  const handleDrop = async (courseId: number) => {
    if (!user?.studentId) {
      setError("Student ID not found");
      return;
    }

    try {
      setError("");
      setSuccess("");

      await coursesApi.delete(`/${courseId}/enroll/${user.studentId}`);

      setEnrolledCourses(enrolledCourses.filter((id) => id !== courseId));
      setSuccess("Successfully dropped course!");

      setTimeout(() => setSuccess(""), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to drop course");
    }
  };

  const openDetails = (course: Course) => {
    setSelectedCourse(course);
    setDetailsOpen(true);
  };

  const filteredCourses = courses.filter(
    (course) =>
      course.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      course.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
      course.teacherName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
            Course Catalog
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Browse and enroll in available courses
          </Typography>
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

        {/* Search Bar */}
        <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
          <TextField
            fullWidth
            placeholder="Search courses by name, code, or instructor..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
        </Paper>

        {/* Enrolled Courses */}
        <Paper elevation={3} sx={{ p: 3, mb: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            📚 My Enrolled Courses ({enrolledCourses.length})
          </Typography>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                  <TableCell sx={{ fontWeight: "bold" }}>Code</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Course Name</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Credits</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Instructor</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Semester</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredCourses
                  .filter((c) => enrolledCourses.includes(c.id))
                  .map((course) => (
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
                      <TableCell>{course.teacherName || "TBA"}</TableCell>
                      <TableCell>
                        {course.semester} {course.year}
                      </TableCell>
                      <TableCell>
                        <Button
                          size="small"
                          variant="outlined"
                          color="error"
                          onClick={() => handleDrop(course.id)}
                        >
                          Drop
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                {enrolledCourses.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      <Typography color="text.secondary" sx={{ py: 3 }}>
                        You are not enrolled in any courses yet
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>

        {/* Available Courses */}
        <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            🔍 Available Courses
          </Typography>
          <Grid container spacing={3}>
            {filteredCourses
              .filter((c) => !enrolledCourses.includes(c.id))
              .map((course) => (
                <Grid item xs={12} md={6} lg={4} key={course.id}>
                  <Paper
                    elevation={2}
                    sx={{
                      p: 3,
                      height: "100%",
                      display: "flex",
                      flexDirection: "column",
                      transition: "transform 0.2s",
                      "&:hover": {
                        transform: "translateY(-4px)",
                        boxShadow: 4,
                      },
                    }}
                  >
                    <Box sx={{ mb: 2 }}>
                      <Chip
                        label={course.code}
                        color="primary"
                        size="small"
                        sx={{ mb: 1 }}
                      />
                      <Typography variant="h6" sx={{ fontWeight: "600" }}>
                        {course.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {course.teacherName || "TBA"}
                      </Typography>
                    </Box>

                    <Box sx={{ mb: 2, flexGrow: 1 }}>
                      <Typography variant="body2" color="text.secondary">
                        Credits: {course.credits} | {course.semester}{" "}
                        {course.year}
                      </Typography>
                    </Box>

                    <Box sx={{ display: "flex", gap: 1 }}>
                      <Button
                        fullWidth
                        variant="outlined"
                        onClick={() => openDetails(course)}
                      >
                        Details
                      </Button>
                      <Button
                        fullWidth
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => handleEnroll(course.id)}
                      >
                        Enroll
                      </Button>
                    </Box>
                  </Paper>
                </Grid>
              ))}
          </Grid>

          {filteredCourses.filter((c) => !enrolledCourses.includes(c.id))
            .length === 0 && (
            <Alert severity="info">
              No available courses found. Try adjusting your search.
            </Alert>
          )}
        </Paper>

        {/* Course Details Dialog */}
        <Dialog
          open={detailsOpen}
          onClose={() => setDetailsOpen(false)}
          maxWidth="sm"
          fullWidth
        >
          {selectedCourse && (
            <>
              <DialogTitle>
                <Box>
                  <Chip
                    label={selectedCourse.code}
                    color="primary"
                    size="small"
                    sx={{ mb: 1 }}
                  />
                  <Typography variant="h5" sx={{ fontWeight: "600" }}>
                    {selectedCourse.name}
                  </Typography>
                </Box>
              </DialogTitle>
              <DialogContent>
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body1" gutterBottom>
                    <strong>Instructor:</strong>{" "}
                    {selectedCourse.teacherName || "TBA"}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    <strong>Credits:</strong> {selectedCourse.credits}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    <strong>Semester:</strong> {selectedCourse.semester}{" "}
                    {selectedCourse.year}
                  </Typography>
                  {selectedCourse.description && (
                    <Typography variant="body1" sx={{ mt: 2 }}>
                      <strong>Description:</strong>
                      <br />
                      {selectedCourse.description}
                    </Typography>
                  )}
                </Box>
              </DialogContent>
              <DialogActions>
                <Button onClick={() => setDetailsOpen(false)}>Close</Button>
                <Button
                  variant="contained"
                  startIcon={<AddIcon />}
                  onClick={() => handleEnroll(selectedCourse.id)}
                >
                  Enroll Now
                </Button>
              </DialogActions>
            </>
          )}
        </Dialog>
      </Container>
    </Box>
  );
};
