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
import { semesterService, departmentService } from '../../services';
import { Semester, SemesterRequest, Department, Status } from '../../types';

const SemesterPage: React.FC = () => {
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedSem, setSelectedSem] = useState<Semester | null>(null);
  const [formData, setFormData] = useState<SemesterRequest>({
    number: 1,
    departmentId: 0,
    status: 'ACTIVE',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const response = await departmentService.getAll(0, 100);
      if (response.success) {
        setDepartments(response.data.content);
      }
    } catch {
      // Handle error
    }
  };

  const fetchSemesters = useCallback(async () => {
    setLoading(true);
    try {
      const response = await semesterService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc'
      );
      if (response.success) {
        setSemesters(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchSemesters();
  }, [fetchSemesters]);

  const handleOpenDialog = (sem?: Semester) => {
    if (sem) {
      setSelectedSem(sem);
      setFormData({
        number: sem.number,
        departmentId: sem.departmentId,
        status: sem.status,
      });
    } else {
      setSelectedSem(null);
      setFormData({ number: 1, departmentId: departments[0]?.id || 0, status: 'ACTIVE' });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedSem) {
        await semesterService.update(selectedSem.id, formData);
      } else {
        await semesterService.create(formData);
      }
      setDialogOpen(false);
      fetchSemesters();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedSem) return;
    try {
      await semesterService.delete(selectedSem.id);
      setConfirmOpen(false);
      setSelectedSem(null);
      fetchSemesters();
    } catch {
      // Handle error
    }
  };

  const columns: GridColDef[] = [
    { field: 'number', headerName: 'Semester', width: 110 },
    { field: 'departmentName', headerName: 'Department', flex: 1, minWidth: 200 },
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
                setSelectedSem(params.row);
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
        title="Semesters"
        subtitle="Manage semesters across departments"
        actionLabel="Add Semester"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={semesters}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedSem ? 'Edit Semester' : 'Add Semester'}
        onSubmit={handleSubmit}
        loading={submitting}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
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
            label="Semester Number"
            type="number"
            value={formData.number}
            onChange={(e) => setFormData({ ...formData, number: Number(e.target.value) })}
            required
            fullWidth
            inputProps={{ min: 1, max: 8 }}
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
      </FormDialog>

      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Delete Semester"
        message="Are you sure you want to delete this semester?"
      />
    </Box>
  );
};

export default SemesterPage;
