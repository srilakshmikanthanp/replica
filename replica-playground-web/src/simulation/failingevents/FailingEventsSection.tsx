import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import Paper from '@mui/material/Paper';
import type {Slave} from '../../servers/useServers';
import {useFailingEvents} from './useFailingEvents';

export function FailingEventsSection({slave}: { slave: Slave }) {
  return (
    <Paper variant="outlined" sx={{p: 2}}>
      <EventTypeChecklist slave={slave}/>
    </Paper>
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
