import type { Client } from '../api/createClient';
import type { Station } from './Station';

export class StationApi {
	constructor(private readonly client: Client) {}

	list(): Promise<Station[]> {
		return this.client.get('/stations');
	}

	find(name: string): Promise<Station> {
		return this.client.get(`/stations/${encodeURIComponent(name)}`);
	}

	create(name: string, url: string): Promise<Station> {
		return this.client.post('/stations', { name, url });
	}
}
