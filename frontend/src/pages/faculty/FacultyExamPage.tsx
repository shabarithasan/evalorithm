import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Chip,
  IconButton,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import VisibilityIcon from '@mui/icons-material/Visibility';
import EditIcon from '@mui/icons-material/Edit';
import PublishIcon from '@mui/icons-material/Publish';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import { examService } from '../../services';
import { Exam } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const FacultyExamPage: React.FC = () => {
  const navigate = useNavigate();
  const [exams, setExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedExamId, setSelectedExamId] = useState<number>(0);

  const fetchExams = useCallback(async () => {
    setLoading(true);
    try {
      const res = await examService.getAll({
        page: paginationModel.page,
        size: paginationModel.pageSize,
      });
      if (res.success) {
        setExams(res.data.content);
        setTotalCount(res.data.totalElements);
      }
    } catch {
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchExams();
  }, [fetchExams]);

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'ACTIVE': return '#1565C0';
      case 'PUBLISHED': return '#2E7D32';
      case 'DRAFT': return '#757575';
      case 'COMPLETED': return '#E65100';
      default: return '#757575';
    }
  };

  const columns: GridColDef[] = [
    { field: 'title', headerName: 'Title', flex: 1, minWidth: 200 },
    { field: 'examType', headerName: 'Type', width: 140 },
    { field: 'subjectName', headerName: 'Subject', width: 150 },
    { field: 'startDate', headerName: 'Start Date', width: 170, valueFormatter: (params) => params.value ? formatDateTime(params.value) : '-' },
    { field: 'durationMinutes', headerName: 'Duration', width: 100, valueFormatter: (params) => `${params.value} min` },
    { field: 'questionCount', headerName: 'Questions', width: 100 },
    { field: 'studentCount', headerName: 'Students', width: 100 },
    {
      field: 'status', headerName: 'Status', width: 120,
      renderCell: (params) => (
        <Box sx={{
          px: 1, py: 0.25, borderRadius: 1,
          bgcolor: `${getStatusColor(params.value)}15`,
          color: getStatusColor(params.value),
          fontSize: '0.75rem', fontWeight: 600,
        }}>
          {params.value}
        </Box>
      ),
    },
    {
      field: 'actions', headerName: '', width: 50, sortable: false,
      renderCell: (params) => (
        <>
          <IconButton size="small" onClick={(e) => { e.stopPropagation(); setAnchorEl(e.currentTarget); setSelectedExamId(params.row.id); }}>
            <MoreVertIcon fontSize="small" />
          </IconButton>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
            <MenuItem onClick={() => { setAnchorEl(null); navigate(`/faculty/exams/${selectedExamId}`); }}>
              <ListItemIcon><VisibilityIcon fontSize="small" /></ListItemIcon>
              <ListItemText>View</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => { setAnchorEl(null); navigate(`/faculty/exams/${selectedExamId}/edit`); }}>
              <ListItemIcon><EditIcon fontSize="small" /></ListItemIcon>
              <ListItemText>Edit</ListItemText>
            </MenuItem>
          </Menu>
        </>
      ),
    },
  ];

  return (
    <Box>
      <PageHeader title="Exams" subtitle="Manage exams for your subjects" />
      <DataTable
        columns={columns}
        rows={exams}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
        searchable
        searchPlaceholder="Search exams..."
        onSearch={(term) => { setPaginationModel({ ...paginationModel, page: 0 }); }}
      />
    </Box>
  );
};

export default FacultyExamPage;
