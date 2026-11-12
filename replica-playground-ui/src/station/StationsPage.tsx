import { useMutation } from '@tanstack/react-query';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import TextField from '@mui/material/TextField';
import { type SubmitEvent, useState } from 'react';
import { type Api, createApi } from '../servers/createApi';
import { ServerPicker } from '../servers/ServerPicker';
import { useSelectedServerApi, useServers } from '../servers/useServers';
import { OfflineAlert } from '../components/OfflineAlert';
import { DataTable, TableCell, TableRow } from '../components/DataTable';
import { FormCard } from '../components/FormCard';
import { Layout } from '../components/Layout';
import { useStations } from './useStations';

async function readHealth(url: string) {
	try {
		return await createApi(url).health.get();
	} catch {
		throw new Error(`Cannot reach ${url}`);
	}
}

async function addStation(api: Api, url: string) {
	const health = await readHealth(url);
	if (health.role !== 'slave') throw new Error(`${url} is a ${health.role}, not a slave`);
	return api.stations.create(health.name, url);
}

export function StationsPage() {
	const { readOnly } = useServers();
	const api = useSelectedServerApi();
	const { stations, isError } = useStations();
	const [url, setUrl] = useState('');

	const create = useMutation({ mutationFn: () => addStation(api, url) });

	const add = (event: SubmitEvent) => {
		event.preventDefault();
		create.mutate(undefined, { onSuccess: () => setUrl('') });
	};

	const pending = stations.filter((station) => !station.ready).map((station) => station.name);

	return (
		<Layout title="Stations" actions={<ServerPicker />}>
			{pending.length > 0 && (
				<Alert severity="warning" sx={{ mb: 2 }}>Changes are paused while {pending.join(', ')} is being backfilled.</Alert>
			)}
			{readOnly && <Alert severity="info" sx={{ mb: 2 }}>Stations are added on the master.</Alert>}
			{!readOnly && <FormCard onSubmit={add}>
				<TextField size="small" type="url" label="Slave URL" placeholder="http://localhost:8081" value={url} onChange={(event) => setUrl(event.target.value)} required sx={{ flex: '1 1 200px' }} />
				<Button type="submit" variant="contained">Add station</Button>
			</FormCard>}
			<OfflineAlert offline={isError} />
			<DataTable columns={['Name', 'URL', 'State', 'Waiting']}>
				{stations.map((station) => (
					<TableRow key={station.name}>
						<TableCell>{station.name}</TableCell>
						<TableCell>{station.url ?? '-'}</TableCell>
						<TableCell>
							<Chip size="small" color={station.ready ? 'success' : 'warning'} label={station.ready ? 'ready' : 'backfilling'} />
						</TableCell>
						<TableCell>{station.waiting}</TableCell>
					</TableRow>
				))}
			</DataTable>
		</Layout>
	);
}
