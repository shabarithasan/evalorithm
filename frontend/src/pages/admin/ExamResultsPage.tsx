import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import { examResultService, examService } from '../../services';
import { ExamResult, Exam } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const ExamResultsPage: React.FC = () => {
  const [results, setResults] = useState<ExamResult[]>([]);
  const [exams, setExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [selectedExamId, setSelectedExamId] = useState<number | ''>('');

  useEffect(() => {
    loadExams();
  }, []);

  useEffect(() => {
    if (selectedExamId) fetchResults();
  }, [selectedExamId, paginationModel]);

  const loadExams = async () => {
    try {
      const res = await examService.getAll({ page: 0, size: 100 });
      if (res.success) setExams(res.data.content);
    } catch {}
  };

  const fetchResults = useCallback(async () => {
    if (!selectedExamId) return;
    setLoading(true);
    try {
      const res = await examResultService.getExamResults(selectedExamId as number, paginationModel.page, paginationModel.pageSize);
      if (res.success) {
        setResults(res.data.content);
        setTotalCount(res.data.totalElements);
      }
    } catch {
    } finally {
      setLoading(false);
    }
  }, [selectedExamId, paginationModel]);

  const handleExport = async () => {
    if (!selectedExamId) return;
    try {
      const blob = await examResultService.exportResults(selectedExamId as number);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `exam-results-${selectedExamId}.csv`;
      a.click();
      window.URL.revokeObjectURL(url);
    } catch {}
  };

  const columns: GridColDef[] = [
    { field: 'studentName', headerName: 'Student', flex: 1, minWidth: 150 },
    { field: 'registerNumber', headerName: 'Register No', width: 130 },
    {
      field: 'totalMarksObtained', headerName: 'Marks', width: 120,
      renderCell: (params) => `${params.value} / ${params.row.totalMarksPossible}`,
    },
    {
      field: 'percentage', headerName: 'Percentage', width: 110,
      renderCell: (params) => (
        <Typography sx={{ fontWeight: 600, color: params.value >= 50 ? '#2E7D32' : '#C62828' }}>
          {params.value.toFixed(1)}%
        </Typography>
      ),
    },
    {
      field: 'grade', headerName: 'Grade', width: 80,
      renderCell: (params) => (
        <Box sx={{ px: 1, py: 0.25, borderRadius: 1, bgcolor: 'primary.main', color: '#fff', fontSize: '0.75rem', fontWeight: 600, textAlign: 'center' }}>
          {params.value}
        </Box>
      ),
    },
    {
      field: 'isPassed', headerName: 'Status', width: 100,
      renderCell: (params) => (
        <Box sx={{
          px: 1, py: 0.25, borderRadius: 1,
          bgcolor: params.value ? '#2E7D3215' : '#C6282815',
          color: params.value ? '#2E7D32' : '#C62828',
          fontSize: '0.75rem', fontWeight: 600,
        }}>
          {params.value ? 'Passed' : 'Failed'}
        </Box>
      ),
    },
    { field: 'correctAnswers', headerName: 'Correct', width: 80 },
    { field: 'wrongAnswers', headerName: 'Wrong', width: 80 },
    { field: 'timeTakenMinutes', headerName: 'Time (min)', width: 100 },
    {
      field: 'evaluatedAt', headerName: 'Evaluated', width: 160,
      valueFormatter: (params) => params.value ? formatDateTime(params.value) : '-',
    },
  ];

  return (
    <Box>
      <PageHeader title="Exam Results" subtitle="View and export examination results" />

      <Box sx={{ display: 'flex', gap: 2, mb: 3, alignItems: 'center' }}>
        <Box sx={{ minWidth: 250 }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>Select Exam</Typography>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <select
              value={selectedExamId}
              onChange={(e) => { setSelectedExamId(e.target.value ? Number(e.target.value) : ''); setPaginationModel({ ...paginationModel, page: 0 }); }}
              style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
            >
              <option value="">-- Select Exam --</option>
              {exams.map((e) => (
                <option key={e.id} value={e.id}>{e.title}</option>
              ))}
            </select>
          </Box>
        </Box>
        {selectedExamId && (
          <Button variant="outlined" startIcon={<DownloadIcon />} onClick={handleExport} sx={{ mt: 2 }}>
            Export CSV
          </Button>
        )}
      </Box>

      {selectedExamId ? (
        <DataTable
          columns={columns}
          rows={results}
          loading={loading}
          rowCount={totalCount}
          paginationModel={paginationModel}
          onPaginationChange={setPaginationModel}
        />
      ) : (
        <Card>
          <CardContent>
            <Typography color="text.secondary" sx={{ textAlign: 'center', py: 6 }}>
              Select an exam to view results
            </Typography>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default ExamResultsPage;
