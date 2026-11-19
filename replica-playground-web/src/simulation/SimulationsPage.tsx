import {Layout} from '../components/Layout';
import {ServerPicker} from '../servers/ServerPicker';
import {type Slave, useServers} from '../servers/useServers';
import {FailingEventsSection} from './failingevents/FailingEventsSection';
import {Simulation} from './Simulation';
import {SlaveOutageSection} from './slaveoutage/SlaveOutageSection';

export function SimulationsPage() {
  const {master, slaves, selected, readOnly} = useServers();

  const selectedRole = readOnly ? 'slave' : 'master';
  const server: Slave = slaves.find((slave) => slave.url === selected) ?? {name: 'master', url: master};

  return (
    <Layout title="Simulations" actions={<ServerPicker/>}>
      <Simulation
        title="Slave down and catch-up"
        description="Take a slave down, make changes on the master, and watch the slave fall behind. Bring it back and the changes are delivered in order until it catches up. The slave process keeps running but refuses incoming messages until it is brought back."
        runsOn="slave"
        selectedRole={selectedRole}
      >
        <SlaveOutageSection slave={server}/>
      </Simulation>
      <Simulation
        title="Failing events"
        description="Tick the event types the slave fails to apply, then change categories, tags or products on the master and watch the slave's inbox. Messages of a ticked type stay pending and are retried until you untick it."
        runsOn="slave"
        selectedRole={selectedRole}
      >
        <FailingEventsSection slave={server}/>
      </Simulation>
    </Layout>
  );
}
