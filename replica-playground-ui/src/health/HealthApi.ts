import type { Client } from '../api/createClient';
import type { Health } from './Health';

export class HealthApi {
	constructor(private readonly client: Client) {}

	get(): Promise<Health> {
		return this.client.get('/health');
	}
}
