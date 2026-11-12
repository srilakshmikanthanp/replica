import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import type { ReactNode } from 'react';

export function DataTable({ columns, children }: { columns: string[]; children: ReactNode }) {
	return (
		<TableContainer component={Paper} variant="outlined">
			<Table size="small" sx={{ minWidth: 480 }}>
				<TableHead>
					<TableRow>{columns.map((column, index) => <TableCell key={index}>{column}</TableCell>)}</TableRow>
				</TableHead>
				<TableBody>{children}</TableBody>
			</Table>
		</TableContainer>
	);
}

export { TableCell, TableRow };
