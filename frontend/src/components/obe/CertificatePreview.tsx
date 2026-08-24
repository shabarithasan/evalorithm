import React from 'react';
import { Box, Paper, Typography, Divider } from '@mui/material';
import { Certificate } from '../../types';

interface CertificatePreviewProps {
  certificate: Certificate;
}

const CertificatePreview: React.FC<CertificatePreviewProps> = ({ certificate }) => {
  return (
    <Paper
      variant="outlined"
      sx={{
        p: 0,
        maxWidth: 700,
        mx: 'auto',
        border: '3px solid #1565C0',
        borderRadius: 2,
        overflow: 'hidden',
      }}
    >
      <Box
        sx={{
          border: '2px solid #1565C0',
          m: 4,
          p: 4,
          textAlign: 'center',
          position: 'relative',
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            top: 8,
            left: 8,
            right: 8,
            bottom: 8,
            border: '1px solid #90CAF9',
            borderRadius: 1,
            pointerEvents: 'none',
          }}
        />
        <Typography variant="h4" sx={{ fontWeight: 700, color: 'primary.main', mb: 0.5 }}>
          EVALORITHM
        </Typography>
        <Typography variant="subtitle2" color="text.secondary">
          Institute of Technology & Engineering
        </Typography>
        <Divider sx={{ my: 2, borderColor: 'primary.main' }} />

        <Typography
          variant="h5"
          sx={{
            fontWeight: 700,
            color: 'text.primary',
            textTransform: 'uppercase',
            letterSpacing: 2,
            mb: 2,
          }}
        >
          Certificate of {certificate.certificateType}
        </Typography>

        <Typography variant="body1" sx={{ mb: 1 }}>
          This is to certify that
        </Typography>

        <Typography
          variant="h4"
          sx={{
            fontWeight: 700,
            color: 'primary.main',
            mb: 1,
            borderBottom: '2px solid primary.main',
            display: 'inline-block',
            pb: 0.5,
          }}
        >
          {certificate.studentName}
        </Typography>

        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
          Register Number: {certificate.registerNumber}
        </Typography>

        <Typography variant="body1" sx={{ mb: 1, mt: 2 }}>
          {certificate.examTitle
            ? `has successfully completed the examination: ${certificate.examTitle}`
            : `has demonstrated excellence in the subject: ${certificate.subjectName}`}
        </Typography>

        <Divider sx={{ my: 2 }} />

        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', mt: 2 }}>
          <Box sx={{ textAlign: 'left' }}>
            <Typography variant="caption" color="text.secondary">Certificate No:</Typography>
            <Typography variant="body2" fontWeight={600}>{certificate.certificateNumber}</Typography>
          </Box>
          <Box sx={{ textAlign: 'center' }}>
            {certificate.qrCode && (
              <Box
                component="img"
                src={certificate.qrCode}
                alt="QR Code"
                sx={{ width: 60, height: 60 }}
              />
            )}
            <Typography variant="caption" color="text.secondary">Scan to verify</Typography>
          </Box>
          <Box sx={{ textAlign: 'right' }}>
            <Typography variant="caption" color="text.secondary">Date of Issue:</Typography>
            <Typography variant="body2" fontWeight={600}>
              {new Date(certificate.issuedDate).toLocaleDateString()}
            </Typography>
          </Box>
        </Box>

        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'space-between' }}>
          <Box sx={{ textAlign: 'center', minWidth: 150 }}>
            <Divider sx={{ mb: 1 }} />
            <Typography variant="body2" fontWeight={600}>{certificate.issuedByName}</Typography>
            <Typography variant="caption" color="text.secondary">Authorized Signatory</Typography>
          </Box>
          <Box sx={{ textAlign: 'center', minWidth: 150 }}>
            <Divider sx={{ mb: 1 }} />
            <Typography variant="body2" fontWeight={600}>Director</Typography>
            <Typography variant="caption" color="text.secondary">EVALORITHM</Typography>
          </Box>
        </Box>

        {certificate.digitalSignature && (
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 2 }}>
            Digitally Signed | {certificate.digitalSignature}
          </Typography>
        )}
      </Box>
    </Paper>
  );
};

export default CertificatePreview;
