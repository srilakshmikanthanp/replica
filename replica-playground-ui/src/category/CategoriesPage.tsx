import { useMutation } from '@tanstack/react-query';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { type SubmitEvent, useState } from 'react';
import { ServerPicker } from '../servers/ServerPicker';
import { useSelectedServerApi, useServers } from '../servers/useServers';
import { useCategories } from './useCategories';
import { OfflineAlert } from '../components/OfflineAlert';
import { DataTable, TableCell, TableRow } from '../components/DataTable';
import { FormCard } from '../components/FormCard';
import { Layout } from '../components/Layout';
import { ParallelWritesSection } from './simulation/ParallelWritesSection';

export function CategoriesPage() {
	const { readOnly } = useServers();
	const api = useSelectedServerApi();
	const { categories, isError } = useCategories();
	const [code, setCode] = useState('');
	const [name, setName] = useState('');

	const save = useMutation({ mutationFn: () => api.categories.save(code, name) });
	const remove = useMutation({ mutationFn: (code: string) => api.categories.remove(code) });

	const submit = (event: SubmitEvent) => {
		event.preventDefault();
		save.mutate(undefined, { onSuccess: () => { setCode(''); setName(''); } });
	};

	return (
		<Layout title="Categories" actions={<ServerPicker />}>
			{readOnly && <Alert severity="info" sx={{ mb: 2 }}>Slaves are read-only. Changes come from the master.</Alert>}
			{!readOnly && <FormCard onSubmit={submit}>
				<TextField size="small" label="Code" placeholder="TOOLS" value={code} onChange={(event) => setCode(event.target.value)} required />
				<TextField size="small" label="Name" value={name} onChange={(event) => setName(event.target.value)} required sx={{ flex: '1 1 200px' }} />
				<Button type="submit" variant="contained">Save category</Button>
			</FormCard>}
			<OfflineAlert offline={isError} />
			<DataTable columns={['Code', 'Name', '']}>
				{categories.map((category) => (
					<TableRow key={category.code}>
						<TableCell>{category.code}</TableCell>
						<TableCell>{category.name}</TableCell>
						<TableCell align="right">
							{!readOnly && <Stack direction="row" spacing={1} sx={{ justifyContent: "flex-end" }}>
								<Button size="small" onClick={() => { setCode(category.code); setName(category.name); }}>Edit</Button>
								<Button size="small" color="error" onClick={() => remove.mutate(category.code)}>Delete</Button>
							</Stack>}
						</TableCell>
					</TableRow>
				))}
			</DataTable>
			{!readOnly && <ParallelWritesSection />}
		</Layout>
	);
}
