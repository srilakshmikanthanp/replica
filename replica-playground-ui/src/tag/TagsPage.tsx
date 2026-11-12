import { useMutation } from '@tanstack/react-query';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import { type SubmitEvent, useState } from 'react';
import { ServerPicker } from '../servers/ServerPicker';
import { useSelectedServerApi, useServers } from '../servers/useServers';
import { OfflineAlert} from '../components/OfflineAlert';
import { DataTable, TableCell, TableRow } from '../components/DataTable';
import { FormCard } from '../components/FormCard';
import { Layout } from '../components/Layout';
import { useTags } from './useTags';

export function TagsPage() {
	const { readOnly } = useServers();
	const api = useSelectedServerApi();
	const { tags, isError } = useTags();
  const [name, setName] = useState('');

  const save = useMutation({ mutationFn: () => api.tags.save(name) });
	const remove = useMutation({ mutationFn: (name: string) => api.tags.remove(name) });

	const submit = (event: SubmitEvent) => {
    event.preventDefault();
    save.mutate(undefined, { onSuccess: () => setName('') });
  };

  return (
    <Layout title="Tags" actions={<ServerPicker/>}>
      {readOnly && <Alert severity="info" sx={{mb: 2}}>Slaves are read-only. Changes come from the master.</Alert>}
      {!readOnly && <FormCard onSubmit={submit}>
        <TextField size="small" label="Name" placeholder="on-sale" value={name} onChange={(event) => setName(event.target.value)} required sx={{ flex: '1 1 200px' }}/>
        <Button type="submit" variant="contained">Add tag</Button>
      </FormCard>}
      <OfflineAlert offline={isError}/>
      <DataTable columns={['Name', '']}>
        {tags.map((tag) => (
          <TableRow key={tag.name}>
            <TableCell>{tag.name}</TableCell>
            <TableCell align="right">
              {!readOnly &&
                <Button size="small" color="error" onClick={() => remove.mutate(tag.name)}>Delete</Button>}
            </TableCell>
          </TableRow>
        ))}
      </DataTable>
    </Layout>
  );
}
