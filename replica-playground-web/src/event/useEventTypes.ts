import {useQuery} from '@tanstack/react-query';
import {useMemo} from 'react';
import {createApi} from '../servers/createApi';

export function useEventTypes(url: string) {
  const api = useMemo(() => createApi(url), [url]);

  const {data} = useQuery({
    queryKey: ['event-types', url],
    queryFn: () => api.events.list(),
    refetchInterval: false,
    retry: false,
  });

  return data;
}
