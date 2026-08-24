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
import { departmentService } from '../../services';
import { Department, DepartmentRequest, Status } from '../../types';
import { formatDate } from '../../utils/helpers';

const DepartmentPage: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedDept, setSelectedDept] = useState<Department | null>(null);
  const [formData, setFormData] = useState<DepartmentRequest>({
    code: '',
    name: '',
    description: '',
    status: 'ACTIVE',
  });
  const [submitting, setSubmitting] = useState(false);
  const [search, setSearch] = useState('');

  const fetchDepartments = useCallback(async () => {
    setLoading(true);
    try {
      const response = await departmentService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc',
        search || undefined
      );
      if (response.success) {
        setDepartments(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel, search]);

  useEffect(() => {
    fetchDepartments();
  }, [fetchDepartments]);

  const handleOpenDialog = (dept?: Department) => {
    if (dept) {
      setSelectedDept(dept);
      setFormData({
        code: dept.code,
        name: dept.name,
        description: dept.description,
        status: dept.status,
      });
    } else {
      setSelectedDept(null);
      setFormData({ code: '', name: '', description: '', status: 'ACTIVE' });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedDept) {
        await departmentService.update(selectedDept.id, formData);
      } else {
        await departmentService.create(formData);
      }
      setDialogOpen(false);
      fetchDepartments();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedDept) return;
    try {
      await departmentService.delete(selectedDept.id);
      setConfirmOpen(false);
      setSelectedDept(null);
      fetchDepartments();
    } catch {
      // Handle error
    }
  };

  const columns: GridColDef[] = [
    { field: 'code', headerName: 'Code', width: 120 },
    { field: 'name', headerName: 'Name', flex: 1, minWidth: 180 },
    { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: (params) => <StatusChip status={params.value} />,
    },
    { field: 'createdAt', headerName: 'Created', width: 130, valueFormatter: (value: any) => formatDate(value) },
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
                setSelectedDept(params.row);
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
        title="Departments"
        subtitle="Manage academic departments"
        actionLabel="Add Department"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={departments}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
        searchable
        searchPlaceholder="Search departments..."
        onSearch={setSearch}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedDept ? 'Edit Department' : 'Add Department'}
        onSubmit={handleSubmit}
        loading={submitting}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
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
          <TextField
            label="Description"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            multiline
            rows={3}
            fullWidth
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
        title="Delete Department"
        message={`Are you sure you want to delete "${selectedDept?.name}"? This action cannot be undone.`}
      />
    </Box>
  );
};

export default DepartmentPage;
