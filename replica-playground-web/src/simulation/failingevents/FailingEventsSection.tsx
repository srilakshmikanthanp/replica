import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Checkbox from '@mui/material/Checkbox';
import Chip from '@mui/material/Chip';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import {useEventTypes} from '../../event/useEventTypes';
import type {Slave} from '../../servers/useServers';
import {eventStatusOf} from './EventStatus';
import {useFailingEvents} from './useFailingEvents';

function EventTypeChecklist({slave}: { slave: Slave }) {
  const {state, update, updating} = useFailingEvents(slave.url);
  const eventTypes = useEventTypes(slave.url);

  if (!state || !eventTypes) {
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
    <Box sx={{display: 'grid', gridTemplateColumns: {xs: '1fr', sm: '1fr 1fr'}, gap: 1}}>
      {eventTypes.map((eventType) => {
        const status = eventStatusOf(eventType, failing);

        return (
          <Stack key={eventType.type} direction="row" sx={{alignItems: 'center'}}>
            <Checkbox checked={failing.includes(eventType.type)} disabled={updating} onChange={() => toggle(eventType.type)}/>
            <Typography variant="body2" sx={{flexGrow: 1}}>{eventType.type}</Typography>
            <Chip size="small" color={status.color} variant="outlined" label={status.label}/>
          </Stack>
        );
      })}
    </Box>
  );
}

export function FailingEventsSection({slave}: { slave: Slave }) {
  return (
    <Paper variant="outlined" sx={{p: 2}}>
      <EventTypeChecklist slave={slave}/>
    </Paper>
  );
}
