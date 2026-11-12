import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { NavLink } from 'react-router-dom';

const pages = [
	{ to: '/', label: 'Playground' },
	{ to: '/categories', label: 'Categories' },
	{ to: '/tags', label: 'Tags' },
	{ to: '/products', label: 'Products' },
	{ to: '/stations', label: 'Stations' },
	{ to: '/outbox', label: 'Outbox' },
	{ to: '/inbox', label: 'Inbox' },
	{ to: '/servers', label: 'Servers' },
];

export function Nav() {
	return (
		<AppBar position="static" color="default" elevation={0} sx={{ borderBottom: 1, borderColor: 'divider' }}>
			<Toolbar sx={{ gap: 0.5, flexWrap: 'wrap', py: 0.5 }}>
				<Typography variant="subtitle1" sx={{ mr: 2, fontWeight: 600 }}>Replica playground</Typography>
				{pages.map((page) => (
					<Button
						key={page.to}
						component={NavLink}
						to={page.to}
						end
						color="inherit"
						sx={{ '&.active': { bgcolor: 'action.selected' } }}
					>
						{page.label}
					</Button>
				))}
			</Toolbar>
		</AppBar>
	);
}
