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
  Button,
  Card,
  CardContent,
  Grid,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from "@mui/material";
import PaymentIcon from "@mui/icons-material/Payment";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import PendingIcon from "@mui/icons-material/Pending";
import CancelIcon from "@mui/icons-material/Cancel";
import { useAuth } from "../context/AuthContext";
import { billingService } from "../services/billing.service";
import { Invoice } from "../types";

export const BillingPage: React.FC = () => {
  const { user } = useAuth();
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [paymentDialogOpen, setPaymentDialogOpen] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [paymentProcessing, setPaymentProcessing] = useState(false);

  useEffect(() => {
    loadInvoices();
  }, []);

  const loadInvoices = async () => {
    if (!user?.studentId) {
      setError("Student ID not found");
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const invoicesData = await billingService.getInvoicesByStudent(
        user.studentId
      );
      setInvoices(invoicesData);
    } catch (err: any) {
      setError(err.message || "Failed to load invoices");
      console.error("Error loading invoices:", err);
    } finally {
      setLoading(false);
    }
  };

  const openPaymentDialog = (invoice: Invoice) => {
    setSelectedInvoice(invoice);
    setPaymentDialogOpen(true);
  };

  const closePaymentDialog = () => {
    setPaymentDialogOpen(false);
    setSelectedInvoice(null);
    setPaymentMethod("");
  };

  const processPayment = async () => {
    if (!selectedInvoice || !paymentMethod.trim()) {
      setError("Please enter a payment method");
      return;
    }

    try {
      setPaymentProcessing(true);
      setError("");

      // TODO: Implement payment processing API call

      // Update invoice status
      const updatedInvoices = invoices.map((inv) =>
        inv.id === selectedInvoice.id
          ? { ...inv, status: "PAID" as const }
          : inv
      );
      setInvoices(updatedInvoices);

      setSuccess(
        `Payment of $${selectedInvoice.amount.toFixed(
          2
        )} processed successfully!`
      );
      closePaymentDialog();

      setTimeout(() => setSuccess(""), 5000);
    } catch (err: any) {
      setError("Payment processing failed. Please try again.");
    } finally {
      setPaymentProcessing(false);
    }
  };

  const calculateTotals = () => {
    const total = invoices.reduce((sum, inv) => sum + inv.amount, 0);
    const pending = invoices
      .filter((inv) => inv.status === "PENDING")
      .reduce((sum, inv) => sum + inv.amount, 0);
    const paid = invoices
      .filter((inv) => inv.status === "PAID")
      .reduce((sum, inv) => sum + inv.amount, 0);

    return { total, pending, paid };
  };

  const totals = calculateTotals();

  const getStatusIcon = (status: string): JSX.Element => {
    switch (status) {
      case "PAID":
        return <CheckCircleIcon sx={{ color: "#4caf50" }} />;
      case "PENDING":
        return <PendingIcon sx={{ color: "#ff9800" }} />;
      case "OVERDUE":
        return <CancelIcon sx={{ color: "#f44336" }} />;
      default:
        return <PendingIcon />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case "PAID":
        return "success";
      case "PENDING":
        return "warning";
      case "OVERDUE":
        return "error";
      default:
        return "default";
    }
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
        <Box sx={{ mb: 4 }}>
          <Typography
            variant="h4"
            gutterBottom
            sx={{ fontWeight: "bold", color: "#1e1e2d" }}
          >
            Billing & Payments
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your invoices and payment history
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

        {/* Summary Cards */}
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
                <Typography variant="h6" gutterBottom>
                  Total Billed
                </Typography>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  ${totals.total.toFixed(2)}
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
                <Typography variant="h6" gutterBottom>
                  Pending
                </Typography>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  ${totals.pending.toFixed(2)}
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
                <Typography variant="h6" gutterBottom>
                  Paid
                </Typography>
                <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                  ${totals.paid.toFixed(2)}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Pending Invoices */}
        <Paper elevation={3} sx={{ p: 3, mb: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            ⚠️ Pending Payments
          </Typography>

          {invoices.filter((inv) => inv.status === "PENDING").length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>Invoice #</TableCell>
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
                        <TableCell>#{invoice.id}</TableCell>
                        <TableCell sx={{ fontWeight: "500" }}>
                          {invoice.description}
                        </TableCell>
                        <TableCell>
                          <Typography
                            sx={{ fontWeight: "bold", color: "#f44336" }}
                          >
                            ${invoice.amount.toFixed(2)}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          {new Date(invoice.dueDate).toLocaleDateString()}
                        </TableCell>
                        <TableCell>
                          <Chip
                            icon={getStatusIcon(invoice.status)}
                            label={invoice.status}
                            color={getStatusColor(invoice.status) as any}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>
                          <Button
                            variant="contained"
                            size="small"
                            startIcon={<PaymentIcon />}
                            onClick={() => openPaymentDialog(invoice)}
                            sx={{
                              background:
                                "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
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
              ✅ Great! You have no pending invoices.
            </Alert>
          )}
        </Paper>

        {/* Payment History */}
        <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
          <Typography
            variant="h5"
            gutterBottom
            sx={{ fontWeight: "600", mb: 3 }}
          >
            📋 All Invoices
          </Typography>

          {invoices.length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>Invoice #</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Description
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Amount</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Created Date
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Due Date</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {invoices.map((invoice) => (
                    <TableRow
                      key={invoice.id}
                      sx={{ "&:hover": { bgcolor: "#f9f9f9" } }}
                    >
                      <TableCell>#{invoice.id}</TableCell>
                      <TableCell>{invoice.description}</TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>
                        ${invoice.amount.toFixed(2)}
                      </TableCell>
                      <TableCell>
                        {new Date(invoice.createdAt).toLocaleDateString()}
                      </TableCell>
                      <TableCell>
                        {new Date(invoice.dueDate).toLocaleDateString()}
                      </TableCell>
                      <TableCell>
                        <Chip
                          icon={getStatusIcon(invoice.status)}
                          label={invoice.status}
                          color={getStatusColor(invoice.status) as any}
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Alert severity="info">No invoices found.</Alert>
          )}
        </Paper>

        {/* Payment Dialog */}
        <Dialog
          open={paymentDialogOpen}
          onClose={closePaymentDialog}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Process Payment</DialogTitle>
          <DialogContent>
            {selectedInvoice && (
              <Box sx={{ pt: 2 }}>
                <Typography variant="h6" gutterBottom>
                  Invoice Details
                </Typography>
                <Typography variant="body1" gutterBottom>
                  <strong>Description:</strong> {selectedInvoice.description}
                </Typography>
                <Typography variant="body1" gutterBottom>
                  <strong>Amount:</strong> ${selectedInvoice.amount.toFixed(2)}
                </Typography>
                <Typography variant="body1" gutterBottom sx={{ mb: 3 }}>
                  <strong>Due Date:</strong>{" "}
                  {new Date(selectedInvoice.dueDate).toLocaleDateString()}
                </Typography>

                <TextField
                  fullWidth
                  label="Payment Method (Card Number)"
                  placeholder="4111 1111 1111 1111"
                  value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  disabled={paymentProcessing}
                  sx={{ mb: 2 }}
                />

                <Alert severity="info" sx={{ mt: 2 }}>
                  This is a demo. Any card number will work.
                </Alert>
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={closePaymentDialog} disabled={paymentProcessing}>
              Cancel
            </Button>
            <Button
              variant="contained"
              onClick={processPayment}
              disabled={paymentProcessing || !paymentMethod.trim()}
              startIcon={
                paymentProcessing ? (
                  <CircularProgress size={20} />
                ) : (
                  <PaymentIcon />
                )
              }
            >
              {paymentProcessing ? "Processing..." : "Pay Now"}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
};
