import Alert from '@mui/material/Alert';
import {Layout} from '../components/Layout';
import {ServerPicker} from '../servers/ServerPicker';
import {useServers} from '../servers/useServers';
import {FailingEventsSection} from './failingevents/FailingEventsSection';
import {SlaveOutageSection} from './slaveoutage/SlaveOutageSection';

export function SimulationsPage() {
  const {readOnly} = useServers();

  return (
    <Layout title="Simulations" actions={<ServerPicker/>}>
      {readOnly
        ? <Alert severity="info">Simulations run on the master. Select the master to start one.</Alert>
        : <><SlaveOutageSection/><FailingEventsSection/></>}
    </Layout>
  );
}
