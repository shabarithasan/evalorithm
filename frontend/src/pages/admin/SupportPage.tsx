import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Grid,
  Alert,
  Tab,
  Tabs,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AddIcon from '@mui/icons-material/Add';
import PageHeader from '../../components/common/PageHeader';
import LoadingScreen from '../../components/common/LoadingScreen';
import { supportTicketService } from '../../services';
import { SupportTicket, FAQ, SupportTicketStatusValue } from '../../types';

const faqs: FAQ[] = [
  { id: 1, question: 'How do I reset my password?', answer: 'Go to the login page and click "Forgot Password". Enter your email and follow the instructions sent to your inbox.', category: 'Account' },
  { id: 2, question: 'How do I take an exam?', answer: 'Navigate to My Exams, select an available exam, and click "Start Exam". Make sure you have a stable internet connection.', category: 'Exams' },
  { id: 3, question: 'How are my results calculated?', answer: 'Results are auto-evaluated for objective questions. MCQ and True/False are graded immediately. Descriptive answers are graded by faculty.', category: 'Results' },
  { id: 4, question: 'Can I retake an exam?', answer: 'This depends on the exam settings configured by your faculty/admin. Some exams allow multiple attempts.', category: 'Exams' },
  { id: 5, question: 'How do I view my certificates?', answer: 'Go to the Certificates page in your dashboard to view and download all earned certificates.', category: 'Certificates' },
];

const SupportPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [tickets, setTickets] = useState<SupportTicket[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Create ticket dialog
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [ticketForm, setTicketForm] = useState({ subject: '', description: '', priority: 'MEDIUM' });

  // Ticket detail dialog
  const [detailDialog, setDetailDialog] = useState<SupportTicket | null>(null);

  useEffect(() => {
    fetchTickets();
  }, []);

  const fetchTickets = async () => {
    try {
      const response = await supportTicketService.getAll();
      if (response.success) {
        setTickets(response.data?.content || response.data || []);
      }
    } catch {
      setError('Failed to load tickets');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTicket = async () => {
    if (!ticketForm.subject || !ticketForm.description) {
      setError('Please fill all required fields');
      return;
    }
    try {
      await supportTicketService.create(ticketForm);
      setCreateDialogOpen(false);
      setTicketForm({ subject: '', description: '', priority: 'MEDIUM' });
      setSuccess('Ticket created successfully');
      fetchTickets();
    } catch {
      setError('Failed to create ticket');
    }
  };

  const getStatusColor = (status: SupportTicketStatusValue): 'success' | 'warning' | 'error' | 'info' => {
    switch (status) {
      case 'OPEN': return 'info';
      case 'IN_PROGRESS': return 'warning';
      case 'RESOLVED': return 'success';
      case 'CLOSED': return 'default' as any;
      default: return 'info';
    }
  };

  if (loading) return <LoadingScreen />;

  return (
    <Box>
      <PageHeader title="Help & Support" subtitle="FAQs and support tickets" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
            <Tab label="FAQ" />
            <Tab label="Support Tickets" />
          </Tabs>

          {tabValue === 0 && (
            <Box sx={{ pt: 2 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>Frequently Asked Questions</Typography>
              {faqs.map((faq) => (
                <Accordion key={faq.id}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Typography fontWeight={600}>{faq.question}</Typography>
                      <Chip label={faq.category} size="small" color="primary" variant="outlined" />
                    </Box>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Typography color="text.secondary">{faq.answer}</Typography>
                  </AccordionDetails>
                </Accordion>
              ))}
            </Box>
          )}

          {tabValue === 1 && (
            <Box sx={{ pt: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreateDialogOpen(true)}>
                  Create Ticket
                </Button>
              </Box>
              <TableContainer component={Paper} variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow sx={{ backgroundColor: 'primary.main' }}>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>ID</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Subject</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Status</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Priority</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Assigned To</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }}>Created</TableCell>
                      <TableCell sx={{ color: '#fff', fontWeight: 600 }} align="right">Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {tickets.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={7} align="center">
                          <Typography color="text.secondary" sx={{ py: 2 }}>No support tickets</Typography>
                        </TableCell>
                      </TableRow>
                    ) : (
                      tickets.map((ticket) => (
                        <TableRow key={ticket.id} hover>
                          <TableCell>#{ticket.id}</TableCell>
                          <TableCell>{ticket.subject}</TableCell>
                          <TableCell><Chip label={ticket.status} size="small" color={getStatusColor(ticket.status)} /></TableCell>
                          <TableCell><Chip label={ticket.priority} size="small" variant="outlined" /></TableCell>
                          <TableCell>{ticket.assignedToName || 'Unassigned'}</TableCell>
                          <TableCell>{new Date(ticket.createdAt).toLocaleDateString()}</TableCell>
                          <TableCell align="right">
                            <Button size="small" onClick={() => setDetailDialog(ticket)}>View</Button>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </Box>
          )}
        </CardContent>
      </Card>

      {/* Create Ticket Dialog */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Support Ticket</DialogTitle>
        <DialogContent sx={{ pt: '16px !important' }}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                label="Subject"
                value={ticketForm.subject}
                onChange={(e) => setTicketForm({ ...ticketForm, subject: e.target.value })}
                fullWidth
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Description"
                value={ticketForm.description}
                onChange={(e) => setTicketForm({ ...ticketForm, description: e.target.value })}
                fullWidth
                required
                multiline
                rows={4}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                label="Priority"
                value={ticketForm.priority}
                onChange={(e) => setTicketForm({ ...ticketForm, priority: e.target.value })}
                fullWidth
              >
                <MenuItem value="LOW">Low</MenuItem>
                <MenuItem value="MEDIUM">Medium</MenuItem>
                <MenuItem value="HIGH">High</MenuItem>
                <MenuItem value="URGENT">Urgent</MenuItem>
              </TextField>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreateTicket}>Create</Button>
        </DialogActions>
      </Dialog>

      {/* Detail Dialog */}
      <Dialog open={!!detailDialog} onClose={() => setDetailDialog(null)} maxWidth="sm" fullWidth>
        <DialogTitle>Ticket #{detailDialog?.id} - {detailDialog?.subject}</DialogTitle>
        <DialogContent>
          {detailDialog && (
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography variant="caption" color="text.secondary">Status</Typography>
                <Chip label={detailDialog.status} size="small" color={getStatusColor(detailDialog.status)} sx={{ ml: 1 }} />
              </Grid>
              <Grid item xs={6}>
                <Typography variant="caption" color="text.secondary">Priority</Typography>
                <Chip label={detailDialog.priority} size="small" variant="outlined" sx={{ ml: 1 }} />
              </Grid>
              <Grid item xs={12}>
                <Typography variant="caption" color="text.secondary">Description</Typography>
                <Typography variant="body2" sx={{ mt: 0.5 }}>{detailDialog.description}</Typography>
              </Grid>
              {detailDialog.resolution && (
                <Grid item xs={12}>
                  <Typography variant="caption" color="text.secondary">Resolution</Typography>
                  <Typography variant="body2" sx={{ mt: 0.5 }}>{detailDialog.resolution}</Typography>
                </Grid>
              )}
              <Grid item xs={6}>
                <Typography variant="caption" color="text.secondary">Created</Typography>
                <Typography variant="body2">{new Date(detailDialog.createdAt).toLocaleString()}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="caption" color="text.secondary">Assigned To</Typography>
                <Typography variant="body2">{detailDialog.assignedToName || 'Unassigned'}</Typography>
              </Grid>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailDialog(null)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default SupportPage;
