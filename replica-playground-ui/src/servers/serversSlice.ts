import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export const SERVERS_KEY = 'replica-playground-servers';

export interface ServersState {
	master: string;
	selected?: string;
}

const defaults: ServersState = { master: 'http://localhost:8080' };

const clean = (url: string) => url.trim().replace(/\/+$/, '');

function load(): ServersState {
	try {
		const saved = JSON.parse(localStorage.getItem(SERVERS_KEY) ?? 'null');
		return saved?.master ? { master: saved.master, selected: saved.selected } : defaults;
	} catch {
		return defaults;
	}
}

const serversSlice = createSlice({
	name: 'servers',
	initialState: load,
	reducers: {
		setMaster: (state, action: PayloadAction<string>) => {
			state.master = clean(action.payload);
			state.selected = undefined;
		},
		select: (state, action: PayloadAction<string>) => {
			state.selected = action.payload;
		},
	},
});

export const { select, setMaster } = serversSlice.actions;
export const serversReducer = serversSlice.reducer;
