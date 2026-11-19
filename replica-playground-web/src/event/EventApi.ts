import type {Client} from '../api/createClient';
import type {EventType} from './EventType';

export class EventApi {
  constructor(private readonly client: Client) {
  }

  list(): Promise<EventType[]> {
    return this.client.get('/replication/events');
  }
}
