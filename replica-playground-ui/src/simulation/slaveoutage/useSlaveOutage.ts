import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo } from 'react';
import { createApi } from '../../servers/createApi';

export function useSlaveOutageState(url: string) {
	const { data } = useQuery({
		queryKey: ['slave-outage', url],
		queryFn: () => createApi(url).simulations.outage(),
		refetchInterval: 1000,
		retry: false,
	});

	return data?.down ?? false;
}

export function useSlaveOutage(url: string) {
	const queryClient = useQueryClient();
	const api = useMemo(() => createApi(url), [url]);
	const down = useSlaveOutageState(url);

	const toggle = useMutation({
		mutationFn: () => (down ? api.simulations.endOutage() : api.simulations.startOutage()),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['slave-outage', url] }),
	});

	return { down, toggle: () => toggle.mutate(), toggling: toggle.isPending };
}
