import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  IconButton,
  Chip,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EditIcon from '@mui/icons-material/Edit';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import PublishIcon from '@mui/icons-material/Publish';
import ArchiveIcon from '@mui/icons-material/Archive';
import DeleteIcon from '@mui/icons-material/Delete';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { examService } from '../../services';
import { Exam } from '../../types';
import { formatDateTime } from '../../utils/helpers';

const ExamManagementPage: React.FC = () => {
  const navigate = useNavigate();
  const [exams, setExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [typeFilter, setTypeFilter] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedExamId, setSelectedExamId] = useState<number>(0);
  const [confirmDialog, setConfirmDialog] = useState<{ open: boolean; title: string; message: string; onConfirm: () => void }>({
    open: false, title: '', message: '', onConfirm: () => {},
  });

  const fetchExams = useCallback(async () => {
    setLoading(true);
    try {
      const res = await examService.getAll({
        page: paginationModel.page,
        size: paginationModel.pageSize,
        search: searchTerm,
        status: statusFilter,
        examType: typeFilter,
      });
      if (res.success) {
        setExams(res.data.content);
        setTotalCount(res.data.totalElements);
      }
    } catch {
    } finally {
      setLoading(false);
    }
  }, [paginationModel, statusFilter, typeFilter, searchTerm]);

  useEffect(() => {
    fetchExams();
  }, [fetchExams]);

  const handleAction = (action: string, examId: number) => {
    setAnchorEl(null);
    switch (action) {
      case 'view': navigate(`/admin/exams/${examId}`); break;
      case 'edit': navigate(`/admin/exams/${examId}/edit`); break;
      case 'clone':
        examService.clone(examId).then(() => fetchExams());
        break;
      case 'publish':
        setConfirmDialog({
          open: true,
          title: 'Publish Exam',
          message: 'Are you sure you want to publish this exam? Students will be able to see and attempt it.',
          onConfirm: async () => {
            await examService.publish(examId);
            setConfirmDialog((prev) => ({ ...prev, open: false }));
            fetchExams();
          },
        });
        break;
      case 'archive':
        examService.archive(examId).then(() => fetchExams());
        break;
      case 'delete':
        setConfirmDialog({
          open: true,
          title: 'Delete Exam',
          message: 'Are you sure you want to delete this exam? This action cannot be undone.',
          onConfirm: async () => {
            await examService.delete(examId);
            setConfirmDialog((prev) => ({ ...prev, open: false }));
            fetchExams();
          },
        });
        break;
    }
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'ACTIVE': return '#1565C0';
      case 'PUBLISHED': return '#2E7D32';
      case 'DRAFT': return '#757575';
      case 'COMPLETED': return '#E65100';
      case 'CANCELLED': return '#C62828';
      case 'ARCHIVED': return '#78909C';
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
            <MenuItem onClick={() => handleAction('view', selectedExamId)}>
              <ListItemIcon><VisibilityIcon fontSize="small" /></ListItemIcon>
              <ListItemText>View</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => handleAction('edit', selectedExamId)}>
              <ListItemIcon><EditIcon fontSize="small" /></ListItemIcon>
              <ListItemText>Edit</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => handleAction('clone', selectedExamId)}>
              <ListItemIcon><ContentCopyIcon fontSize="small" /></ListItemIcon>
              <ListItemText>Clone</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => handleAction('publish', selectedExamId)}>
              <ListItemIcon><PublishIcon fontSize="small" /></ListItemIcon>
              <ListItemText>Publish</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => handleAction('archive', selectedExamId)}>
              <ListItemIcon><ArchiveIcon fontSize="small" /></ListItemIcon>
              <ListItemText>Archive</ListItemText>
            </MenuItem>
            <MenuItem onClick={() => handleAction('delete', selectedExamId)}>
              <ListItemIcon><DeleteIcon fontSize="small" color="error" /></ListItemIcon>
              <ListItemText sx={{ color: 'error.main' }}>Delete</ListItemText>
            </MenuItem>
          </Menu>
        </>
      ),
    },
  ];

  return (
    <Box>
      <PageHeader
        title="Exam Management"
        subtitle="Create and manage examinations"
        actionLabel="Create Exam"
        onAction={() => navigate('/admin/exams/create')}
      />

      <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
        <Chip label="All" onClick={() => { setStatusFilter(''); setPaginationModel({ ...paginationModel, page: 0 }); }}
          color={!statusFilter ? 'primary' : 'default'} variant={!statusFilter ? 'filled' : 'outlined'} />
        <Chip label="Draft" onClick={() => { setStatusFilter('DRAFT'); setPaginationModel({ ...paginationModel, page: 0 }); }}
          color={statusFilter === 'DRAFT' ? 'primary' : 'default'} variant={statusFilter === 'DRAFT' ? 'filled' : 'outlined'} />
        <Chip label="Published" onClick={() => { setStatusFilter('PUBLISHED'); setPaginationModel({ ...paginationModel, page: 0 }); }}
          color={statusFilter === 'PUBLISHED' ? 'primary' : 'default'} variant={statusFilter === 'PUBLISHED' ? 'filled' : 'outlined'} />
        <Chip label="Active" onClick={() => { setStatusFilter('ACTIVE'); setPaginationModel({ ...paginationModel, page: 0 }); }}
          color={statusFilter === 'ACTIVE' ? 'primary' : 'default'} variant={statusFilter === 'ACTIVE' ? 'filled' : 'outlined'} />
        <Chip label="Completed" onClick={() => { setStatusFilter('COMPLETED'); setPaginationModel({ ...paginationModel, page: 0 }); }}
          color={statusFilter === 'COMPLETED' ? 'primary' : 'default'} variant={statusFilter === 'COMPLETED' ? 'filled' : 'outlined'} />
      </Box>

      <DataTable
        columns={columns}
        rows={exams}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
        searchable
        searchPlaceholder="Search exams..."
        onSearch={(term) => { setSearchTerm(term); setPaginationModel({ ...paginationModel, page: 0 }); }}
      />

      <ConfirmDialog
        open={confirmDialog.open}
        onClose={() => setConfirmDialog((prev) => ({ ...prev, open: false }))}
        onConfirm={confirmDialog.onConfirm}
        title={confirmDialog.title}
        message={confirmDialog.message}
      />
    </Box>
  );
};

export default ExamManagementPage;
