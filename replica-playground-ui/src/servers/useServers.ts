import { useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { useAppSelector } from '../store';
import { createApi } from './createApi';

export interface Slave {
	name: string;
	url: string;
}

export function useMasterApi() {
	const master = useAppSelector((state) => state.servers.master);
	return useMemo(() => createApi(master), [master]);
}

export function useSlaves(): Slave[] | undefined {
	const master = useAppSelector((state) => state.servers.master);
	const masterApi = useMasterApi();

	const { data } = useQuery({
		queryKey: ['slaves', master],
		queryFn: async () => (await masterApi.stations.list()).flatMap((station) => (station.url ? [{ name: station.name, url: station.url }] : [])),
	});

	return data;
}

export function useServers() {
	const { master, selected: chosen } = useAppSelector((state) => state.servers);
	const slaves = useSlaves();
	const chosenIsKnown = chosen === undefined || slaves === undefined || chosen === master || slaves.some((slave) => slave.url === chosen);
	const selected = chosen !== undefined && chosenIsKnown ? chosen : master;
	return { master, slaves: slaves ?? [], selected, readOnly: selected !== master };
}

export function useSelectedServerApi() {
	const { selected } = useServers();
	return useMemo(() => createApi(selected), [selected]);
}
