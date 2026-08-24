import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  TextField,
  MenuItem,
  Button,
  Paper,
  LinearProgress,
  Alert,
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import PageHeader from '../../components/common/PageHeader';
import AttainmentTable from '../../components/obe/AttainmentTable';
import DataTableExport from '../../components/common/DataTableExport';
import { obeService, departmentService } from '../../services';
import { Attainment, Department } from '../../types';

const AttainmentDashboard: React.FC = () => {
  const [attainments, setAttainments] = useState<Attainment[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [selectedDept, setSelectedDept] = useState<number>(0);
  const [academicYear, setAcademicYear] = useState('2025-2026');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      const [deptsRes, attRes] = await Promise.allSettled([
        departmentService.getAll(),
        obeService.getAllAttainments(),
      ]);
      if (deptsRes.status === 'fulfilled' && deptsRes.value.success) {
        const deptList = deptsRes.value.data?.content || deptsRes.value.data || [];
        setDepartments(deptList);
        if (deptList.length > 0) setSelectedDept(deptList[0].id);
      }
      if (attRes.status === 'fulfilled' && attRes.value.success) {
        setAttainments(attRes.value.data?.content || attRes.value.data || []);
      }
    } catch {
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const fetchDashboard = async () => {
    if (!selectedDept) return;
    try {
      const response = await obeService.getDashboard(selectedDept, academicYear);
      if (response.success) {
        setAttainments(response.data?.attainments || []);
      }
    } catch {
      setError('Failed to load attainment data');
    }
  };

  const handleExport = async (format: string) => {
    try {
      const response = await obeService.exportReport(selectedDept, academicYear, format);
      const blob = new Blob([response.data], {
        type: format === 'PDF' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Attainment_Report_${academicYear}.${format === 'PDF' ? 'pdf' : 'xlsx'}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      setSuccess('Report exported successfully');
    } catch {
      setError('Failed to export report');
    }
  };

  const overallTarget = attainments.length > 0
    ? attainments.reduce((sum, a) => sum + a.targetAttainment, 0) / attainments.length
    : 0;
  const overallActual = attainments.length > 0
    ? attainments.reduce((sum, a) => sum + a.actualAttainment, 0) / attainments.length
    : 0;
  const achievedCount = attainments.filter((a) => a.isAchieved).length;
  const percentageAchieved = attainments.length > 0 ? (achievedCount / attainments.length) * 100 : 0;

  return (
    <Box>
      <PageHeader title="Attainment Dashboard" subtitle="Track and analyze CO/PO attainment" />

      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={4}>
          <TextField
            select
            label="Department"
            value={selectedDept || ''}
            onChange={(e) => setSelectedDept(Number(e.target.value))}
            fullWidth
          >
            {departments.map((d) => (
              <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={12} sm={4}>
          <TextField
            label="Academic Year"
            value={academicYear}
            onChange={(e) => setAcademicYear(e.target.value)}
            fullWidth
          />
        </Grid>
        <Grid item xs={12} sm={4}>
          <Button variant="contained" onClick={fetchDashboard} fullWidth sx={{ height: 56 }}>
            Load Dashboard
          </Button>
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={3}>
          <Paper variant="outlined" sx={{ p: 2, textAlign: 'center' }}>
            <Typography variant="h4" fontWeight={700} color="primary.main">{attainments.length}</Typography>
            <Typography variant="body2" color="text.secondary">Total COs</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Paper variant="outlined" sx={{ p: 2, textAlign: 'center' }}>
            <Typography variant="h4" fontWeight={700} color="success.main">{achievedCount}</Typography>
            <Typography variant="body2" color="text.secondary">Achieved</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Paper variant="outlined" sx={{ p: 2, textAlign: 'center' }}>
            <Typography variant="h4" fontWeight={700} color="warning.main">{overallActual.toFixed(1)}%</Typography>
            <Typography variant="body2" color="text.secondary">Avg. Attainment</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Paper variant="outlined" sx={{ p: 2, textAlign: 'center' }}>
            <Typography variant="h4" fontWeight={700} color="info.main">{percentageAchieved.toFixed(0)}%</Typography>
            <Typography variant="body2" color="text.secondary">Achievement Rate</Typography>
          </Paper>
        </Grid>
      </Grid>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">Overall Attainment Progress</Typography>
            <DataTableExport
              onExportPDF={() => handleExport('PDF')}
              onExportExcel={() => handleExport('EXCEL')}
              onPrint={() => window.print()}
            />
          </Box>
          <Box sx={{ mb: 1, display: 'flex', justifyContent: 'space-between' }}>
            <Typography variant="body2">Target: {overallTarget.toFixed(1)}%</Typography>
            <Typography variant="body2">Actual: {overallActual.toFixed(1)}%</Typography>
          </Box>
          <LinearProgress
            variant="determinate"
            value={Math.min(overallActual, 100)}
            color={overallActual >= overallTarget ? 'success' : 'warning'}
            sx={{ height: 16, borderRadius: 8 }}
          />
          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              startIcon={<DownloadIcon />}
              onClick={() => handleExport('PDF')}
            >
              Export Full Report
            </Button>
          </Box>
        </CardContent>
      </Card>

      <AttainmentTable data={attainments} />
    </Box>
  );
};

export default AttainmentDashboard;
