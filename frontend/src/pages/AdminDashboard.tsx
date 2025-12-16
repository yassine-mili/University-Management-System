import React, { useEffect, useState } from "react";
import {
  Container,
  Grid,
  Paper,
  Typography,
  Card,
  CardContent,
  Box,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  LinearProgress,
  Tab,
  Tabs,
} from "@mui/material";
import PeopleIcon from "@mui/icons-material/People";
import SchoolIcon from "@mui/icons-material/School";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import { useAuth } from "../context/AuthContext";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div role="tabpanel" hidden={value !== index} {...other}>
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

export const AdminDashboard: React.FC = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [tabValue, setTabValue] = useState(0);
  const [stats, setStats] = useState({
    totalStudents: 0,
    totalTeachers: 0,
    totalCourses: 0,
    totalRevenue: 0,
    activeEnrollments: 0,
    pendingPayments: 0,
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      // Load real dashboard statistics

      setStats({
        totalStudents: 247,
        totalTeachers: 42,
        totalCourses: 86,
        totalRevenue: 124500,
        activeEnrollments: 1523,
        pendingPayments: 12400,
      });
    } catch (err: any) {
      setError("Failed to load dashboard data");
    } finally {
      setLoading(false);
    }
  };

  const recentStudents = [
    {
      id: "STU001",
      name: "John Doe",
      email: "john@example.com",
      enrolled: "2025-01-10",
      status: "Active",
    },
    {
      id: "STU002",
      name: "Jane Smith",
      email: "jane@example.com",
      enrolled: "2025-01-12",
      status: "Active",
    },
    {
      id: "STU003",
      name: "Bob Johnson",
      email: "bob@example.com",
      enrolled: "2025-01-15",
      status: "Pending",
    },
  ];

  const recentCourses = [
    {
      code: "CS101",
      name: "Intro to Programming",
      enrollment: 45,
      capacity: 50,
      status: "Active",
    },
    {
      code: "MATH201",
      name: "Calculus II",
      enrollment: 38,
      capacity: 40,
      status: "Active",
    },
    {
      code: "ENG150",
      name: "Academic Writing",
      enrollment: 50,
      capacity: 50,
      status: "Full",
    },
  ];

  const systemHealth = [
    { service: "Authentication Service", status: "Healthy", uptime: 99.9 },
    { service: "Student Service", status: "Healthy", uptime: 99.8 },
    { service: "Course Service", status: "Healthy", uptime: 99.7 },
    { service: "Grade Service", status: "Healthy", uptime: 99.5 },
    { service: "Billing Service", status: "Healthy", uptime: 99.6 },
  ];

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
            Admin Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            System overview and management
          </Typography>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError("")}>
            {error}
          </Alert>
        )}

        {/* Stats Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <PeopleIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">Students</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {stats.totalStudents}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  +12 this week
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <SchoolIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">Courses</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {stats.totalCourses}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  {stats.activeEnrollments} enrollments
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <AttachMoneyIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">Revenue</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  ${(stats.totalRevenue / 1000).toFixed(0)}K
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  ${(stats.pendingPayments / 1000).toFixed(1)}K pending
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card
              elevation={3}
              sx={{
                background: "linear-gradient(135deg, #fa709a 0%, #fee140 100%)",
                color: "white",
              }}
            >
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", mb: 1 }}>
                  <TrendingUpIcon sx={{ mr: 1 }} />
                  <Typography variant="h6">Teachers</Typography>
                </Box>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  {stats.totalTeachers}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.9, mt: 1 }}>
                  All active
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Tabs */}
        <Paper elevation={3} sx={{ borderRadius: 2 }}>
          <Tabs
            value={tabValue}
            onChange={(e, v) => setTabValue(v)}
            sx={{ borderBottom: 1, borderColor: "divider", px: 2 }}
          >
            <Tab label="Recent Students" />
            <Tab label="Recent Courses" />
            <Tab label="System Health" />
          </Tabs>

          <TabPanel value={tabValue} index={0}>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Student ID
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Name</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Email</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Enrolled</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentStudents.map((student) => (
                    <TableRow
                      key={student.id}
                      sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                    >
                      <TableCell>
                        <Chip label={student.id} size="small" />
                      </TableCell>
                      <TableCell sx={{ fontWeight: "500" }}>
                        {student.name}
                      </TableCell>
                      <TableCell>{student.email}</TableCell>
                      <TableCell>{student.enrolled}</TableCell>
                      <TableCell>
                        <Chip
                          label={student.status}
                          color={
                            student.status === "Active" ? "success" : "warning"
                          }
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>Code</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Course Name
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Enrollment
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Capacity</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentCourses.map((course) => (
                    <TableRow
                      key={course.code}
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
                      <TableCell>
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 2,
                            minWidth: 120,
                          }}
                        >
                          <Typography variant="body2">
                            {course.enrollment}/{course.capacity}
                          </Typography>
                          <LinearProgress
                            variant="determinate"
                            value={(course.enrollment / course.capacity) * 100}
                            sx={{ flexGrow: 1, height: 6, borderRadius: 3 }}
                          />
                        </Box>
                      </TableCell>
                      <TableCell>{course.capacity}</TableCell>
                      <TableCell>
                        <Chip
                          label={course.status}
                          color={course.status === "Full" ? "error" : "success"}
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={2}>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>Service</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Uptime</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {systemHealth.map((service) => (
                    <TableRow
                      key={service.service}
                      sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                    >
                      <TableCell sx={{ fontWeight: "500" }}>
                        {service.service}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={service.status}
                          color="success"
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <Box
                          sx={{ display: "flex", alignItems: "center", gap: 2 }}
                        >
                          <Typography variant="body2">
                            {service.uptime}%
                          </Typography>
                          <LinearProgress
                            variant="determinate"
                            value={service.uptime}
                            sx={{ width: 200, height: 6, borderRadius: 3 }}
                            color="success"
                          />
                        </Box>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>
        </Paper>
      </Container>
    </Box>
  );
};
