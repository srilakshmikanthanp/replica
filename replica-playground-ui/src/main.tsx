import { QueryClientProvider } from '@tanstack/react-query';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Notifier } from './notice/Notifier';
import { queryClient } from './queryClient';
import { CategoriesPage } from './category/CategoriesPage';
import { InboxPage } from './inbox/InboxPage';
import { OutboxPage } from './outbox/OutboxPage';
import { PlaygroundPage } from './playground/PlaygroundPage';
import { ProductsPage } from './product/ProductsPage';
import { ServersPage } from './servers/ServersPage';
import { StationsPage } from './station/StationsPage';
import { TagsPage } from './tag/TagsPage';
import { store } from './store';
import { theme } from './theme';

createRoot(document.querySelector('#root')!).render(
	<ThemeProvider theme={theme}>
		<CssBaseline />
		<Provider store={store}>
			<QueryClientProvider client={queryClient}>
				<Notifier />
				<BrowserRouter>
					<Routes>
						<Route path="/" element={<PlaygroundPage />} />
						<Route path="/categories" element={<CategoriesPage />} />
						<Route path="/tags" element={<TagsPage />} />
						<Route path="/products" element={<ProductsPage />} />
						<Route path="/stations" element={<StationsPage />} />
						<Route path="/outbox" element={<OutboxPage />} />
						<Route path="/inbox" element={<InboxPage />} />
						<Route path="/servers" element={<ServersPage />} />
					</Routes>
				</BrowserRouter>
			</QueryClientProvider>
		</Provider>
	</ThemeProvider>,
);
