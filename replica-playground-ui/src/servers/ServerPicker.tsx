import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import { useAppDispatch } from '../store';
import { select } from './serversSlice';
import { useServers } from './useServers';

export function ServerPicker() {
	const dispatch = useAppDispatch();
	const { master, slaves, selected } = useServers();

	return (
		<TextField select size="small" label="Server" value={selected} onChange={(event) => dispatch(select(event.target.value))} sx={{ minWidth: { sm: 300 } }}>
			<MenuItem value={master}>Master - {master}</MenuItem>
			{slaves.map((slave) => <MenuItem key={slave.url} value={slave.url}>{slave.name} - {slave.url}</MenuItem>)}
		</TextField>
	);
}
