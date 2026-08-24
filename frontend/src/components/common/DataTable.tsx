import React, { useState, useMemo } from 'react';
import { DataGrid, GridColDef, GridPaginationModel } from '@mui/x-data-grid';
import { TextField, InputAdornment, Box, Paper } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

interface DataTableProps {
  columns: GridColDef[];
  rows: any[];
  loading?: boolean;
  rowCount?: number;
  paginationModel?: GridPaginationModel;
  onPaginationChange?: (model: GridPaginationModel) => void;
  pageSizeOptions?: number[];
  searchable?: boolean;
  searchPlaceholder?: string;
  onSearch?: (query: string) => void;
}

const DataTable: React.FC<DataTableProps> = ({
  columns,
  rows,
  loading = false,
  rowCount = 0,
  paginationModel = { page: 0, pageSize: 10 },
  onPaginationChange,
  pageSizeOptions = [5, 10, 25, 50],
  searchable = false,
  searchPlaceholder = 'Search...',
  onSearch,
}) => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchQuery(value);
    if (onSearch) {
      onSearch(value);
    }
  };

  const processedRows = useMemo(() => {
    if (onSearch || !searchable || !searchQuery) return rows;
    const lower = searchQuery.toLowerCase();
    return rows.filter((row) =>
      columns.some((col) => {
        const val = row[col.field];
        return val && String(val).toLowerCase().includes(lower);
      })
    );
  }, [rows, searchQuery, columns, onSearch, searchable]);

  return (
    <Paper elevation={0} sx={{ width: '100%' }}>
      {searchable && (
        <Box sx={{ mb: 2 }}>
          <TextField
            fullWidth
            size="small"
            placeholder={searchPlaceholder}
            value={searchQuery}
            onChange={handleSearch}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            }}
            sx={{ maxWidth: 400 }}
          />
        </Box>
      )}
      <DataGrid
        rows={processedRows}
        columns={columns}
        loading={loading}
        rowCount={rowCount}
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={onPaginationChange}
        pageSizeOptions={pageSizeOptions}
        disableRowSelectionOnClick
        autoHeight
        sx={{
          border: 0,
          '& .MuiDataGrid-columnHeaders': {
            backgroundColor: 'grey.50',
            fontWeight: 600,
          },
          '& .MuiDataGrid-cell': {
            borderBottom: '1px solid',
            borderColor: 'grey.100',
          },
        }}
      />
    </Paper>
  );
};

export default DataTable;
