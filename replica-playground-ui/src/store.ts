import { configureStore } from '@reduxjs/toolkit';
import { useDispatch, useSelector } from 'react-redux';
import { noticeReducer } from './notice/noticeSlice';
import { SERVERS_KEY, serversReducer } from './servers/serversSlice';

export const store = configureStore({ reducer: { servers: serversReducer, notice: noticeReducer } });

store.subscribe(() => localStorage.setItem(SERVERS_KEY, JSON.stringify(store.getState().servers)));

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export const useAppDispatch = useDispatch.withTypes<AppDispatch>();
export const useAppSelector = useSelector.withTypes<RootState>();
