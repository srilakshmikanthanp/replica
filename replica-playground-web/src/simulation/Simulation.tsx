import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import type {ReactNode} from 'react';

export type ServerRole = 'master' | 'slave';

interface SimulationProps {
  title: string;
  description: string;
  runsOn: ServerRole;
  selectedRole: ServerRole;
  children: ReactNode;
}

export function Simulation({title, description, runsOn, selectedRole, children}: SimulationProps) {
  const supported = runsOn === selectedRole;

  return (
    <Box sx={{mb: 4, opacity: supported ? 1 : 0.6}}>
      <Typography variant="h6" sx={{mb: 1}}>{title}</Typography>
      <Alert severity="info" sx={{mb: 2}}>{description}</Alert>
      {supported ? children : <Alert severity="warning">Runs on a {runsOn}. Select a {runsOn} in the server picker to use it.</Alert>}
    </Box>
  );
}
