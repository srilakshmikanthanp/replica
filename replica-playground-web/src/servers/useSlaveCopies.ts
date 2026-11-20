import {useQueries} from '@tanstack/react-query';
import {type Api, createApi} from './createApi';
import {type Slave, useServers} from './useServers';

export interface SlaveCopy<T> {
  slave: Slave;
  rows?: T[];
}

export function useSlaveCopies<T>(name: string, fetchSlaveRows: (api: Api) => Promise<T[]>): SlaveCopy<T>[] {
  const {slaves} = useServers();

  const results = useQueries({
    queries: slaves.map((slave) => ({
      queryKey: ['slave-copy', name, slave.url],
      queryFn: () => fetchSlaveRows(createApi(slave.url)),
    })),
  });

  return slaves.map((slave, index) => ({
    slave,
    rows: results[index].isError ? undefined : results[index].data
  }));
}
