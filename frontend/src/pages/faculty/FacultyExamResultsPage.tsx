import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Chip,
} from '@mui/material';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import { examResultService, examService } from '../../services';
import { ExamResult, Exam } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const FacultyExamResultsPage: React.FC = () => {
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
        <Chip
          label={params.value ? 'Passed' : 'Failed'}
          size="small"
          color={params.value ? 'success' : 'error'}
        />
      ),
    },
    { field: 'correctAnswers', headerName: 'Correct', width: 80 },
    { field: 'wrongAnswers', headerName: 'Wrong', width: 80 },
    { field: 'timeTakenMinutes', headerName: 'Time (min)', width: 100 },
  ];

  return (
    <Box>
      <PageHeader title="Exam Results" subtitle="View results for your exams" />

      <Box sx={{ mb: 3 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>Select Exam</Typography>
        <select
          value={selectedExamId}
          onChange={(e) => { setSelectedExamId(e.target.value ? Number(e.target.value) : ''); setPaginationModel({ ...paginationModel, page: 0 }); }}
          style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ccc', minWidth: 250 }}
        >
          <option value="">-- Select Exam --</option>
          {exams.map((e) => (
            <option key={e.id} value={e.id}>{e.title}</option>
          ))}
        </select>
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

export default FacultyExamResultsPage;
