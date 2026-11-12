import { useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { createApi } from '../servers/createApi';

export function useServerCounts(url: string) {
	const api = useMemo(() => createApi(url), [url]);

	const { data, isError } = useQuery({
		queryKey: ['counts', url],
		queryFn: async () => {
			const [categories, tags, products] = await Promise.all([api.categories.list(), api.tags.list(), api.products.list()]);
			return { categories: categories.length, tags: tags.length, products: products.length };
		},
	});

	return { counts: data, isError };
}
