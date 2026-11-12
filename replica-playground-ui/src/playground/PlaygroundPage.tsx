import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useServers } from '../servers/useServers';
import { Layout } from '../components/Layout';
import { useServerCounts } from './useServerCounts';

function Count({ label, value }: { label: string; value?: number }) {
	return (
		<Box>
			<Typography variant="h4">{value ?? '-'}</Typography>
			<Typography variant="body2" sx={{ color: 'text.secondary' }}>{label}</Typography>
		</Box>
	);
}

function Panel({ title, url }: { title: string; url: string }) {
	const { counts, isError: offline } = useServerCounts(url);

	return (
		<Paper variant="outlined" sx={{ p: 2 }}>
			<Stack direction="row" spacing={1} sx={{ alignItems: 'center' }}>
				<Typography variant="h6">{title}</Typography>
				<Chip size="small" color={offline ? 'warning' : 'success'} label={offline ? 'offline' : 'online'} />
			</Stack>
			<Typography variant="body2" sx={{ color: 'text.secondary', mb: 2 }}>{url}</Typography>
			<Stack direction="row" spacing={4}>
				<Count label="Categories" value={counts?.categories} />
				<Count label="Tags" value={counts?.tags} />
				<Count label="Products" value={counts?.products} />
			</Stack>
		</Paper>
	);
}

export function PlaygroundPage() {
	const { master, slaves } = useServers();

	return (
		<Layout title="Playground">
			<Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: 2, alignItems: 'start' }}>
				<Panel title="Master" url={master} />
				{slaves.map((slave) => <Panel key={slave.url} title={`Slave ${slave.name}`} url={slave.url} />)}
			</Box>
		</Layout>
	);
}
