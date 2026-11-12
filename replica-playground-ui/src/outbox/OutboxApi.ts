import type { Client } from '../api/createClient';
import type { OutboxRow } from './OutboxRow';

export class OutboxApi {
	constructor(private readonly client: Client) {}

	list(): Promise<OutboxRow[]> {
		return this.client.get('/replication/outbox');
	}
}
