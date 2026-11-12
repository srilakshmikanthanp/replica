import Chip from '@mui/material/Chip';
import { useServerOnline } from './useServerOnline';

export function ServerStatus({ url }: { url: string }) {
	const online = useServerOnline(url);

	return <Chip size="small" color={online ? 'success' : 'warning'} label={online ? 'online' : 'offline'} />;
}
