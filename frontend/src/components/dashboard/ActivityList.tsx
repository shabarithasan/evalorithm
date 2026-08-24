import React from 'react';
import { List, ListItem, ListItemText, ListItemAvatar, Avatar, Typography, Box, Divider } from '@mui/material';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { formatDateTime } from '../../utils/helpers';

interface Activity {
  id: number;
  title: string;
  description: string;
  timestamp: string;
}

interface ActivityListProps {
  activities: Activity[];
}

const ActivityList: React.FC<ActivityListProps> = ({ activities }) => {
  if (activities.length === 0) {
    return (
      <Box sx={{ py: 4, textAlign: 'center' }}>
        <Typography color="text.secondary">No recent activities</Typography>
      </Box>
    );
  }

  return (
    <List disablePadding>
      {activities.map((activity, index) => (
        <React.Fragment key={activity.id}>
          <ListItem alignItems="flex-start" disablePadding sx={{ py: 1.5 }}>
            <ListItemAvatar>
              <Avatar sx={{ bgcolor: 'primary.light', width: 40, height: 40 }}>
                <AccessTimeIcon fontSize="small" />
              </Avatar>
            </ListItemAvatar>
            <ListItemText
              primary={
                <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
                  {activity.title}
                </Typography>
              }
              secondary={
                <>
                  <Typography variant="body2" color="text.secondary" component="span">
                    {activity.description}
                  </Typography>
                  <br />
                  <Typography variant="caption" color="text.secondary">
                    {formatDateTime(activity.timestamp)}
                  </Typography>
                </>
              }
            />
          </ListItem>
          {index < activities.length - 1 && <Divider />}
        </React.Fragment>
      ))}
    </List>
  );
};

export default ActivityList;
