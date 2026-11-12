import Chip from '@mui/material/Chip';
import { OfflineAlert } from '../components/OfflineAlert';
import { DataTable, TableCell, TableRow } from '../components/DataTable';
import { Layout } from '../components/Layout';
import { useOutbox } from './useOutbox';
import { Payload } from '../components/Payload';
import { formatTime } from '../utility/format';
import { ServerPicker } from '../servers/ServerPicker';

export function OutboxPage() {
	const { rows, isError } = useOutbox();

	return (
		<Layout title="Outbox" actions={<ServerPicker />}>
			<OfflineAlert offline={isError} />
			<DataTable columns={['ID', 'To', 'Event', 'Payload', 'Created', 'State']}>
				{rows.map((row) => (
					<TableRow key={row.id}>
						<TableCell>{row.id}</TableCell>
						<TableCell>{row.destination}</TableCell>
						<TableCell>{row.eventType}</TableCell>
						<TableCell><Payload value={row.payload} /></TableCell>
						<TableCell>{formatTime(row.createdAt)}</TableCell>
						<TableCell>
							<Chip size="small" color={row.publishedAt === null ? 'warning' : 'success'} label={row.publishedAt === null ? 'pending' : `sent ${formatTime(row.publishedAt)}`} />
						</TableCell>
					</TableRow>
				))}
			</DataTable>
		</Layout>
	);
}
