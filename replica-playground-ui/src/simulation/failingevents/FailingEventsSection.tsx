import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import {useState} from 'react';
import {type Slave, useServers} from '../../servers/useServers';
import {useFailingEvents} from './useFailingEvents';

export function FailingEventsSection() {
  const {slaves} = useServers();
  const [chosen, setChosen] = useState('');

  const slave = slaves.find((candidate) => candidate.name === chosen) ?? slaves[0];

  return (
    <>
      <Typography variant="h6" sx={{mt: 4, mb: 1}}>Failing events</Typography>
      <Alert severity="info" sx={{mb: 2}}>
        Tick the event types the slave fails to apply, then change categories, tags or products on the master and
        watch the slave's inbox. Messages of a ticked type stay pending and are retried until you untick it.
      </Alert>
      {slave ? (
        <Paper variant="outlined" sx={{p: 2, mb: 2}}>
          <SlavePicker slaves={slaves} slave={slave} onChoose={setChosen}/>
          <EventTypeChecklist slave={slave}/>
        </Paper>
      ) : (
        <Alert severity="warning">Add a slave station first.</Alert>
      )}
    </>
  );
}

interface SlavePickerProps {
  slaves: Slave[];
  slave: Slave;
  onChoose: (name: string) => void;
}

function SlavePicker({slaves, slave, onChoose}: SlavePickerProps) {
  return (
    <TextField select size="small" label="Slave" value={slave.name} onChange={(event) => onChoose(event.target.value)} sx={{minWidth: 160, mb: 2}}>
      {slaves.map((candidate) => (
        <MenuItem key={candidate.name} value={candidate.name}>{candidate.name}</MenuItem>
      ))}
    </TextField>
  );
}

function EventTypeChecklist({slave}: { slave: Slave }) {
  const {state, update, updating} = useFailingEvents(slave.url);

  if (!state) {
    return <Alert severity="warning">{slave.name} is not reachable.</Alert>;
  }

  const failing = state.failingEventTypes;

  const toggle = (eventType: string) => {
    if (failing.includes(eventType)) {
      update(failing.filter((type) => type !== eventType));
    } else {
      update([...failing, eventType]);
    }
  };

  return (
    <Box sx={{display: 'grid', gridTemplateColumns: {xs: '1fr', sm: '1fr 1fr'}}}>
      {state.eventTypes.map((eventType) => (
        <FormControlLabel
          key={eventType}
          label={eventType}
          disabled={updating}
          control={<Checkbox checked={failing.includes(eventType)} onChange={() => toggle(eventType)}/>}
        />
      ))}
    </Box>
  );
}
