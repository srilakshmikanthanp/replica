export interface InboxRow {
	id: number;
	source: string;
	messageId: number;
	eventType: string;
	payload: string;
	createdAt: number;
	receivedAt: number;
	processedAt: number | null;
}
