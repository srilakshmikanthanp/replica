import { useQuery } from '@tanstack/react-query';
import { useSelectedServerApi, useServers } from '../servers/useServers';

export function useTags() {
	const { selected } = useServers();
	const api = useSelectedServerApi();
	const { data = [], isError } = useQuery({ queryKey: ['tags', selected], queryFn: () => api.tags.list() });

	return { tags: data, isError };
}
