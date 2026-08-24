import React from 'react';
import {
  Card,
  CardContent,
  CardActions,
  Typography,
  Box,
  IconButton,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EditIcon from '@mui/icons-material/Edit';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import ArchiveIcon from '@mui/icons-material/Archive';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import QuestionTypeBadge from './QuestionTypeBadge';
import DifficultyBadge from './DifficultyBadge';
import { Question } from '../../types';
import { truncateText } from '../../utils/helpers';

interface QuestionCardProps {
  question: Question;
  onDuplicate: (id: number) => void;
  onArchive: (id: number) => void;
  onDelete: (id: number) => void;
}

const statusColorMap: Record<string, string> = {
  DRAFT: '#757575',
  PENDING_REVIEW: '#E65100',
  APPROVED: '#2E7D32',
  REJECTED: '#C62828',
  ARCHIVED: '#78909C',
};

const QuestionCard: React.FC<QuestionCardProps> = ({ question, onDuplicate, onArchive, onDelete }) => {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const handleMenuOpen = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    setAnchorEl(e.currentTarget);
  };

  const handleMenuClose = () => setAnchorEl(null);

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        cursor: 'pointer',
        transition: 'box-shadow 0.2s, transform 0.2s',
        '&:hover': { boxShadow: 3, transform: 'translateY(-2px)' },
      }}
      onClick={() => navigate(`/admin/questions/${question.id}`)}
    >
      <CardContent sx={{ flex: 1 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 600, flex: 1, mr: 1 }}>
            {truncateText(question.title, 60)}
          </Typography>
          <IconButton size="small" onClick={handleMenuOpen}>
            <MoreVertIcon fontSize="small" />
          </IconButton>
        </Box>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1.5 }}>
          {truncateText(question.description, 80)}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 1 }}>
          <QuestionTypeBadge type={question.questionType} />
          <DifficultyBadge difficulty={question.difficulty} />
        </Box>
        <Typography variant="caption" color="text.secondary">
          {question.subjectName} · {question.marks} marks
        </Typography>
      </CardContent>
      <Box sx={{ px: 2, pb: 1 }}>
        <Box
          sx={{
            display: 'inline-block',
            px: 1,
            py: 0.25,
            borderRadius: 1,
            backgroundColor: `${statusColorMap[question.status] || '#757575'}15`,
            color: statusColorMap[question.status] || '#757575',
            fontSize: '0.7rem',
            fontWeight: 600,
          }}
        >
          {question.status.replace('_', ' ')}
        </Box>
      </Box>

      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleMenuClose}>
        <MenuItem onClick={() => { handleMenuClose(); navigate(`/admin/questions/${question.id}/edit`); }}>
          <ListItemIcon><EditIcon fontSize="small" /></ListItemIcon>
          <ListItemText>Edit</ListItemText>
        </MenuItem>
        <MenuItem onClick={() => { handleMenuClose(); onDuplicate(question.id); }}>
          <ListItemIcon><ContentCopyIcon fontSize="small" /></ListItemIcon>
          <ListItemText>Duplicate</ListItemText>
        </MenuItem>
        <MenuItem onClick={() => { handleMenuClose(); onArchive(question.id); }}>
          <ListItemIcon><ArchiveIcon fontSize="small" /></ListItemIcon>
          <ListItemText>{question.isArchived ? 'Restore' : 'Archive'}</ListItemText>
        </MenuItem>
        <MenuItem onClick={() => { handleMenuClose(); onDelete(question.id); }}>
          <ListItemIcon><DeleteIcon fontSize="small" color="error" /></ListItemIcon>
          <ListItemText sx={{ color: 'error.main' }}>Delete</ListItemText>
        </MenuItem>
      </Menu>
    </Card>
  );
};

export default QuestionCard;
