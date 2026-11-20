import type {ReactNode} from 'react';
import type {Api} from '../servers/createApi';
import {type SlaveCopy, useSlaveCopies} from '../servers/useSlaveCopies';
import {DataTable, TableCell, TableRow} from './DataTable';
import {type SyncState, SyncStatus} from './SyncStatus';

const extraCell = {textDecoration: 'line-through', color: 'text.disabled'};

interface MasterTableProps<T> {
  name: string;
  rows: T[];
  rowKey: (row: T) => string;
  columns: string[];
  copies: (api: Api) => Promise<T[]>;
  compare: (row: T, copy: T) => boolean;
  cells: (row: T, extra: boolean) => ReactNode[];
  actions: (row: T) => ReactNode;
}

function extraRows<T>(rows: T[], copies: SlaveCopy<T>[], rowKey: (row: T) => string): T[] {
  const seen = new Set(rows.map(rowKey));
  const extra: T[] = [];

  for (const copy of copies) {
    for (const row of copy.rows ?? []) {
      if (seen.has(rowKey(row))) continue;
      seen.add(rowKey(row));
      extra.push(row);
    }
  }

  return extra;
}

export function MasterTable<T>({
  name,
  copies,
  columns,
  rows,
  rowKey,
  compare,
  cells,
  actions,
}: MasterTableProps<T>) {
  const slaveCopies = useSlaveCopies(name, copies);
  const entries = [...rows.map((row) => ({row, extra: false})), ...extraRows(rows, slaveCopies, rowKey).map((row) => ({row, extra: true}))];

  const state = (row: T, extra: boolean, slaveRows: T[] | undefined): SyncState => {
    if (!slaveRows) return 'offline';
    const copy = slaveRows.find((candidate) => rowKey(candidate) === rowKey(row));
    if (extra) return copy ? 'extra' : 'absent';
    if (!copy) return 'missing';
    return compare(row, copy) ? 'in sync' : 'different';
  };

  return (
    <DataTable columns={[...columns, ...slaveCopies.map((copy) => copy.slave.name), '']}>
      {entries.map(({row, extra}) => (
        <TableRow key={rowKey(row)}>
          {cells(row, extra).map((cell, index) => (
            <TableCell key={index} sx={extra ? extraCell : undefined}>
              {cell}
            </TableCell>
          ))}
          {slaveCopies.map((copy) => (
            <TableCell key={copy.slave.url}>
              <SyncStatus state={state(row, extra, copy.rows)}/>
            </TableCell>
          ))}
          <TableCell align="right">{!extra && actions(row)}</TableCell>
        </TableRow>
      ))}
    </DataTable>
  );
}
