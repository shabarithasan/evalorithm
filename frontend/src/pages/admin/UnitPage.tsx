import React, { useState, useEffect, useCallback } from 'react';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import { IconButton, Tooltip, Box, MenuItem, TextField } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import FormDialog from '../../components/common/FormDialog';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { unitService, subjectService } from '../../services';
import { Unit, UnitRequest, Subject } from '../../types';

const UnitPage: React.FC = () => {
  const [units, setUnits] = useState<Unit[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedUnit, setSelectedUnit] = useState<Unit | null>(null);
  const [formData, setFormData] = useState<UnitRequest>({
    number: 1,
    name: '',
    subjectId: 0,
    description: '',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchSubjects();
  }, []);

  const fetchSubjects = async () => {
    try {
      const response = await subjectService.getAll(0, 200);
      if (response.success) setSubjects(response.data.content);
    } catch {
      // Handle error
    }
  };

  const fetchUnits = useCallback(async () => {
    setLoading(true);
    try {
      const response = await unitService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc'
      );
      if (response.success) {
        setUnits(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchUnits();
  }, [fetchUnits]);

  const handleOpenDialog = (unit?: Unit) => {
    if (unit) {
      setSelectedUnit(unit);
      setFormData({
        number: unit.number,
        name: unit.name,
        subjectId: unit.subjectId,
        description: unit.description,
      });
    } else {
      setSelectedUnit(null);
      setFormData({ number: 1, name: '', subjectId: subjects[0]?.id || 0, description: '' });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedUnit) {
        await unitService.update(selectedUnit.id, formData);
      } else {
        await unitService.create(formData);
      }
      setDialogOpen(false);
      fetchUnits();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedUnit) return;
    try {
      await unitService.delete(selectedUnit.id);
      setConfirmOpen(false);
      setSelectedUnit(null);
      fetchUnits();
    } catch {
      // Handle error
    }
  };

  const columns: GridColDef[] = [
    { field: 'number', headerName: 'Unit #', width: 90, type: 'number' },
    { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
    { field: 'subjectName', headerName: 'Subject', flex: 1, minWidth: 200 },
    { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
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
                setSelectedUnit(params.row);
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
        title="Units"
        subtitle="Manage subject units"
        actionLabel="Add Unit"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={units}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedUnit ? 'Edit Unit' : 'Add Unit'}
        onSubmit={handleSubmit}
        loading={submitting}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          <TextField
            select
            label="Subject"
            value={formData.subjectId}
            onChange={(e) => setFormData({ ...formData, subjectId: Number(e.target.value) })}
            required
            fullWidth
          >
            {subjects.map((sub) => (
              <MenuItem key={sub.id} value={sub.id}>
                {sub.code} - {sub.name}
              </MenuItem>
            ))}
          </TextField>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Unit Number"
              type="number"
              value={formData.number}
              onChange={(e) => setFormData({ ...formData, number: Number(e.target.value) })}
              required
              fullWidth
              inputProps={{ min: 1 }}
            />
            <TextField
              label="Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
              fullWidth
            />
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
        title="Delete Unit"
        message={`Are you sure you want to delete "${selectedUnit?.name}"?`}
      />
    </Box>
  );
};

export default UnitPage;
