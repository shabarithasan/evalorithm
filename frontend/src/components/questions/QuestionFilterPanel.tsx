import React from 'react';
import {
  Box,
  TextField,
  MenuItem,
  Chip,
  Typography,
  Button,
  Divider,
  FormControl,
  InputLabel,
  Select,
  SelectChangeEvent,
  FormControlLabel,
  Checkbox,
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import ClearIcon from '@mui/icons-material/Clear';
import {
  QuestionType,
  QuestionDifficulty,
  BloomLevel,
  QuestionStatus,
} from '../../types';

interface QuestionFilterPanelProps {
  filters: {
    departmentId?: number | '';
    semesterId?: number | '';
    subjectId?: number | '';
    unitId?: number | '';
    topicId?: number | '';
    questionType: QuestionType | '';
    difficulty: QuestionDifficulty | '';
    bloomLevel: BloomLevel | '';
    status: QuestionStatus | '';
    categoryId?: number | '';
    isArchived: boolean;
  };
  onFilterChange: (filters: any) => void;
  onClear: () => void;
  departments: { id: number; name: string }[];
  semesters: { id: number; number: number }[];
  subjects: { id: number; name: string }[];
  units: { id: number; name: string }[];
  topics: { id: number; name: string }[];
  categories: { id: number; categoryName: string }[];
}

const questionTypes: QuestionType[] = ['MCQ', 'TRUE_FALSE', 'MATCH_FOLLOWING', 'FILL_BLANKS', 'ASSERTION_REASON', 'DESCRIPTIVE', 'CASE_STUDY', 'PROGRAMMING'];
const difficulties: QuestionDifficulty[] = ['EASY', 'MEDIUM', 'HARD', 'EXPERT'];
const bloomLevels: BloomLevel[] = ['K1_REMEMBER', 'K2_UNDERSTAND', 'K3_APPLY', 'K4_ANALYZE', 'K5_EVALUATE', 'K6_CREATE'];
const statuses: QuestionStatus[] = ['DRAFT', 'PENDING_REVIEW', 'APPROVED', 'REJECTED', 'ARCHIVED'];

const QuestionFilterPanel: React.FC<QuestionFilterPanelProps> = ({
  filters,
  onFilterChange,
  onClear,
  departments,
  semesters,
  subjects,
  units,
  topics,
  categories,
}) => {
  const handleChange = (field: string, value: any) => {
    onFilterChange({ ...filters, [field]: value });
  };

  return (
    <Box sx={{ p: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="subtitle1" sx={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1 }}>
          <FilterListIcon /> Filters
        </Typography>
        <Button size="small" startIcon={<ClearIcon />} onClick={onClear}>
          Clear All
        </Button>
      </Box>

      <Divider sx={{ mb: 2 }} />

      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <FormControl fullWidth size="small">
          <InputLabel>Department</InputLabel>
          <Select
            value={filters.departmentId?.toString() || ''}
            label="Department"
            onChange={(e: SelectChangeEvent) => handleChange('departmentId', e.target.value ? Number(e.target.value) : '')}
          >
            <MenuItem value="">All</MenuItem>
            {departments.map((d) => (
              <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Semester</InputLabel>
          <Select
            value={filters.semesterId?.toString() || ''}
            label="Semester"
            onChange={(e: SelectChangeEvent) => handleChange('semesterId', e.target.value ? Number(e.target.value) : '')}
          >
            <MenuItem value="">All</MenuItem>
            {semesters.map((s) => (
              <MenuItem key={s.id} value={s.id}>Semester {s.number}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Subject</InputLabel>
          <Select
            value={filters.subjectId?.toString() || ''}
            label="Subject"
            onChange={(e: SelectChangeEvent) => handleChange('subjectId', e.target.value ? Number(e.target.value) : '')}
          >
            <MenuItem value="">All</MenuItem>
            {subjects.map((s) => (
              <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Unit</InputLabel>
          <Select
            value={filters.unitId?.toString() || ''}
            label="Unit"
            onChange={(e: SelectChangeEvent) => handleChange('unitId', e.target.value ? Number(e.target.value) : '')}
          >
            <MenuItem value="">All</MenuItem>
            {units.map((u) => (
              <MenuItem key={u.id} value={u.id}>{u.name}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Topic</InputLabel>
          <Select
            value={filters.topicId?.toString() || ''}
            label="Topic"
            onChange={(e: SelectChangeEvent) => handleChange('topicId', e.target.value ? Number(e.target.value) : '')}
          >
            <MenuItem value="">All</MenuItem>
            {topics.map((t) => (
              <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <Divider />

        <FormControl fullWidth size="small">
          <InputLabel>Question Type</InputLabel>
          <Select
            value={filters.questionType}
            label="Question Type"
            onChange={(e: SelectChangeEvent) => handleChange('questionType', e.target.value)}
          >
            <MenuItem value="">All</MenuItem>
            {questionTypes.map((t) => (
              <MenuItem key={t} value={t}>{t.replace('_', ' ')}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Difficulty</InputLabel>
          <Select
            value={filters.difficulty}
            label="Difficulty"
            onChange={(e: SelectChangeEvent) => handleChange('difficulty', e.target.value)}
          >
            <MenuItem value="">All</MenuItem>
            {difficulties.map((d) => (
              <MenuItem key={d} value={d}>{d}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Bloom Level</InputLabel>
          <Select
            value={filters.bloomLevel}
            label="Bloom Level"
            onChange={(e: SelectChangeEvent) => handleChange('bloomLevel', e.target.value)}
          >
            <MenuItem value="">All</MenuItem>
            {bloomLevels.map((b) => (
              <MenuItem key={b} value={b}>{b.replace('_', ' ')}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Status</InputLabel>
          <Select
            value={filters.status}
            label="Status"
            onChange={(e: SelectChangeEvent) => handleChange('status', e.target.value)}
          >
            <MenuItem value="">All</MenuItem>
            {statuses.map((s) => (
              <MenuItem key={s} value={s}>{s.replace('_', ' ')}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth size="small">
          <InputLabel>Category</InputLabel>
          <Select
            value={filters.categoryId?.toString() || ''}
            label="Category"
            onChange={(e: SelectChangeEvent) => handleChange('categoryId', e.target.value ? Number(e.target.value) : '')}
          >
            <MenuItem value="">All</MenuItem>
            {categories.map((c) => (
              <MenuItem key={c.id} value={c.id}>{c.categoryName}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControlLabel
          control={
            <Checkbox
              checked={filters.isArchived}
              onChange={(e) => handleChange('isArchived', e.target.checked)}
            />
          }
          label="Show Archived Only"
        />
      </Box>
    </Box>
  );
};

export default QuestionFilterPanel;
