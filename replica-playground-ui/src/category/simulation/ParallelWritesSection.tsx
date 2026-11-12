import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useMutation } from '@tanstack/react-query';
import { type SubmitEvent, useState } from 'react';
import { DataTable, TableCell, TableRow } from '../../components/DataTable';
import { FormCard } from '../../components/FormCard';
import { useMasterApi, useServers } from '../../servers/useServers';
import { useCategoryValues } from './useCategoryValues';

export function ParallelWritesSection() {
	const { master, slaves } = useServers();
	const masterApi = useMasterApi();
	const [open, setOpen] = useState(false);
	const [code, setCode] = useState('SIM1');
	const [writes, setWrites] = useState('50');
	const [parallel, setParallel] = useState('10');

	const servers = [{ label: 'Master', url: master }, ...slaves.map((slave) => ({ label: slave.name, url: slave.url }))];

	const values = useCategoryValues(servers.map(({ url }) => url), code, open);
	const simulate = useMutation({ mutationFn: () => masterApi.categories.simulateParallelWrites(code, Number(writes), Number(parallel)) });
	const result = simulate.isPending ? undefined : simulate.data;

	const start = (event: SubmitEvent) => {
		event.preventDefault();
		simulate.mutate();
	};

	return (
		<Accordion variant="outlined" expanded={open} onChange={(_, expanded) => setOpen(expanded)} sx={{ mt: 2 }}>
			<AccordionSummary>
				<Typography>Simulate parallel writes</Typography>
			</AccordionSummary>
			<AccordionDetails>
				<Alert severity="info" sx={{ mb: 2 }}>
					The master runs the writes on its own threads, all updating the same category at once, then this compares its final value on every server.
				</Alert>
				<FormCard onSubmit={start}>
					<TextField size="small" label="Category code" value={code} onChange={(event) => setCode(event.target.value)} required />
					<TextField size="small" type="number" label="Writes" value={writes} onChange={(event) => setWrites(event.target.value)} slotProps={{ htmlInput: { min: 1 } }} required />
					<TextField size="small" type="number" label="At the same time" value={parallel} onChange={(event) => setParallel(event.target.value)} slotProps={{ htmlInput: { min: 1 } }} required />
					<Button type="submit" variant="contained" disabled={simulate.isPending}>{simulate.isPending ? 'Running...' : 'Start'}</Button>
				</FormCard>
				{result && (
					<Alert severity={result.failed > 0 ? 'error' : result.refused > 0 ? 'warning' : 'success'} sx={{ mb: 2 }}>
						{result.ok} written, {result.refused} refused (a station was backfilling or a lock timed out), {result.failed} failed, in {result.millis} ms.
					</Alert>
				)}
				<DataTable columns={['Server', 'Value', 'State']}>
					{servers.map(({ label, url }, index) => (
						<TableRow key={url}>
							<TableCell>{label} - {url}</TableCell>
							<TableCell>{values[index] ?? 'missing'}</TableCell>
							<TableCell>
								{index === 0
									? <Chip size="small" label="source" />
									: <Chip size="small" color={values[index] === values[0] ? 'success' : 'warning'} label={values[index] === values[0] ? 'in sync' : 'behind or different'} />}
							</TableCell>
						</TableRow>
					))}
				</DataTable>
			</AccordionDetails>
		</Accordion>
	);
}
