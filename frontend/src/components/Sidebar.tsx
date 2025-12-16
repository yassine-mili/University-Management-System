import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Divider,
  Avatar,
  IconButton,
  Toolbar,
} from "@mui/material";
import DashboardIcon from "@mui/icons-material/Dashboard";
import SchoolIcon from "@mui/icons-material/School";
import GradeIcon from "@mui/icons-material/Grade";
import PaymentIcon from "@mui/icons-material/Payment";
import PersonIcon from "@mui/icons-material/Person";
import SettingsIcon from "@mui/icons-material/Settings";
import LogoutIcon from "@mui/icons-material/Logout";
import MenuIcon from "@mui/icons-material/Menu";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import { useAuth } from "../context/AuthContext";

const drawerWidth = 260;

interface SidebarProps {
  children: React.ReactNode;
}

export const Sidebar: React.FC<SidebarProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(true);

  const handleDrawerToggle = () => {
    setOpen(!open);
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const getRoleBasedMenuItems = () => {
    const baseItems = [
      { text: "Dashboard", icon: <DashboardIcon />, path: "/dashboard" },
    ];

    if (user?.role === "STUDENT") {
      return [
        ...baseItems,
        { text: "Courses", icon: <SchoolIcon />, path: "/courses" },
        { text: "Grades", icon: <GradeIcon />, path: "/grades" },
        { text: "Billing", icon: <PaymentIcon />, path: "/billing" },
        { text: "Profile", icon: <PersonIcon />, path: "/profile" },
      ];
    } else if (user?.role === "TEACHER") {
      return [
        ...baseItems,
        { text: "My Courses", icon: <SchoolIcon />, path: "/teacher" },
        { text: "Students", icon: <PersonIcon />, path: "/students" },
        { text: "Profile", icon: <PersonIcon />, path: "/profile" },
      ];
    } else if (user?.role === "ADMIN") {
      return [
        ...baseItems,
        { text: "Students", icon: <PersonIcon />, path: "/students" },
        { text: "Teachers", icon: <SchoolIcon />, path: "/teachers" },
        { text: "Courses", icon: <SchoolIcon />, path: "/courses" },
        { text: "Profile", icon: <PersonIcon />, path: "/profile" },
      ];
    }

    return baseItems;
  };

  const menuItems = getRoleBasedMenuItems();

  return (
    <Box sx={{ display: "flex" }}>
      <Drawer
        variant="permanent"
        open={open}
        sx={{
          width: open ? drawerWidth : 64,
          flexShrink: 0,
          transition: "width 0.3s",
          "& .MuiDrawer-paper": {
            width: open ? drawerWidth : 64,
            boxSizing: "border-box",
            transition: "width 0.3s",
            backgroundColor: "#1e1e2d",
            color: "white",
            borderRight: "none",
          },
        }}
      >
        <Toolbar
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: open ? "space-between" : "center",
            px: open ? 2 : 1,
            py: 2,
          }}
        >
          {open && (
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <SchoolIcon sx={{ color: "#667eea", fontSize: 32 }} />
              <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                UniMS
              </Typography>
            </Box>
          )}
          <IconButton onClick={handleDrawerToggle} sx={{ color: "white" }}>
            {open ? <ChevronLeftIcon /> : <MenuIcon />}
          </IconButton>
        </Toolbar>

        <Divider sx={{ backgroundColor: "rgba(255,255,255,0.1)" }} />

        {/* User Info */}
        {open && (
          <Box sx={{ p: 2, textAlign: "center" }}>
            <Avatar
              sx={{
                width: 64,
                height: 64,
                mx: "auto",
                mb: 1,
                bgcolor: "#667eea",
                fontSize: 28,
              }}
            >
              {user?.username?.charAt(0).toUpperCase()}
            </Avatar>
            <Typography variant="body1" sx={{ fontWeight: "600", mb: 0.5 }}>
              {user?.username}
            </Typography>
            <Typography
              variant="caption"
              sx={{ color: "rgba(255,255,255,0.6)" }}
            >
              {user?.role?.replace("ROLE_", "")}
            </Typography>
          </Box>
        )}

        <Divider sx={{ backgroundColor: "rgba(255,255,255,0.1)", my: 2 }} />

        {/* Navigation Menu */}
        <List sx={{ px: 1 }}>
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
                <ListItemButton
                  onClick={() => navigate(item.path)}
                  sx={{
                    borderRadius: 2,
                    backgroundColor: isActive
                      ? "rgba(102, 126, 234, 0.2)"
                      : "transparent",
                    color: isActive ? "#667eea" : "rgba(255,255,255,0.8)",
                    "&:hover": {
                      backgroundColor: "rgba(102, 126, 234, 0.1)",
                    },
                    justifyContent: open ? "initial" : "center",
                    px: open ? 2 : 1.5,
                  }}
                >
                  <ListItemIcon
                    sx={{
                      minWidth: 0,
                      mr: open ? 2 : 0,
                      justifyContent: "center",
                      color: isActive ? "#667eea" : "rgba(255,255,255,0.8)",
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  {open && <ListItemText primary={item.text} />}
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>

        <Box sx={{ flexGrow: 1 }} />

        {/* Bottom Actions */}
        <List sx={{ px: 1, pb: 2 }}>
          <ListItem disablePadding>
            <ListItemButton
              onClick={handleLogout}
              sx={{
                borderRadius: 2,
                color: "#f5576c",
                "&:hover": {
                  backgroundColor: "rgba(245, 87, 108, 0.1)",
                },
                justifyContent: open ? "initial" : "center",
                px: open ? 2 : 1.5,
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 0,
                  mr: open ? 2 : 0,
                  justifyContent: "center",
                  color: "#f5576c",
                }}
              >
                <LogoutIcon />
              </ListItemIcon>
              {open && <ListItemText primary="Logout" />}
            </ListItemButton>
          </ListItem>
        </List>
      </Drawer>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          minHeight: "100vh",
          backgroundColor: "#f5f5f5",
          transition: "margin 0.3s",
        }}
      >
        {children}
      </Box>
    </Box>
  );
};
