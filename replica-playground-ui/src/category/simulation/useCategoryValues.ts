import { useQueries } from '@tanstack/react-query';
import { createApi } from '../../servers/createApi';

async function readCategoryName(url: string, code: string) {
	try {
		return (await createApi(url).categories.find(code)).name;
	} catch {
		return null;
	}
}

export function useCategoryValues(urls: string[], code: string, enabled: boolean) {
	const results = useQueries({
		queries: urls.map((url) => ({
			queryKey: ['parallel-writes', code, url],
			queryFn: () => readCategoryName(url, code),
			enabled,
		})),
	});

	return results.map((query) => query.data ?? null);
}
