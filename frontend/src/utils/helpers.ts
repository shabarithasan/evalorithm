import { Role, Status } from '../types';

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const getInitials = (firstName: string, lastName: string): string => {
  return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
};

export const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

export const getRoleColor = (role: Role): string => {
  switch (role) {
    case 'ROLE_ADMIN':
      return '#D32F2F';
    case 'ROLE_FACULTY':
      return '#1565C0';
    case 'ROLE_STUDENT':
      return '#2E7D32';
    default:
      return '#757575';
  }
};

export const getRoleLabel = (role: Role): string => {
  switch (role) {
    case 'ROLE_ADMIN':
      return 'Admin';
    case 'ROLE_FACULTY':
      return 'Faculty';
    case 'ROLE_STUDENT':
      return 'Student';
    default:
      return role;
  }
};

export const getStatusColor = (status: Status): 'success' | 'error' | 'default' => {
  return status === 'ACTIVE' ? 'success' : 'error';
};

export const getNotificationTypeColor = (type: string): string => {
  switch (type) {
    case 'LOGIN':
      return '#1565C0';
    case 'SYSTEM':
      return '#D32F2F';
    case 'ACADEMIC':
      return '#2E7D32';
    default:
      return '#757575';
  }
};
