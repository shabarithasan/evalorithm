import React from 'react';
import { Box, IconButton, Tooltip, Paper, Divider } from '@mui/material';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import TableChartIcon from '@mui/icons-material/TableChart';
import PrintIcon from '@mui/icons-material/Print';

interface DataTableExportProps {
  onExportPDF?: () => void;
  onExportExcel?: () => void;
  onPrint?: () => void;
}

const DataTableExport: React.FC<DataTableExportProps> = ({
  onExportPDF,
  onExportExcel,
  onPrint,
}) => {
  return (
    <Paper variant="outlined" sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5, p: 0.5 }}>
      {onExportPDF && (
        <Tooltip title="Export as PDF">
          <IconButton size="small" onClick={onExportPDF} color="error">
            <PictureAsPdfIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      )}
      {onExportPDF && onExportExcel && <Divider orientation="vertical" flexItem />}
      {onExportExcel && (
        <Tooltip title="Export as Excel">
          <IconButton size="small" onClick={onExportExcel} color="success">
            <TableChartIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      )}
      {(onExportPDF || onExportExcel) && onPrint && <Divider orientation="vertical" flexItem />}
      {onPrint && (
        <Tooltip title="Print">
          <IconButton size="small" onClick={onPrint} color="primary">
            <PrintIcon fontSize="small" />
          </IconButton>
        </Tooltip>
      )}
    </Paper>
  );
};

export default DataTableExport;
