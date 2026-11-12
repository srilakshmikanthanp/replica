import type { Client } from '../api/createClient';
import type { Tag } from './Tag';

export class TagApi {
	constructor(private readonly client: Client) {}

	list(): Promise<Tag[]> {
		return this.client.get('/tags');
	}

	find(name: string): Promise<Tag> {
		return this.client.get(`/tags/${encodeURIComponent(name)}`);
	}

	save(name: string): Promise<Tag> {
		return this.client.put(`/tags/${encodeURIComponent(name)}`);
	}

	remove(name: string): Promise<void> {
		return this.client.delete(`/tags/${encodeURIComponent(name)}`);
	}
}
