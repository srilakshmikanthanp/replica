import type {ReactNode} from 'react';
import {DataTable, TableCell, TableRow} from './DataTable';

interface SlaveTableProps<T> {
  columns: string[];
  rows: T[];
  rowKey: (row: T) => string;
  cells: (row: T) => ReactNode[];
}

export function SlaveTable<T>({columns, rows, rowKey, cells}: SlaveTableProps<T>) {
  return (
    <DataTable columns={columns}>
      {rows.map((row) => (
        <TableRow key={rowKey(row)}>
          {cells(row).map((cell, index) => <TableCell key={index}>{cell}</TableCell>)}
        </TableRow>
      ))}
    </DataTable>
  );
}
