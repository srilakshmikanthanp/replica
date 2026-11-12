import Alert from '@mui/material/Alert';

export function OfflineAlert({ offline }: { offline: boolean }) {
	return offline ? <Alert severity="error" sx={{ mb: 2 }}>Cannot reach the server.</Alert> : null;
}
