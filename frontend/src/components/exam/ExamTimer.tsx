import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Box, Typography } from '@mui/material';
import { keyframes } from '@emotion/react';

interface ExamTimerProps {
  totalMinutes: number;
  onTimeUp: () => void;
  onWarning?: (minutesLeft: number) => void;
  isPaused?: boolean;
}

const pulse = keyframes`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
`;

const ExamTimer: React.FC<ExamTimerProps> = ({ totalMinutes, onTimeUp, onWarning, isPaused = false }) => {
  const [totalSeconds, setTotalSeconds] = useState(totalMinutes * 60);
  const hasWarned10 = useRef(false);
  const hasWarned5 = useRef(false);
  const hasWarned1 = useRef(false);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    setTotalSeconds(totalMinutes * 60);
    hasWarned10.current = false;
    hasWarned5.current = false;
    hasWarned1.current = false;
  }, [totalMinutes]);

  useEffect(() => {
    if (isPaused) {
      if (intervalRef.current) clearInterval(intervalRef.current);
      return;
    }

    intervalRef.current = setInterval(() => {
      setTotalSeconds((prev) => {
        if (prev <= 1) {
          if (intervalRef.current) clearInterval(intervalRef.current);
          onTimeUp();
          return 0;
        }

        const minutesLeft = Math.floor((prev - 1) / 60);
        if (minutesLeft <= 10 && !hasWarned10.current && onWarning) {
          hasWarned10.current = true;
          onWarning(10);
        }
        if (minutesLeft <= 5 && !hasWarned5.current && onWarning) {
          hasWarned5.current = true;
          onWarning(5);
        }
        if (minutesLeft <= 1 && !hasWarned1.current && onWarning) {
          hasWarned1.current = true;
          onWarning(1);
        }

        return prev - 1;
      });
    }, 1000);

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [isPaused, onTimeUp, onWarning]);

  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  const getColor = (): string => {
    if (totalSeconds > 600) return '#2E7D32';
    if (totalSeconds > 300) return '#E65100';
    return '#C62828';
  };

  const color = getColor();
  const isCritical = totalSeconds <= 60;

  const pad = (n: number) => String(n).padStart(2, '0');

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        gap: 0.5,
        animation: isCritical ? `${pulse} 1s ease-in-out infinite` : 'none',
      }}
    >
      <Box
        sx={{
          bgcolor: color,
          color: '#fff',
          px: 1.5,
          py: 0.75,
          borderRadius: 1,
          fontFamily: 'monospace',
          fontSize: '1.25rem',
          fontWeight: 700,
          letterSpacing: 1,
          minWidth: 100,
          textAlign: 'center',
        }}
      >
        {hours > 0 && `${pad(hours)}:`}
        {pad(minutes)}:{pad(seconds)}
      </Box>
    </Box>
  );
};

export default ExamTimer;
