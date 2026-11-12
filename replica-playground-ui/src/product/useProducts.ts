import { useQuery } from '@tanstack/react-query';
import type { Category } from '../category/Category';
import { useSelectedServerApi, useServers } from '../servers/useServers';
import type { Tag } from '../tag/Tag';
import type { Product } from './Product';

interface ProductsData {
	products: Product[];
	categories: Category[];
	tags: Tag[];
}

const noProductsData: ProductsData = { products: [], categories: [], tags: [] };

export function useProducts() {
	const { selected } = useServers();
	const api = useSelectedServerApi();

	const { data = noProductsData, isError } = useQuery({
		queryKey: ['products', selected],
		queryFn: async (): Promise<ProductsData> => {
			const [products, categories, tags] = await Promise.all([api.products.list(), api.categories.list(), api.tags.list()]);
			return { products, categories, tags };
		},
	});

	return { ...data, isError };
}
