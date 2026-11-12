export interface Product {
	sku: string;
	categoryCode: string;
	name: string;
	price: number;
	tags: string[];
	updatedAt: string;
}

export interface ProductInput {
	categoryCode: string;
	name: string;
	price: number;
}
