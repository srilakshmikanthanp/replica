import { HealthApi } from '../health/HealthApi';
import { InboxApi } from '../inbox/InboxApi';
import { OutboxApi } from '../outbox/OutboxApi';
import { CategoryApi } from '../category/CategoryApi';
import { ProductApi } from '../product/ProductApi';
import { createClient } from '../api/createClient';
import { StationApi } from '../station/StationApi';
import { TagApi } from '../tag/TagApi';

export function createApi(baseUrl = '') {
	const client = createClient(baseUrl);

	return {
		categories: new CategoryApi(client),
		tags: new TagApi(client),
		products: new ProductApi(client),
		stations: new StationApi(client),
		health: new HealthApi(client),
		outbox: new OutboxApi(client),
		inbox: new InboxApi(client),
	};
}

export type Api = ReturnType<typeof createApi>;
