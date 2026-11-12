import Alert from '@mui/material/Alert';
import Snackbar from '@mui/material/Snackbar';
import { useAppDispatch, useAppSelector } from '../store';
import { clearError } from './noticeSlice';

export function Notifier() {
	const dispatch = useAppDispatch();
	const error = useAppSelector((state) => state.notice.error);
	const close = () => dispatch(clearError());

	return (
		<Snackbar open={error !== ''} autoHideDuration={6000} onClose={close} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
			<Alert severity="error" variant="filled" onClose={close}>{error}</Alert>
		</Snackbar>
	);
}
