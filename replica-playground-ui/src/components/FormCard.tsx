import Paper from '@mui/material/Paper';
import type { ReactNode, SubmitEventHandler } from 'react';

export function FormCard({ onSubmit, children }: { onSubmit: SubmitEventHandler; children: ReactNode }) {
	return (
		<Paper
			variant="outlined"
			component="form"
			onSubmit={onSubmit}
			sx={{ p: 2, mb: 2, display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 1.5, '& button[type=submit]': { whiteSpace: 'nowrap' } }}
		>
			{children}
		</Paper>
	);
}
