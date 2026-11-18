import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {useMemo} from 'react';
import {createApi} from '../../servers/createApi';

export function useFailingEvents(url: string) {
  const queryClient = useQueryClient();
  const api = useMemo(() => createApi(url), [url]);

  const {data} = useQuery({
    queryKey: ['failing-events', url],
    queryFn: () => api.simulations.failingEvents(),
    refetchInterval: 1000,
    retry: false,
  });

  const update = useMutation({
    mutationFn: (failingEventTypes: string[]) => api.simulations.updateFailingEvents(failingEventTypes),
    onSuccess: (saved) => queryClient.setQueryData(['failing-events', url], saved),
  });

  return {state: data, update: update.mutate, updating: update.isPending};
}
