import { useQuery } from '@tanstack/react-query';
import { useSelectedServerApi, useServers } from '../servers/useServers';

export function useCategories() {
	const { selected } = useServers();
	const api = useSelectedServerApi();
	const { data = [], isError } = useQuery({ queryKey: ['categories', selected], queryFn: () => api.categories.list() });

	return { categories: data, isError };
}
