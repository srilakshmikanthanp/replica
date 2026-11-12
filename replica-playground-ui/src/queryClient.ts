import { MutationCache, QueryClient } from '@tanstack/react-query';
import { store } from './store';
import { showError } from './notice/noticeSlice';

export const queryClient = new QueryClient({
	defaultOptions: { queries: { refetchInterval: 1000, retry: false } },
	mutationCache: new MutationCache({
		onError: (error) => store.dispatch(showError(error.message)),
		onSuccess: () => queryClient.invalidateQueries(),
	}),
});
