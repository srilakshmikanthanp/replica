import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import { type SubmitEvent, useState } from 'react';
import { DataTable, TableCell, TableRow } from '../components/DataTable';
import { FormCard } from '../components/FormCard';
import { Layout } from '../components/Layout';
import { useAppDispatch } from '../store';
import { setMaster } from './serversSlice';
import { ServerStatus } from './ServerStatus';
import { useServers } from './useServers';

export function ServersPage() {
	const dispatch = useAppDispatch();
	const { master, slaves } = useServers();
	const [masterUrl, setMasterUrl] = useState(master);

	const saveMaster = (event: SubmitEvent) => {
		event.preventDefault();
		dispatch(setMaster(masterUrl));
	};

	return (
		<Layout title="Servers">
			<FormCard onSubmit={saveMaster}>
				<TextField size="small" type="url" label="Master URL" value={masterUrl} onChange={(event) => setMasterUrl(event.target.value)} required sx={{ flex: '1 1 200px' }} />
				<Button type="submit" variant="contained">Set master</Button>
				<ServerStatus url={master} />
			</FormCard>
			<DataTable columns={['Slave', 'URL', 'State']}>
				{slaves.map((slave) => (
					<TableRow key={slave.url}>
						<TableCell>{slave.name}</TableCell>
						<TableCell>{slave.url}</TableCell>
						<TableCell><ServerStatus url={slave.url} /></TableCell>
					</TableRow>
				))}
			</DataTable>
		</Layout>
	);
}
