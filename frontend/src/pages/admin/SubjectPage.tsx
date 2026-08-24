import React, { useState, useEffect, useCallback } from 'react';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import { IconButton, Tooltip, Box, MenuItem, TextField } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import FormDialog from '../../components/common/FormDialog';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import StatusChip from '../../components/common/StatusChip';
import { subjectService, departmentService, semesterService } from '../../services';
import { Subject, SubjectRequest, Department, Semester, Status } from '../../types';
import { formatDate } from '../../utils/helpers';

const SubjectPage: React.FC = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedSubject, setSelectedSubject] = useState<Subject | null>(null);
  const [formData, setFormData] = useState<SubjectRequest>({
    code: '',
    name: '',
    departmentId: 0,
    semesterId: 0,
    credits: 3,
    description: '',
    status: 'ACTIVE',
  });
  const [submitting, setSubmitting] = useState(false);
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchDropdowns();
  }, []);

  const fetchDropdowns = async () => {
    try {
      const [deptRes, semRes] = await Promise.all([
        departmentService.getAll(0, 100),
        semesterService.getAll(0, 100),
      ]);
      if (deptRes.success) setDepartments(deptRes.data.content);
      if (semRes.success) setSemesters(semRes.data.content);
    } catch {
      // Handle error
    }
  };

  const fetchSubjects = useCallback(async () => {
    setLoading(true);
    try {
      const response = await subjectService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc',
        search || undefined
      );
      if (response.success) {
        setSubjects(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel, search]);

  useEffect(() => {
    fetchSubjects();
  }, [fetchSubjects]);

  const handleOpenDialog = (subject?: Subject) => {
    if (subject) {
      setSelectedSubject(subject);
      setFormData({
        code: subject.code,
        name: subject.name,
        departmentId: subject.departmentId,
        semesterId: subject.semesterId,
        credits: subject.credits,
        description: subject.description,
        status: subject.status,
      });
    } else {
      setSelectedSubject(null);
      setFormData({
        code: '',
        name: '',
        departmentId: departments[0]?.id || 0,
        semesterId: semesters[0]?.id || 0,
        credits: 3,
        description: '',
        status: 'ACTIVE',
      });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedSubject) {
        await subjectService.update(selectedSubject.id, formData);
      } else {
        await subjectService.create(formData);
      }
      setDialogOpen(false);
      fetchSubjects();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedSubject) return;
    try {
      await subjectService.delete(selectedSubject.id);
      setConfirmOpen(false);
      setSelectedSubject(null);
      fetchSubjects();
    } catch {
      // Handle error
    }
  };

  const columns: GridColDef[] = [
    { field: 'code', headerName: 'Code', width: 120 },
    { field: 'name', headerName: 'Name', flex: 1, minWidth: 180 },
    { field: 'departmentName', headerName: 'Department', width: 150 },
    { field: 'semesterNumber', headerName: 'Semester', width: 100, type: 'number' },
    { field: 'credits', headerName: 'Credits', width: 80, type: 'number' },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: (params) => <StatusChip status={params.value} />,
    },
    {
      field: 'actions',
      headerName: 'Actions',
      width: 120,
      sortable: false,
      renderCell: (params) => (
        <Box>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => handleOpenDialog(params.row)}>
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              color="error"
              onClick={() => {
                setSelectedSubject(params.row);
                setConfirmOpen(true);
              }}
            >
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      ),
    },
  ];

  return (
    <Box>
      <PageHeader
        title="Subjects"
        subtitle="Manage academic subjects"
        actionLabel="Add Subject"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={subjects}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
        searchable
        searchPlaceholder="Search subjects..."
        onSearch={setSearch}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedSubject ? 'Edit Subject' : 'Add Subject'}
        onSubmit={handleSubmit}
        loading={submitting}
        maxWidth="md"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Code"
              value={formData.code}
              onChange={(e) => setFormData({ ...formData, code: e.target.value })}
              required
              fullWidth
            />
            <TextField
              label="Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
              fullWidth
            />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              select
              label="Department"
              value={formData.departmentId}
              onChange={(e) => setFormData({ ...formData, departmentId: Number(e.target.value) })}
              required
              fullWidth
            >
              {departments.map((dept) => (
                <MenuItem key={dept.id} value={dept.id}>
                  {dept.name}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              select
              label="Semester"
              value={formData.semesterId}
              onChange={(e) => setFormData({ ...formData, semesterId: Number(e.target.value) })}
              required
              fullWidth
            >
              {semesters.map((sem) => (
                <MenuItem key={sem.id} value={sem.id}>
                  {sem.departmentName} - Semester {sem.number}
                </MenuItem>
              ))}
            </TextField>
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Credits"
              type="number"
              value={formData.credits}
              onChange={(e) => setFormData({ ...formData, credits: Number(e.target.value) })}
              required
              fullWidth
              inputProps={{ min: 1 }}
            />
            <TextField
              select
              label="Status"
              value={formData.status}
              onChange={(e) => setFormData({ ...formData, status: e.target.value as Status })}
              fullWidth
            >
              <MenuItem value="ACTIVE">Active</MenuItem>
              <MenuItem value="INACTIVE">Inactive</MenuItem>
            </TextField>
          </Box>
          <TextField
            label="Description"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            multiline
            rows={3}
            fullWidth
          />
        </Box>
      </FormDialog>

      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Delete Subject"
        message={`Are you sure you want to delete "${selectedSubject?.name}"?`}
      />
    </Box>
  );
};

export default SubjectPage;
