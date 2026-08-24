import React, { useState, useEffect, useCallback } from 'react';
import { GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import { IconButton, Tooltip, Box, MenuItem, TextField } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import DataTable from '../../components/common/DataTable';
import PageHeader from '../../components/common/PageHeader';
import FormDialog from '../../components/common/FormDialog';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { topicService, unitService } from '../../services';
import { Topic, TopicRequest, Unit } from '../../types';
import { truncateText } from '../../utils/helpers';

const TopicPage: React.FC = () => {
  const [topics, setTopics] = useState<Topic[]>([]);
  const [units, setUnits] = useState<Unit[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedTopic, setSelectedTopic] = useState<Topic | null>(null);
  const [formData, setFormData] = useState<TopicRequest>({
    name: '',
    unitId: 0,
    description: '',
    keywords: '',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchUnits();
  }, []);

  const fetchUnits = async () => {
    try {
      const response = await unitService.getAll(0, 200);
      if (response.success) setUnits(response.data.content);
    } catch {
      // Handle error
    }
  };

  const fetchTopics = useCallback(async () => {
    setLoading(true);
    try {
      const response = await topicService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc'
      );
      if (response.success) {
        setTopics(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchTopics();
  }, [fetchTopics]);

  const handleOpenDialog = (topic?: Topic) => {
    if (topic) {
      setSelectedTopic(topic);
      setFormData({
        name: topic.name,
        unitId: topic.unitId,
        description: topic.description,
        keywords: topic.keywords,
      });
    } else {
      setSelectedTopic(null);
      setFormData({ name: '', unitId: units[0]?.id || 0, description: '', keywords: '' });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedTopic) {
        await topicService.update(selectedTopic.id, formData);
      } else {
        await topicService.create(formData);
      }
      setDialogOpen(false);
      fetchTopics();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedTopic) return;
    try {
      await topicService.delete(selectedTopic.id);
      setConfirmOpen(false);
      setSelectedTopic(null);
      fetchTopics();
    } catch {
      // Handle error
    }
  };

  const columns: GridColDef[] = [
    { field: 'name', headerName: 'Name', flex: 1, minWidth: 200 },
    { field: 'unitName', headerName: 'Unit', flex: 1, minWidth: 180 },
    {
      field: 'description',
      headerName: 'Description',
      flex: 1,
      minWidth: 200,
      valueGetter: (value: any) => truncateText(value || '', 80),
    },
    {
      field: 'keywords',
      headerName: 'Keywords',
      width: 200,
      valueGetter: (value: any) => truncateText(value || '', 50),
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
                setSelectedTopic(params.row);
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
        title="Topics"
        subtitle="Manage unit topics"
        actionLabel="Add Topic"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={topics}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedTopic ? 'Edit Topic' : 'Add Topic'}
        onSubmit={handleSubmit}
        loading={submitting}
        maxWidth="md"
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          <TextField
            select
            label="Unit"
            value={formData.unitId}
            onChange={(e) => setFormData({ ...formData, unitId: Number(e.target.value) })}
            required
            fullWidth
          >
            {units.map((unit) => (
              <MenuItem key={unit.id} value={unit.id}>
                {unit.subjectName} - Unit {unit.number}: {unit.name}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            label="Topic Name"
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
            label="Keywords (comma separated)"
            value={formData.keywords}
            onChange={(e) => setFormData({ ...formData, keywords: e.target.value })}
            fullWidth
            helperText="Separate keywords with commas"
          />
        </Box>
      </FormDialog>

      <ConfirmDialog
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Delete Topic"
        message={`Are you sure you want to delete "${selectedTopic?.name}"?`}
      />
    </Box>
  );
};

export default TopicPage;
