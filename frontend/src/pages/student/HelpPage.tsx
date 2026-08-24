import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  TextField,
  Button,
  Grid,
  Alert,
  Tab,
  Tabs,
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import SendIcon from '@mui/icons-material/Send';
import PageHeader from '../../components/common/PageHeader';
import { supportTicketService } from '../../services';

const faqs = [
  { id: 1, question: 'How do I take an exam?', answer: 'Navigate to My Exams, select an available exam, and click "Start Exam". Ensure you have a stable internet connection and a supported browser.' },
  { id: 2, question: 'Can I retake an exam?', answer: 'This depends on the exam settings configured by your instructor. Check the exam details for the maximum attempts allowed.' },
  { id: 3, question: 'How are my results calculated?', answer: 'MCQ and True/False questions are auto-graded immediately. Descriptive questions are graded by your faculty.' },
  { id: 4, question: 'How do I view my certificates?', answer: 'Go to the My Certificates page in your dashboard to view and download all your earned certificates.' },
  { id: 5, question: 'What is adaptive testing?', answer: 'Adaptive testing adjusts question difficulty based on your performance, providing a personalized assessment experience.' },
  { id: 6, question: 'How do recommendations work?', answer: 'Our AI analyzes your performance across subjects and topics to generate personalized study recommendations and identify weak areas.' },
];

const HelpPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  // Support ticket form
  const [ticketSubject, setTicketSubject] = useState('');
  const [ticketDescription, setTicketDescription] = useState('');
  const [ticketPriority, setTicketPriority] = useState('MEDIUM');

  const handleCreateTicket = async () => {
    if (!ticketSubject || !ticketDescription) {
      setError('Please fill all required fields');
      return;
    }
    try {
      await supportTicketService.create({
        subject: ticketSubject,
        description: ticketDescription,
        priority: ticketPriority,
      });
      setSuccess('Support ticket created successfully. Our team will respond shortly.');
      setTicketSubject('');
      setTicketDescription('');
    } catch {
      setError('Failed to create support ticket');
    }
  };

  return (
    <Box>
      <PageHeader title="Help & Support" subtitle="Find answers and get help" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
            <Tab label="FAQ" />
            <Tab label="Contact Admin / Create Ticket" />
          </Tabs>

          {tabValue === 0 && (
            <Box sx={{ pt: 2 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>Frequently Asked Questions</Typography>
              {faqs.map((faq) => (
                <Accordion key={faq.id}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography fontWeight={600}>{faq.question}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Typography color="text.secondary">{faq.answer}</Typography>
                  </AccordionDetails>
                </Accordion>
              ))}
            </Box>
          )}

          {tabValue === 1 && (
            <Box sx={{ pt: 2, maxWidth: 600 }}>
              <Typography variant="h6" sx={{ mb: 2 }}>Contact Admin</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Can't find what you're looking for? Create a support ticket and our team will assist you.
              </Typography>
              <TextField
                label="Subject"
                value={ticketSubject}
                onChange={(e) => setTicketSubject(e.target.value)}
                fullWidth
                sx={{ mb: 2 }}
                required
              />
              <TextField
                label="Description"
                value={ticketDescription}
                onChange={(e) => setTicketDescription(e.target.value)}
                fullWidth
                multiline
                rows={4}
                sx={{ mb: 2 }}
                required
                placeholder="Describe your issue in detail..."
              />
              <TextField
                select
                label="Priority"
                value={ticketPriority}
                onChange={(e) => setTicketPriority(e.target.value)}
                fullWidth
                sx={{ mb: 2 }}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </TextField>
              <Button variant="contained" startIcon={<SendIcon />} onClick={handleCreateTicket}>
                Submit Ticket
              </Button>
            </Box>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default HelpPage;
