export interface Station {
	id: number;
	name: string;
	url: string | null;
	ready: boolean;
	waiting: number;
}
