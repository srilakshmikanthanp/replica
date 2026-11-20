import Chip from '@mui/material/Chip';

export type SyncState = 'in sync' | 'different' | 'missing' | 'extra' | 'absent' | 'offline';

const colors = {
  'in sync': 'success',
  different: 'warning',
  missing: 'error',
  extra: 'error',
  absent: 'default',
  offline: 'default',
} as const;

export function SyncStatus({state}: { state: SyncState }) {
  if (state === 'absent') return <>—</>;

  return <Chip size="small" variant="outlined" color={colors[state]} label={state}/>;
}
