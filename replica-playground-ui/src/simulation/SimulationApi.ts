import type {Client} from '../api/createClient';
import type {FailingEvents} from './failingevents/FailingEvents';

export class SimulationApi {
  constructor(private readonly client: Client) {
  }

  outage(): Promise<{ down: boolean }> {
    return this.client.get('/simulations/slave-outage');
  }

  startOutage(): Promise<{ down: boolean }> {
    return this.client.put('/simulations/slave-outage');
  }

  endOutage(): Promise<{ down: boolean }> {
    return this.client.delete('/simulations/slave-outage');
  }

  failingEvents(): Promise<FailingEvents> {
    return this.client.get('/simulations/failing-events');
  }

  updateFailingEvents(failingEventTypes: string[]): Promise<FailingEvents> {
    return this.client.put('/simulations/failing-events', {failingEventTypes});
  }
}
