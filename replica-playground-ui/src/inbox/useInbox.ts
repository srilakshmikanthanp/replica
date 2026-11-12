import { useQuery } from '@tanstack/react-query';
import { useSelectedServerApi, useServers } from '../servers/useServers';

export function useInbox() {
	const { selected } = useServers();
	const api = useSelectedServerApi();
	const { data = [], isError } = useQuery({ queryKey: ['inbox', selected], queryFn: () => api.inbox.list() });

	return { rows: data, isError };
}
