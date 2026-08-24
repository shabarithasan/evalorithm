import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Typography,
  Button,
  IconButton,
  Drawer,
  useMediaQuery,
  useTheme,
  Tabs,
  Tab,
  Checkbox,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  Divider,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import FilterListIcon from '@mui/icons-material/FilterList';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import TableChartIcon from '@mui/icons-material/TableChart';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import QuestionCard from '../../components/questions/QuestionCard';
import QuestionTypeBadge from '../../components/questions/QuestionTypeBadge';
import DifficultyBadge from '../../components/questions/DifficultyBadge';
import BloomLevelBadge from '../../components/questions/BloomLevelBadge';
import QuestionFilterPanel from '../../components/questions/QuestionFilterPanel';
import BulkImportDialog from '../../components/questions/BulkImportDialog';
import StatusChip from '../../components/common/StatusChip';
import { questionService, departmentService, semesterService, subjectService, unitService, topicService, questionCategoryService } from '../../services';
import { Question, QuestionStatus } from '../../types';
import { formatDate } from '../../utils/helpers';

const QuestionBankPage: React.FC = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [viewMode, setViewMode] = useState<'table' | 'grid'>('table');
  const [filterOpen, setFilterOpen] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [importDialogOpen, setImportDialogOpen] = useState(false);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [confirmDialog, setConfirmDialog] = useState<{ open: boolean; title: string; message: string; onConfirm: () => void }>({
    open: false, title: '', message: '', onConfirm: () => {},
  });

  const [filters, setFilters] = useState({
    departmentId: '' as number | '',
    semesterId: '' as number | '',
    subjectId: '' as number | '',
    unitId: '' as number | '',
    topicId: '' as number | '',
    questionType: '' as any,
    difficulty: '' as any,
    bloomLevel: '' as any,
    status: '' as any,
    categoryId: '' as number | '',
    isArchived: false,
    searchTerm: '',
  });

  const [departments, setDepartments] = useState<{ id: number; name: string }[]>([]);
  const [semesters, setSemesters] = useState<{ id: number; number: number }[]>([]);
  const [subjects, setSubjects] = useState<{ id: number; name: string }[]>([]);
  const [units, setUnits] = useState<{ id: number; name: string }[]>([]);
  const [topics, setTopics] = useState<{ id: number; name: string }[]>([]);
  const [categories, setCategories] = useState<{ id: number; categoryName: string }[]>([]);

  useEffect(() => {
    loadFilterData();
  }, []);

  const loadFilterData = async () => {
    try {
      const [deptRes, catRes] = await Promise.all([
        departmentService.getAll(0, 100),
        questionCategoryService.getAllActive(),
      ]);
      if (deptRes.success) setDepartments(deptRes.data.content.map((d) => ({ id: d.id, name: d.name })));
      if (catRes.success) setCategories(catRes.data.map((c) => ({ id: c.id, categoryName: c.categoryName })));
    } catch {}
  };

  useEffect(() => {
    if (filters.departmentId) {
      semesterService.getByDepartment(filters.departmentId as number).then((res) => {
        if (res.success) setSemesters(res.data.map((s) => ({ id: s.id, number: s.number })));
      }).catch(() => {});
    } else { setSemesters([]); }
  }, [filters.departmentId]);

  useEffect(() => {
    if (filters.semesterId) {
      subjectService.getBySemester(filters.semesterId as number).then((res) => {
        if (res.success) setSubjects(res.data.map((s) => ({ id: s.id, name: s.name })));
      }).catch(() => {});
    } else { setSubjects([]); }
  }, [filters.semesterId]);

  useEffect(() => {
    if (filters.subjectId) {
      unitService.getBySubject(filters.subjectId as number).then((res) => {
        if (res.success) setUnits(res.data.map((u) => ({ id: u.id, name: u.name })));
      }).catch(() => {});
    } else { setUnits([]); }
  }, [filters.subjectId]);

  useEffect(() => {
    if (filters.unitId) {
      topicService.getByUnit(filters.unitId as number).then((res) => {
        if (res.success) setTopics(res.data.map((t) => ({ id: t.id, name: t.name })));
      }).catch(() => {});
    } else { setTopics([]); }
  }, [filters.unitId]);

  const fetchQuestions = useCallback(async () => {
    setLoading(true);
    try {
      const params: any = {
        page: paginationModel.page,
        size: paginationModel.pageSize,
        sortBy: 'id',
        sortDir: 'desc',
      };
      if (filters.departmentId) params.departmentId = filters.departmentId;
      if (filters.semesterId) params.semesterId = filters.semesterId;
      if (filters.subjectId) params.subjectId = filters.subjectId;
      if (filters.unitId) params.unitId = filters.unitId;
      if (filters.topicId) params.topicId = filters.topicId;
      if (filters.questionType) params.questionType = filters.questionType;
      if (filters.difficulty) params.difficulty = filters.difficulty;
      if (filters.bloomLevel) params.bloomLevel = filters.bloomLevel;
      if (filters.status) params.status = filters.status;
      if (filters.categoryId) params.categoryId = filters.categoryId;
      if (filters.isArchived) params.isArchived = true;
      if (filters.searchTerm) params.searchTerm = filters.searchTerm;

      const response = await questionService.getAll(params);
      if (response.success) {
        setQuestions(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel, filters]);

  useEffect(() => {
    fetchQuestions();
  }, [fetchQuestions]);

  const handleFilterChange = (newFilters: any) => {
    setFilters(newFilters);
    setPaginationModel({ ...paginationModel, page: 0 });
  };

  const handleClearFilters = () => {
    setFilters({
      departmentId: '', semesterId: '', subjectId: '', unitId: '', topicId: '',
      questionType: '', difficulty: '', bloomLevel: '', status: '', categoryId: '', isArchived: false, searchTerm: '',
    });
    setPaginationModel({ ...paginationModel, page: 0 });
  };

  const handleDuplicate = async (id: number) => {
    try {
      await questionService.duplicate(id);
      fetchQuestions();
    } catch {}
  };

  const handleArchive = async (id: number) => {
    try {
      await questionService.archive(id);
      fetchQuestions();
    } catch {}
  };

  const handleDelete = async (id: number) => {
    setConfirmDialog({
      open: true,
      title: 'Delete Question',
      message: 'Are you sure you want to delete this question?',
      onConfirm: async () => {
        try {
          await questionService.delete(id);
          setConfirmDialog((prev) => ({ ...prev, open: false }));
          fetchQuestions();
        } catch {}
      },
    });
  };

  const columns: GridColDef[] = [
    { field: 'title', headerName: 'Title', flex: 1, minWidth: 200 },
    {
      field: 'questionType', headerName: 'Type', width: 130,
      renderCell: (params) => <QuestionTypeBadge type={params.value} />,
    },
    {
      field: 'difficulty', headerName: 'Difficulty', width: 110,
      renderCell: (params) => <DifficultyBadge difficulty={params.value} />,
    },
    {
      field: 'bloomLevel', headerName: 'Bloom', width: 120,
      renderCell: (params) => <BloomLevelBadge level={params.value} />,
    },
    { field: 'subjectName', headerName: 'Subject', width: 150 },
    {
      field: 'status', headerName: 'Status', width: 130,
      renderCell: (params) => {
        const colorMap: Record<string, string> = { DRAFT: '#757575', PENDING_REVIEW: '#E65100', APPROVED: '#2E7D32', REJECTED: '#C62828', ARCHIVED: '#78909C' };
        return (
          <Box sx={{ px: 1, py: 0.25, borderRadius: 1, backgroundColor: `${colorMap[params.value] || '#757575'}15`, color: colorMap[params.value] || '#757575', fontSize: '0.75rem', fontWeight: 600 }}>
            {params.value.replace('_', ' ')}
          </Box>
        );
      },
    },
    { field: 'createdByName', headerName: 'Created By', width: 130 },
    {
      field: 'actions', headerName: '', width: 50, sortable: false,
      renderCell: (params) => (
        <IconButton size="small" onClick={(e) => { e.stopPropagation(); navigate(`/admin/questions/${params.row.id}`); }}>
          <MoreVertIcon fontSize="small" />
        </IconButton>
      ),
    },
  ];

  const drawerWidth = 300;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontSize: { xs: '1.5rem', sm: '1.8rem' } }}>Question Bank</Typography>
          <Typography variant="body2" color="text.secondary">Manage all questions</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button variant="outlined" startIcon={<UploadFileIcon />} onClick={() => setImportDialogOpen(true)}>
            Bulk Import
          </Button>
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/admin/questions/create')}>
            Create Question
          </Button>
        </Box>
      </Box>

      <Box sx={{ display: 'flex', gap: 2, mb: 2, alignItems: 'center' }}>
        <Tabs value={tabValue} onChange={(_, v) => { setTabValue(v); setFilters((prev) => ({ ...prev, isArchived: v === 1 })); setPaginationModel({ ...paginationModel, page: 0 }); }}>
          <Tab label="Active" />
          <Tab label="Archived" />
        </Tabs>
        <Box sx={{ flex: 1 }} />
        <IconButton onClick={() => setFilterOpen(!filterOpen)} color={Object.values(filters).some((v) => v && v !== false) ? 'primary' : 'default'}>
          <FilterListIcon />
        </IconButton>
        <IconButton onClick={() => setViewMode(viewMode === 'table' ? 'grid' : 'table')}>
          {viewMode === 'table' ? <ViewModuleIcon /> : <TableChartIcon />}
        </IconButton>
      </Box>

      <Box sx={{ display: 'flex' }}>
        <Drawer
          anchor="right"
          open={filterOpen}
          onClose={() => setFilterOpen(false)}
          sx={{
            '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' },
          }}
        >
          <QuestionFilterPanel
            filters={filters}
            onFilterChange={handleFilterChange}
            onClear={handleClearFilters}
            departments={departments}
            semesters={semesters}
            subjects={subjects}
            units={units}
            topics={topics}
            categories={categories}
          />
        </Drawer>

        <Box sx={{ flex: 1 }}>
          {viewMode === 'table' ? (
            <DataTable
              columns={columns}
              rows={questions}
              loading={loading}
              rowCount={totalCount}
              paginationModel={paginationModel}
              onPaginationChange={setPaginationModel}
              searchable
              searchPlaceholder="Search questions..."
              onSearch={(term) => {
                setFilters((prev) => ({ ...prev, searchTerm: term }));
                setPaginationModel({ ...paginationModel, page: 0 });
              }}
            />
          ) : (
            <Grid container spacing={2}>
              {questions.map((q) => (
                <Grid item xs={12} sm={6} md={4} lg={3} key={q.id}>
                  <QuestionCard
                    question={q}
                    onDuplicate={handleDuplicate}
                    onArchive={handleArchive}
                    onDelete={handleDelete}
                  />
                </Grid>
              ))}
              {questions.length === 0 && !loading && (
                <Grid item xs={12}>
                  <Box sx={{ textAlign: 'center', py: 6 }}>
                    <Typography variant="body1" color="text.secondary">No questions found</Typography>
                  </Box>
                </Grid>
              )}
            </Grid>
          )}
        </Box>
      </Box>

      <BulkImportDialog
        open={importDialogOpen}
        onClose={() => setImportDialogOpen(false)}
        onComplete={fetchQuestions}
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

export default QuestionBankPage;
