import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import {useState} from 'react';
import {FormCard} from '../../components/FormCard';
import {useServers} from '../../servers/useServers';
import {useSlaveOutage} from './useSlaveOutage';

export function SlaveOutageSection() {
  const {slaves} = useServers();
  const [chosen, setChosen] = useState('');

  const slave = slaves.find((candidate) => candidate.name === chosen) ?? slaves[0];

  return (
    <>
      <Typography variant="h6" sx={{mt: 4, mb: 1}}>Slave down and catch-up</Typography>
      <Alert severity="info" sx={{mb: 2}}>
        Take a slave down, make changes on the master, and watch the slave fall behind. Bring it back and the changes
        are delivered in order until it catches up.
        The slave process keeps running but refuses incoming messages until it is brought back.
      </Alert>
      {slave
        ? <Controls slaves={slaves} slave={slave} onChoose={setChosen}/>
        : <Alert severity="warning">Add a slave station first.</Alert>}
    </>
  );
}

interface ControlsProps {
  slaves: { name: string; url: string }[];
  slave: { name: string; url: string };
  onChoose: (name: string) => void;
}

function Controls({slaves, slave, onChoose}: ControlsProps) {
  const {down, toggle, toggling} = useSlaveOutage(slave.url);

  return (
    <FormCard onSubmit={(event) => event.preventDefault()}>
      <TextField select size="small" label="Slave" value={slave.name} onChange={(event) => onChoose(event.target.value)}
                 sx={{minWidth: 160}}>
        {slaves.map((candidate) => <MenuItem key={candidate.name} value={candidate.name}>{candidate.name}</MenuItem>)}
      </TextField>
      <Button variant="outlined" color={down ? 'success' : 'warning'} onClick={toggle} disabled={toggling}>
        {down ? 'Bring slave back' : 'Take slave down'}
      </Button>
    </FormCard>
  );
}
