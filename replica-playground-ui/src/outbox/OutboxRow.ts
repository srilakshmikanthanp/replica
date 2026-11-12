export interface OutboxRow {
	id: number;
	destination: string;
	eventType: string;
	payload: string;
	createdAt: number;
	publishedAt: number | null;
}
