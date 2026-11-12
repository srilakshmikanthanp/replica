import axios, { type AxiosInstance } from 'axios';
import { ApiError } from './ApiError';

export type Client = AxiosInstance;

export function createClient(baseURL = ''): Client {
	const client = axios.create({ baseURL });

	client.interceptors.response.use(
		(response) => response.data,
		(error) => Promise.reject(new ApiError(error.response?.status ?? 0, error.response?.data?.error ?? error.message)),
	);

	return client;
}
