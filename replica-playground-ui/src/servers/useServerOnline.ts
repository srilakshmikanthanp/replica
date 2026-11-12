import { useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { createApi } from './createApi';

export function useServerOnline(url: string) {
	const api = useMemo(() => createApi(url), [url]);
	const { data, isError } = useQuery({ queryKey: ['status', url], queryFn: () => api.health.get(), refetchInterval: 3000 });

	return data?.status === 'UP' && !isError;
}
