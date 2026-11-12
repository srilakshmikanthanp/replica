import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { FormCard } from '../../components/FormCard';
import { useMasterApi, useServers } from '../../servers/useServers';
import { useSlaveOutage } from './useSlaveOutage';

const ROW = 'outage-demo';

export function SlaveOutageSection() {
	const { slaves } = useServers();
	const masterApi = useMasterApi();
	const [chosen, setChosen] = useState('');
	const [writes, setWrites] = useState('20');

	const slave = slaves.find((candidate) => candidate.name === chosen) ?? slaves[0];
	const write = useMutation({ mutationFn: () => masterApi.simulations.parallelWrites(ROW, Number(writes), 1) });

	return (
		<>
			<Typography variant="h6" sx={{ mt: 3, mb: 1 }}>Slave down and catch-up</Typography>
			<Alert severity="info" sx={{ mb: 2 }}>
				Take a slave down, write on the master, and watch the changes wait. Bring the slave back and they are delivered in order until it catches up.
				The slave process keeps running but refuses incoming messages until it is brought back.
			</Alert>
			{slave
				? <Controls slaves={slaves} slave={slave} onChoose={setChosen} writes={writes} onWrites={setWrites} writing={write.isPending} onWrite={() => write.mutate()} />
				: <Alert severity="warning">Add a slave station first.</Alert>}
		</>
	);
}

interface ControlsProps {
	slaves: { name: string; url: string }[];
	slave: { name: string; url: string };
	onChoose: (name: string) => void;
	writes: string;
	onWrites: (value: string) => void;
	writing: boolean;
	onWrite: () => void;
}

function Controls({ slaves, slave, onChoose, writes, onWrites, writing, onWrite }: ControlsProps) {
	const { down, toggle, toggling } = useSlaveOutage(slave.url);

	return (
		<FormCard onSubmit={(event) => event.preventDefault()}>
			<TextField select size="small" label="Slave" value={slave.name} onChange={(event) => onChoose(event.target.value)} sx={{ minWidth: 160 }}>
				{slaves.map((candidate) => <MenuItem key={candidate.name} value={candidate.name}>{candidate.name}</MenuItem>)}
			</TextField>
			<Button variant="outlined" color={down ? 'success' : 'warning'} onClick={toggle} disabled={toggling}>
				{down ? 'Bring slave back' : 'Take slave down'}
			</Button>
			<TextField size="small" type="number" label="Writes" value={writes} onChange={(event) => onWrites(event.target.value)} slotProps={{ htmlInput: { min: 1 } }} />
			<Button variant="contained" onClick={onWrite} disabled={writing}>{writing ? 'Writing...' : 'Write on master'}</Button>
		</FormCard>
	);
}
