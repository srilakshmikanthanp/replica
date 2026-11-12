import Chip from '@mui/material/Chip';
import { OfflineAlert } from '../components/OfflineAlert';
import { DataTable, TableCell, TableRow } from '../components/DataTable';
import { Layout } from '../components/Layout';
import { useInbox } from './useInbox';
import { Payload } from '../components/Payload';
import { formatTime } from '../utility/format';
import { ServerPicker } from '../servers/ServerPicker';

export function InboxPage() {
	const { rows, isError } = useInbox();

	return (
		<Layout title="Inbox" actions={<ServerPicker />}>
			<OfflineAlert offline={isError} />
			<DataTable columns={['ID', 'From', 'Message', 'Event', 'Payload', 'Received', 'State']}>
				{rows.map((row) => (
					<TableRow key={row.id}>
						<TableCell>{row.id}</TableCell>
						<TableCell>{row.source}</TableCell>
						<TableCell>{row.messageId}</TableCell>
						<TableCell>{row.eventType}</TableCell>
						<TableCell><Payload value={row.payload} /></TableCell>
						<TableCell>{formatTime(row.receivedAt)}</TableCell>
						<TableCell>
							<Chip size="small" color={row.processedAt === null ? 'warning' : 'success'} label={row.processedAt === null ? 'pending' : `applied ${formatTime(row.processedAt)}`} />
						</TableCell>
					</TableRow>
				))}
			</DataTable>
		</Layout>
	);
}
