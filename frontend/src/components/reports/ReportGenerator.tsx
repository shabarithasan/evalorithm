import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  MenuItem,
  Grid,
  Alert,
  CircularProgress,
  Paper,
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import { ReportType, ExportFormat } from '../../types';
import { reportService } from '../../services';

interface ReportGeneratorProps {
  preselectedType?: ReportType;
}

const reportTypes: { value: ReportType; label: string }[] = [
  { value: 'STUDENT', label: 'Student Report' },
  { value: 'FACULTY', label: 'Faculty Report' },
  { value: 'DEPARTMENT', label: 'Department Report' },
  { value: 'SEMESTER', label: 'Semester Report' },
  { value: 'SUBJECT', label: 'Subject Report' },
  { value: 'QUESTION_BANK', label: 'Question Bank Report' },
  { value: 'EXAM', label: 'Exam Report' },
  { value: 'ANALYTICS', label: 'Analytics Report' },
  { value: 'AI_PERFORMANCE', label: 'AI Performance Report' },
];

const ReportGenerator: React.FC<ReportGeneratorProps> = ({ preselectedType }) => {
  const [reportType, setReportType] = useState<ReportType>(preselectedType || 'STUDENT');
  const [format, setFormat] = useState<ExportFormat>('PDF');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (preselectedType) setReportType(preselectedType);
  }, [preselectedType]);

  const handleGenerate = async () => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const response = await reportService.generate({
        reportType,
        format,
        dateFrom: dateFrom || undefined,
        dateTo: dateTo || undefined,
      });
      const blob = new Blob([response.data], {
        type: format === 'PDF' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${reportType}_Report.${format === 'PDF' ? 'pdf' : 'xlsx'}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      setSuccess('Report generated successfully!');
    } catch {
      setError('Failed to generate report. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" sx={{ mb: 2 }}>Generate Report</Typography>
        {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
        {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <TextField
              select
              label="Report Type"
              value={reportType}
              onChange={(e) => setReportType(e.target.value as ReportType)}
              fullWidth
            >
              {reportTypes.map((rt) => (
                <MenuItem key={rt.value} value={rt.value}>{rt.label}</MenuItem>
              ))}
            </TextField>
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              select
              label="Format"
              value={format}
              onChange={(e) => setFormat(e.target.value as ExportFormat)}
              fullWidth
            >
              <MenuItem value="PDF">PDF</MenuItem>
              <MenuItem value="EXCEL">Excel</MenuItem>
            </TextField>
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Date From"
              type="date"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
              fullWidth
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Date To"
              type="date"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}
              fullWidth
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
        </Grid>
        <Paper variant="outlined" sx={{ p: 2, mt: 2, backgroundColor: 'grey.50' }}>
          <Typography variant="body2" color="text.secondary">
            Report: <strong>{reportTypes.find((r) => r.value === reportType)?.label}</strong> | Format: <strong>{format}</strong>
          </Typography>
        </Paper>
        <Button
          variant="contained"
          startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <DownloadIcon />}
          onClick={handleGenerate}
          disabled={loading}
          sx={{ mt: 2 }}
          fullWidth
        >
          {loading ? 'Generating...' : 'Generate Report'}
        </Button>
      </CardContent>
    </Card>
  );
};

export default ReportGenerator;
