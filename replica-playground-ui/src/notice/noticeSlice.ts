import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

const noticeSlice = createSlice({
	name: 'notice',
	initialState: { error: '' },
	reducers: {
		showError: (state, action: PayloadAction<string>) => {
			state.error = action.payload;
		},
		clearError: (state) => {
			state.error = '';
		},
	},
});

export const { showError, clearError } = noticeSlice.actions;
export const noticeReducer = noticeSlice.reducer;
