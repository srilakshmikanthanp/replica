import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Typography from '@mui/material/Typography';
import {useQueries} from '@tanstack/react-query';
import {DataTable, TableCell, TableRow} from '../components/DataTable';
import {createApi} from '../servers/createApi';
import {useServers} from '../servers/useServers';
import type {Simulation} from './Simulation';

function inSync(source: Simulation[] | undefined, replica: Simulation[] | undefined) {
  if (!source || !replica || source.length !== replica.length) return false;

  for (let i = 0; i < source.length; i++) {
    if (source[i].name !== replica[i].name || source[i].lastWrite !== replica[i].lastWrite) return false;
  }

  return true;
}

function SlaveState({failed, source, replica}: { failed: boolean; source?: Simulation[]; replica?: Simulation[] }) {
  if (failed) return <Chip size="small" color="error" label="down"/>;
  if (inSync(source, replica)) return <Chip size="small" color="success" label="in sync"/>;
  return <Chip size="small" color="warning" label="not in sync"/>;
}

export function SimulationComparison() {
  const {master, slaves} = useServers();

  const [masterTable, ...replicas] = useQueries({
    queries: [master, ...slaves.map((slave) => slave.url)].map((url) => ({
      queryKey: ['simulation-table', url],
      queryFn: () => createApi(url).simulations.list(),
      refetchInterval: 1000,
      retry: false,
    })),
  });
  const source = masterTable.data;

  return (
    <Box sx={{display: 'grid', gridTemplateColumns: {xs: '1fr', md: '2fr 1fr'}, gap: 3, alignItems: 'start'}}>
      <Box>
        <Typography variant="h6" sx={{mb: 1}}>Master</Typography>
        <DataTable columns={['Row', 'Last write']}>
          {source?.length === 0 && (
            <TableRow>
              <TableCell colSpan={2}>No rows yet</TableCell>
            </TableRow>
          )}
          {(source ?? []).map((row) => (
            <TableRow key={row.name}>
              <TableCell>{row.name}</TableCell>
              <TableCell>{row.lastWrite}</TableCell>
            </TableRow>
          ))}
        </DataTable>
      </Box>
      <Box>
        <Typography variant="h6" sx={{mb: 1}}>Slaves</Typography>
        <DataTable columns={['Slave', 'State']}>
          {slaves.map((slave, index) => (
            <TableRow key={slave.url}>
              <TableCell>{slave.name}</TableCell>
              <TableCell>
                <SlaveState failed={replicas[index]?.isError} source={source} replica={replicas[index]?.data}/>
              </TableCell>
            </TableRow>
          ))}
        </DataTable>
      </Box>
    </Box>
  );
}
