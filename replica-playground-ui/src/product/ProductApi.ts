import type { Client } from '../api/createClient';
import type { Product, ProductInput } from './Product';

export class ProductApi {
	constructor(private readonly client: Client) {}

	list(filter: { category?: string; tag?: string } = {}): Promise<Product[]> {
		return this.client.get('/products', { params: { category: filter.category || undefined, tag: filter.tag || undefined } });
	}

	find(sku: string): Promise<Product> {
		return this.client.get(`/products/${encodeURIComponent(sku)}`);
	}

	save(sku: string, input: ProductInput): Promise<Product> {
		return this.client.put(`/products/${encodeURIComponent(sku)}`, input);
	}

	remove(sku: string): Promise<void> {
		return this.client.delete(`/products/${encodeURIComponent(sku)}`);
	}

	addTag(sku: string, tag: string): Promise<Product> {
		return this.client.put(`/products/${encodeURIComponent(sku)}/tags/${encodeURIComponent(tag)}`);
	}

	removeTag(sku: string, tag: string): Promise<Product> {
		return this.client.delete(`/products/${encodeURIComponent(sku)}/tags/${encodeURIComponent(tag)}`);
	}
}
