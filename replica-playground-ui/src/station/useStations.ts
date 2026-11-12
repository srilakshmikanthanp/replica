import { useQuery } from '@tanstack/react-query';
import { useSelectedServerApi, useServers } from '../servers/useServers';

export function useStations() {
	const { selected } = useServers();
	const api = useSelectedServerApi();
	const { data = [], isError } = useQuery({ queryKey: ['stations', selected], queryFn: () => api.stations.list() });

	return { stations: data, isError };
}
