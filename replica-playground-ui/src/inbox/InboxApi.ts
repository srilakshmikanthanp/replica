import type { Client } from '../api/createClient';
import type { InboxRow } from './InboxRow';

export class InboxApi {
	constructor(private readonly client: Client) {}

	list(): Promise<InboxRow[]> {
		return this.client.get('/replication/inbox');
	}
}
