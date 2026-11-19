import Chip from '@mui/material/Chip';

export type SyncState = 'in sync' | 'different' | 'missing' | 'offline';

const colors = {
  'in sync': 'success',
  different: 'warning',
  missing: 'error',
  offline: 'default',
} as const;

export function syncState<T>(row: T, slaveRows: T[] | undefined, key: (row: T) => string, content: (row: T) => string): SyncState {
  if (!slaveRows) return 'offline';

  const copy = slaveRows.find((candidate) => key(candidate) === key(row));
  if (!copy) return 'missing';

  return content(copy) === content(row) ? 'in sync' : 'different';
}

export function SyncStatus({state}: { state: SyncState }) {
  return <Chip size="small" variant="outlined" color={colors[state]} label={state}/>;
}
