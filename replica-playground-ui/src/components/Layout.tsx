import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import type { ReactNode } from 'react';
import { Nav } from './Navigation';

export function Layout({ title, actions, children }: { title: string; actions?: ReactNode; children: ReactNode }) {
	return (
		<>
			<Nav />
			<Container maxWidth="xl" sx={{ py: { xs: 2, sm: 3 } }}>
				<Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} sx={{ mb: 2, alignItems: { sm: 'center' }, justifyContent: 'space-between' }}>
					<Typography variant="h5" component="h1">{title}</Typography>
					{actions}
				</Stack>
				{children}
			</Container>
		</>
	);
}
