export interface Health {
	status: string;
	role: 'master' | 'slave';
	name: string;
}
