import React, { useState, useEffect, useCallback } from 'react';
import { GridColDef, GridPaginationModel, GridValueGetterParams } from '@mui/x-data-grid';
import {
  IconButton,
  Tooltip,
  Box,
  MenuItem,
  TextField,
  Chip,
  Typography,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AssignmentIndIcon from '@mui/icons-material/AssignmentInd';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import FormDialog from '../../components/common/FormDialog';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { facultyService, departmentService } from '../../services';
import { Faculty, FacultyRequest, Department } from '../../types';

const FacultyPage: React.FC = () => {
  const [faculty, setFaculty] = useState<Faculty[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [assignDialogOpen, setAssignDialogOpen] = useState(false);
  const [selectedFaculty, setSelectedFaculty] = useState<Faculty | null>(null);
  const [selectedSubjectIds, setSelectedSubjectIds] = useState<number[]>([]);
  const [formData, setFormData] = useState<FacultyRequest>({
    facultyId: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    departmentId: 0,
    designation: '',
    assignedSubjectIds: [],
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const response = await departmentService.getAll(0, 100);
      if (response.success) setDepartments(response.data.content);
    } catch {
      // Handle error
    }
  };

  const fetchFaculty = useCallback(async () => {
    setLoading(true);
    try {
      const response = await facultyService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc'
      );
      if (response.success) {
        setFaculty(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchFaculty();
  }, [fetchFaculty]);

  const handleOpenDialog = (fac?: Faculty) => {
    if (fac) {
      setSelectedFaculty(fac);
      setFormData({
        facultyId: fac.facultyId,
        firstName: fac.firstName,
        lastName: fac.lastName,
        email: fac.email,
        phone: fac.phone,
        departmentId: fac.departmentId,
        designation: fac.designation,
        assignedSubjectIds: fac.assignedSubjects?.map((s) => s.id) || [],
      });
    } else {
      setSelectedFaculty(null);
      setFormData({
        facultyId: '',
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        departmentId: departments[0]?.id || 0,
        designation: '',
        assignedSubjectIds: [],
      });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedFaculty) {
        await facultyService.update(selectedFaculty.id, formData);
      } else {
        await facultyService.create(formData);
      }
      setDialogOpen(false);
      fetchFaculty();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedFaculty) return;
    try {
      await facultyService.delete(selectedFaculty.id);
      setConfirmOpen(false);
      setSelectedFaculty(null);
      fetchFaculty();
    } catch {
      // Handle error
    }
  };

  const handleAssignSubjects = async () => {
    if (!selectedFaculty) return;
    setSubmitting(true);
    try {
      const currentIds = selectedFaculty.assignedSubjects?.map((s) => s.id) || [];
      const toAdd = selectedSubjectIds.filter((id) => !currentIds.includes(id));
      const toRemove = currentIds.filter((id) => !selectedSubjectIds.includes(id));

      if (toAdd.length > 0) {
        await facultyService.assignSubjects(selectedFaculty.id, toAdd);
      }
      if (toRemove.length > 0) {
        await facultyService.removeSubjects(selectedFaculty.id, toRemove);
      }
      setAssignDialogOpen(false);
      fetchFaculty();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const columns: GridColDef[] = [
    { field: 'facultyId', headerName: 'Faculty ID', width: 130 },
    {
      field: 'name',
      headerName: 'Name',
      flex: 1,
      minWidth: 180,
      valueGetter: (params: GridValueGetterParams) => `${params.row.firstName} ${params.row.lastName}`,
    },
    { field: 'email', headerName: 'Email', flex: 1, minWidth: 200 },
    { field: 'departmentName', headerName: 'Department', width: 150 },
    { field: 'designation', headerName: 'Designation', width: 140 },
    {
      field: 'subjectCount',
      headerName: 'Subjects',
      width: 100,
      type: 'number',
      valueGetter: (params: GridValueGetterParams) => params.row.assignedSubjects?.length || 0,
    },
    {
      field: 'actions',
      headerName: 'Actions',
      width: 160,
      sortable: false,
      renderCell: (params) => (
        <Box>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => handleOpenDialog(params.row)}>
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Assign Subjects">
            <IconButton
              size="small"
              color="primary"
              onClick={() => {
                setSelectedFaculty(params.row);
                setSelectedSubjectIds(params.row.assignedSubjects?.map((s: any) => s.id) || []);
                setAssignDialogOpen(true);
              }}
            >
              <AssignmentIndIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              size="small"
              color="error"
              onClick={() => {
                setSelectedFaculty(params.row);
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
        title="Faculty"
        subtitle="Manage faculty members"
        actionLabel="Add Faculty"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={faculty}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedFaculty ? 'Edit Faculty' : 'Add Faculty'}
        onSubmit={handleSubmit}
        loading={submitting}
        maxWidth="md"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Faculty ID"
              value={formData.facultyId}
              onChange={(e) => setFormData({ ...formData, facultyId: e.target.value })}
              required
              fullWidth
            />
            <TextField
              label="Designation"
              value={formData.designation}
              onChange={(e) => setFormData({ ...formData, designation: e.target.value })}
              required
              fullWidth
            />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="First Name"
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              required
              fullWidth
            />
            <TextField
              label="Last Name"
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              required
              fullWidth
            />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Email"
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              required
              fullWidth
            />
            <TextField
              label="Phone"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              required
              fullWidth
            />
          </Box>
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
        </Box>
      </FormDialog>

      <FormDialog
        open={assignDialogOpen}
        onClose={() => setAssignDialogOpen(false)}
        title={`Assign Subjects to ${selectedFaculty?.firstName} ${selectedFaculty?.lastName}`}
        onSubmit={handleAssignSubjects}
        loading={submitting}
        maxWidth="md"
        submitLabel="Update Subjects"
      >
        <Box sx={{ pt: 1 }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Select the subjects to assign to this faculty member.
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {selectedFaculty?.assignedSubjects?.map((subject) => (
              <Chip
                key={subject.id}
                label={`${subject.code} - ${subject.name}`}
                onClick={() => {
                  setSelectedSubjectIds((prev) =>
                    prev.includes(subject.id)
                      ? prev.filter((id) => id !== subject.id)
                      : [...prev, subject.id]
                  );
                }}
                color={selectedSubjectIds.includes(subject.id) ? 'primary' : 'default'}
                variant={selectedSubjectIds.includes(subject.id) ? 'filled' : 'outlined'}
                sx={{ cursor: 'pointer' }}
              />
            ))}
          </Box>
        </Box>
      </FormDialog>

      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Delete Faculty"
        message={`Are you sure you want to delete "${selectedFaculty?.firstName} ${selectedFaculty?.lastName}"?`}
      />
    </Box>
  );
};

export default FacultyPage;
