import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  CardActionArea,
  Typography,
  Grid,
  Dialog,
  DialogTitle,
  DialogContent,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  Divider,
} from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import PeopleIcon from '@mui/icons-material/People';
import BusinessIcon from '@mui/icons-material/Business';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import QuizIcon from '@mui/icons-material/Quiz';
import AssignmentIcon from '@mui/icons-material/Assignment';
import AssessmentIcon from '@mui/icons-material/Assessment';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import PageHeader from '../../components/common/PageHeader';
import ReportGenerator from '../../components/reports/ReportGenerator';
import { ReportType } from '../../types';

const reportCards: { type: ReportType; title: string; description: string; icon: React.ReactElement; color: string }[] = [
  { type: 'STUDENT', title: 'Student Report', description: 'Individual student performance and progress', icon: <PersonIcon />, color: '#1565C0' },
  { type: 'FACULTY', title: 'Faculty Report', description: 'Faculty performance and assigned subjects', icon: <PeopleIcon />, color: '#2E7D32' },
  { type: 'DEPARTMENT', title: 'Department Report', description: 'Department-wide statistics and performance', icon: <BusinessIcon />, color: '#E65100' },
  { type: 'SEMESTER', title: 'Semester Report', description: 'Semester-wise results and analysis', icon: <CalendarMonthIcon />, color: '#6A1B9A' },
  { type: 'SUBJECT', title: 'Subject Report', description: 'Subject-wise performance and analytics', icon: <MenuBookIcon />, color: '#00838F' },
  { type: 'QUESTION_BANK', title: 'Question Bank Report', description: 'Question bank statistics and usage', icon: <QuizIcon />, color: '#AD1457' },
  { type: 'EXAM', title: 'Exam Report', description: 'Exam results and analysis', icon: <AssignmentIcon />, color: '#4E342E' },
  { type: 'ANALYTICS', title: 'Analytics Report', description: 'Comprehensive analytics and insights', icon: <AssessmentIcon />, color: '#1B5E20' },
  { type: 'AI_PERFORMANCE', title: 'AI Performance', description: 'AI system performance and accuracy', icon: <SmartToyIcon />, color: '#283593' },
];

const ReportsPage: React.FC = () => {
  const [selectedType, setSelectedType] = useState<ReportType | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  const handleCardClick = (type: ReportType) => {
    setSelectedType(type);
    setDialogOpen(true);
  };

  return (
    <Box>
      <PageHeader title="Reports" subtitle="Generate and download various reports" />

      <Grid container spacing={3} sx={{ mb: 3 }}>
        {reportCards.map((card) => (
          <Grid item xs={12} sm={6} md={4} key={card.type}>
            <Card variant="outlined" sx={{ height: '100%' }}>
              <CardActionArea onClick={() => handleCardClick(card.type)} sx={{ height: '100%' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1.5 }}>
                    <Box
                      sx={{
                        width: 48,
                        height: 48,
                        borderRadius: 2,
                        backgroundColor: card.color,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: '#fff',
                      }}
                    >
                      {card.icon}
                    </Box>
                    <Typography variant="h6" sx={{ fontSize: '1rem', fontWeight: 600 }}>
                      {card.title}
                    </Typography>
                  </Box>
                  <Typography variant="body2" color="text.secondary">
                    {card.description}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Card>
        <CardContent>
          <Typography variant="h6" sx={{ mb: 2 }}>Recent Reports</Typography>
          <List>
            <ListItem>
              <ListItemIcon><AssessmentIcon color="primary" /></ListItemIcon>
              <ListItemText primary="Exam Report - Unit Test 1" secondary="Generated on Jul 15, 2026" />
              <Chip label="PDF" size="small" color="primary" />
            </ListItem>
            <Divider />
            <ListItem>
              <ListItemIcon><PersonIcon color="success" /></ListItemIcon>
              <ListItemText primary="Student Performance Report" secondary="Generated on Jul 12, 2026" />
              <Chip label="Excel" size="small" color="success" />
            </ListItem>
            <Divider />
            <ListItem>
              <ListItemIcon><SmartToyIcon color="info" /></ListItemIcon>
              <ListItemText primary="AI Performance Report - June 2026" secondary="Generated on Jul 10, 2026" />
              <Chip label="PDF" size="small" color="primary" />
            </ListItem>
          </List>
        </CardContent>
      </Card>

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          Generate {reportCards.find((c) => c.type === selectedType)?.title}
        </DialogTitle>
        <DialogContent>
          {selectedType && <ReportGenerator preselectedType={selectedType} />}
        </DialogContent>
      </Dialog>
    </Box>
  );
};

export default ReportsPage;
