import { useQuery } from '@tanstack/react-query';
import { useSelectedServerApi, useServers } from '../servers/useServers';

export function useOutbox() {
	const { selected } = useServers();
	const api = useSelectedServerApi();
	const { data = [], isError } = useQuery({ queryKey: ['outbox', selected], queryFn: () => api.outbox.list() });

	return { rows: data, isError };
}
