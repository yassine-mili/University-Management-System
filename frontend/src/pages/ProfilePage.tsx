import React, { useState, useEffect } from "react";
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
  Avatar,
  Grid,
  Alert,
  CircularProgress,
  Divider,
  Card,
  CardContent,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import CancelIcon from "@mui/icons-material/Cancel";
import PersonIcon from "@mui/icons-material/Person";
import EmailIcon from "@mui/icons-material/Email";
import BadgeIcon from "@mui/icons-material/Badge";
import { useAuth } from "../context/AuthContext";
import { validateEmail, validateRequired } from "../utils/validation";

export const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    username: "",
    studentId: "",
    role: "",
  });
  const [validationErrors, setValidationErrors] = useState<{
    fullName?: string;
    email?: string;
  }>({});

  useEffect(() => {
    if (user) {
      setFormData({
        fullName: user.username || "",
        email: user.email || "",
        username: user.username || "",
        studentId: user.studentId || "",
        role: user.role || "",
      });
    }
  }, [user]);

  const handleEdit = () => {
    setEditing(true);
    setError("");
    setSuccess("");
  };

  const handleCancel = () => {
    setEditing(false);
    setValidationErrors({});
    if (user) {
      setFormData({
        fullName: user.username || "",
        email: user.email || "",
        username: user.username || "",
        studentId: user.studentId || "",
        role: user.role || "",
      });
    }
  };

  const handleSave = async () => {
    setError("");
    setSuccess("");
    setValidationErrors({});

    // Validate
    const errors = {
      fullName: validateRequired(formData.fullName, "Full name"),
      email: validateEmail(formData.email),
    };

    if (errors.fullName || errors.email) {
      setValidationErrors({
        fullName: errors.fullName || undefined,
        email: errors.email || undefined,
      });
      return;
    }

    try {
      setLoading(true);
      // TODO: Implement password change API call

      setSuccess("Profile updated successfully!");
      setEditing(false);
      setTimeout(() => setSuccess(""), 3000);
    } catch (err: any) {
      setError(err.message || "Failed to update profile");
    } finally {
      setLoading(false);
    }
  };

  const getInitials = () => {
    if (user?.username) {
      const parts = user.username.split(" ");
      if (parts.length >= 2) {
        return parts[0][0] + parts[1][0];
      }
      return user.username.substring(0, 2).toUpperCase();
    }
    return "U";
  };

  const getAvatarColor = () => {
    const colors = ["#667eea", "#f093fb", "#4facfe", "#fa709a", "#764ba2"];
    const index = user?.username?.charCodeAt(0) || 0;
    return colors[index % colors.length];
  };

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
      <Container maxWidth="lg">
        <Box sx={{ mb: 4 }}>
          <Typography
            variant="h4"
            gutterBottom
            sx={{ fontWeight: "bold", color: "#1e1e2d" }}
          >
            My Profile
          </Typography>
          <Typography variant="body1" color="text.secondary">
            View and manage your personal information
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

        <Grid container spacing={3}>
          {/* Profile Card */}
          <Grid item xs={12} md={4}>
            <Paper elevation={3} sx={{ p: 4, textAlign: "center" }}>
              <Avatar
                sx={{
                  width: 120,
                  height: 120,
                  margin: "0 auto 16px",
                  bgcolor: getAvatarColor(),
                  fontSize: "3rem",
                  fontWeight: "bold",
                }}
              >
                {getInitials()}
              </Avatar>
              <Typography variant="h5" gutterBottom sx={{ fontWeight: "600" }}>
                {user?.username}
              </Typography>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {user?.email}
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Typography
                  variant="caption"
                  sx={{
                    bgcolor: "#667eea",
                    color: "white",
                    px: 2,
                    py: 0.5,
                    borderRadius: 2,
                    fontWeight: "bold",
                  }}
                >
                  {user?.role}
                </Typography>
              </Box>

              <Divider sx={{ my: 3 }} />

              <Box sx={{ textAlign: "left" }}>
                {user?.role === "STUDENT" && user?.studentId && (
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    gutterBottom
                  >
                    <strong>Student ID:</strong> {user.studentId}
                  </Typography>
                )}
                {user?.role === "TEACHER" && user?.id && (
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    gutterBottom
                  >
                    <strong>Teacher ID:</strong> {user.id}
                  </Typography>
                )}
              </Box>
            </Paper>
          </Grid>

          {/* Edit Form */}
          <Grid item xs={12} md={8}>
            <Paper elevation={3} sx={{ p: 4 }}>
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  mb: 3,
                }}
              >
                <Typography variant="h5" sx={{ fontWeight: "600" }}>
                  Personal Information
                </Typography>
                {!editing && (
                  <Button
                    variant="contained"
                    startIcon={<EditIcon />}
                    onClick={handleEdit}
                    sx={{
                      background:
                        "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    }}
                  >
                    Edit Profile
                  </Button>
                )}
              </Box>

              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Username"
                    value={formData.username}
                    disabled
                    InputProps={{
                      startAdornment: (
                        <PersonIcon sx={{ mr: 1, color: "#999" }} />
                      ),
                    }}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Role"
                    value={formData.role}
                    disabled
                    InputProps={{
                      startAdornment: (
                        <BadgeIcon sx={{ mr: 1, color: "#999" }} />
                      ),
                    }}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Full Name"
                    value={formData.fullName}
                    onChange={(e) =>
                      setFormData({ ...formData, fullName: e.target.value })
                    }
                    disabled={!editing}
                    error={!!validationErrors.fullName}
                    helperText={validationErrors.fullName}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Email"
                    value={formData.email}
                    onChange={(e) =>
                      setFormData({ ...formData, email: e.target.value })
                    }
                    disabled={!editing}
                    error={!!validationErrors.email}
                    helperText={validationErrors.email}
                    InputProps={{
                      startAdornment: (
                        <EmailIcon sx={{ mr: 1, color: "#999" }} />
                      ),
                    }}
                  />
                </Grid>

                {user?.studentId && (
                  <Grid item xs={12} sm={6}>
                    <TextField
                      fullWidth
                      label="Student ID"
                      value={formData.studentId}
                      disabled
                    />
                  </Grid>
                )}

                {editing && (
                  <Grid item xs={12}>
                    <Box
                      sx={{
                        display: "flex",
                        gap: 2,
                        justifyContent: "flex-end",
                      }}
                    >
                      <Button
                        variant="outlined"
                        startIcon={<CancelIcon />}
                        onClick={handleCancel}
                        disabled={loading}
                      >
                        Cancel
                      </Button>
                      <Button
                        variant="contained"
                        startIcon={
                          loading ? (
                            <CircularProgress size={20} />
                          ) : (
                            <SaveIcon />
                          )
                        }
                        onClick={handleSave}
                        disabled={loading}
                        sx={{
                          background:
                            "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                        }}
                      >
                        {loading ? "Saving..." : "Save Changes"}
                      </Button>
                    </Box>
                  </Grid>
                )}
              </Grid>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};
