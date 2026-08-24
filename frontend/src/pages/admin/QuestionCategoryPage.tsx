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
import { questionCategoryService } from '../../services';
import { QuestionCategory, QuestionCategoryRequest, Status } from '../../types';
import { formatDate } from '../../utils/helpers';

const QuestionCategoryPage: React.FC = () => {
  const [categories, setCategories] = useState<QuestionCategory[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({ page: 0, pageSize: 10 });
  const [dialogOpen, setDialogOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<QuestionCategory | null>(null);
  const [formData, setFormData] = useState<QuestionCategoryRequest>({
    categoryName: '',
    description: '',
    status: 'ACTIVE',
  });
  const [submitting, setSubmitting] = useState(false);
  const [search, setSearch] = useState('');

  const fetchCategories = useCallback(async () => {
    setLoading(true);
    try {
      const response = await questionCategoryService.getAll(
        paginationModel.page,
        paginationModel.pageSize,
        'id',
        'asc',
        search || undefined
      );
      if (response.success) {
        setCategories(response.data.content);
        setTotalCount(response.data.totalElements);
      }
    } catch {
      // Handle error
    } finally {
      setLoading(false);
    }
  }, [paginationModel, search]);

  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  const handleOpenDialog = (category?: QuestionCategory) => {
    if (category) {
      setSelectedCategory(category);
      setFormData({
        categoryName: category.categoryName,
        description: category.description,
        status: category.status,
      });
    } else {
      setSelectedCategory(null);
      setFormData({ categoryName: '', description: '', status: 'ACTIVE' });
    }
    setDialogOpen(true);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      if (selectedCategory) {
        await questionCategoryService.update(selectedCategory.id, formData);
      } else {
        await questionCategoryService.create(formData);
      }
      setDialogOpen(false);
      fetchCategories();
    } catch {
      // Handle error
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedCategory) return;
    try {
      await questionCategoryService.delete(selectedCategory.id);
      setConfirmOpen(false);
      setSelectedCategory(null);
      fetchCategories();
    } catch {
      // Handle error
    }
  };

  const columns: GridColDef[] = [
    { field: 'categoryName', headerName: 'Category Name', flex: 1, minWidth: 180 },
    { field: 'description', headerName: 'Description', flex: 1, minWidth: 200 },
    { field: 'questionCount', headerName: 'Questions', width: 100, type: 'number' },
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
                setSelectedCategory(params.row);
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
        title="Question Categories"
        subtitle="Manage question categories"
        actionLabel="Add Category"
        onAction={() => handleOpenDialog()}
      />

      <DataTable
        columns={columns}
        rows={categories}
        loading={loading}
        rowCount={totalCount}
        paginationModel={paginationModel}
        onPaginationChange={setPaginationModel}
        searchable
        searchPlaceholder="Search categories..."
        onSearch={setSearch}
      />

      <FormDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        title={selectedCategory ? 'Edit Category' : 'Add Category'}
        onSubmit={handleSubmit}
        loading={submitting}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
          <TextField
            label="Category Name"
            value={formData.categoryName}
            onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })}
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
        title="Delete Category"
        message={`Are you sure you want to delete "${selectedCategory?.categoryName}"? This action cannot be undone.`}
      />
    </Box>
  );
};

export default QuestionCategoryPage;
