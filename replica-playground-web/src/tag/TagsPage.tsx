import {useMutation} from '@tanstack/react-query';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import {type SubmitEvent, useState} from 'react';
import {ServerPicker} from '../servers/ServerPicker';
import {useSelectedServerApi, useServers} from '../servers/useServers';
import {OfflineAlert} from '../components/OfflineAlert';
import {FormCard} from '../components/FormCard';
import {Layout} from '../components/Layout';
import {MasterTable} from '../components/MasterTable';
import {SlaveTable} from '../components/SlaveTable';
import type {Tag} from './Tag';
import {useTags} from './useTags';

const tagName = (tag: Tag) => tag.name;

const tagCells = (tag: Tag) => [tag.name];

const columns = ['Name'];

export function TagsPage() {
  const {readOnly} = useServers();
  const api = useSelectedServerApi();
  const {tags, isError} = useTags();
  const [name, setName] = useState('');

  const save = useMutation({mutationFn: () => api.tags.save(name)});
  const remove = useMutation({mutationFn: (name: string) => api.tags.remove(name)});

  const submit = (event: SubmitEvent) => {
    event.preventDefault();
    save.mutate(undefined, {onSuccess: () => setName('')});
  };

  return (
    <Layout title="Tags" actions={<ServerPicker/>}>
      {readOnly && <Alert severity="info" sx={{mb: 2}}>Slaves are read-only. Changes come from the master.</Alert>}
      {!readOnly && <FormCard onSubmit={submit}>
        <TextField size="small" label="Name" placeholder="on-sale" value={name}
                   onChange={(event) => setName(event.target.value)} required sx={{flex: '1 1 200px'}}/>
        <Button type="submit" variant="contained">Add tag</Button>
      </FormCard>}
      <OfflineAlert offline={isError}/>
      {readOnly ? (
        <SlaveTable columns={columns} rows={tags} rowKey={tagName} cells={tagCells}/>
      ) : (
        <MasterTable
          name="tags"
          copies={(slaveApi) => slaveApi.tags.list()}
          columns={columns}
          rows={tags}
          rowKey={tagName}
          compare={() => true}
          cells={tagCells}
          actions={(tag) => <Button size="small" color="error" onClick={() => remove.mutate(tag.name)}>Delete</Button>}
        />
      )}
    </Layout>
  );
}
