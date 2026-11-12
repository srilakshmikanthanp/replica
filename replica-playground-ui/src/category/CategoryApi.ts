import type { Client } from '../api/createClient';
import type { Category } from './Category';
import type { ParallelWritesResult } from './simulation/ParallelWritesResult';

export class CategoryApi {
	constructor(private readonly client: Client) {}

	list(): Promise<Category[]> {
		return this.client.get('/categories');
	}

	find(code: string): Promise<Category> {
		return this.client.get(`/categories/${encodeURIComponent(code)}`);
	}

	save(code: string, name: string): Promise<Category> {
		return this.client.put(`/categories/${encodeURIComponent(code)}`, { name });
	}

	remove(code: string): Promise<void> {
		return this.client.delete(`/categories/${encodeURIComponent(code)}`);
	}

	simulateParallelWrites(code: string, writes: number, parallel: number): Promise<ParallelWritesResult> {
		return this.client.post(`/categories/${encodeURIComponent(code)}/simulations/parallel-writes`, { writes, parallel });
	}
}
