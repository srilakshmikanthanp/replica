import Button from '@mui/material/Button';
import {FormCard} from '../../components/FormCard';
import type {Slave} from '../../servers/useServers';
import {useSlaveOutage} from './useSlaveOutage';

export function SlaveOutageSection({slave}: { slave: Slave }) {
  const {down, toggle, toggling} = useSlaveOutage(slave.url);

  return (
    <FormCard onSubmit={(event) => event.preventDefault()}>
      <Button variant="outlined" color={down ? 'success' : 'warning'} onClick={toggle} disabled={toggling}>
        {down ? `Bring ${slave.name} back` : `Take ${slave.name} down`}
      </Button>
    </FormCard>
  );
}
