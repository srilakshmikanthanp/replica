import {useMutation} from '@tanstack/react-query';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import {type SubmitEvent, useState} from 'react';
import {ServerPicker} from '../servers/ServerPicker';
import {useSelectedServerApi, useServers} from '../servers/useServers';
import {OfflineAlert} from '../components/OfflineAlert';
import {FormCard} from '../components/FormCard';
import {Layout} from '../components/Layout';
import {MasterTable} from '../components/MasterTable';
import {SlaveTable} from '../components/SlaveTable';
import type {Product} from './Product';
import {useProducts} from './useProducts';

const productKey = (product: Product) => product.sku;

const productCells = (product: Product) => [
  product.sku,
  product.categoryCode,
  product.name,
  product.price,
  <Stack direction="row" spacing={0.5} sx={{flexWrap: "wrap"}}>
    {product.tags.map((tag) => <Chip key={tag} size="small" label={tag}/>)}
  </Stack>,
];

const columns = ['SKU', 'Category', 'Name', 'Price', 'Tags'];

const compareProducts = (product: Product, copy: Product) =>
  product.categoryCode === copy.categoryCode &&
  product.name === copy.name &&
  product.price === copy.price &&
  product.tags.length === copy.tags.length &&
  product.tags.every((tag) => copy.tags.includes(tag));

export function ProductsPage() {
  const {readOnly} = useServers();
  const api = useSelectedServerApi();
  const {products, categories, tags, isError} = useProducts();
  const [sku, setSku] = useState('');
  const [categoryCode, setCategoryCode] = useState('');
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');

  const save = useMutation({mutationFn: () => api.products.save(sku, {categoryCode, name, price: Number(price)})});
  const remove = useMutation({mutationFn: (sku: string) => api.products.remove(sku)});
  const addTag = useMutation({
    mutationFn: (tag: {
      sku: string;
      name: string
    }) => api.products.addTag(tag.sku, tag.name)
  });
  const removeTag = useMutation({
    mutationFn: (tag: {
      sku: string;
      name: string
    }) => api.products.removeTag(tag.sku, tag.name)
  });

  const submit = (event: SubmitEvent) => {
    event.preventDefault();
    save.mutate(undefined, {
      onSuccess: () => {
        setSku('');
        setCategoryCode('');
        setName('');
        setPrice('');
      }
    });
  };

  const freeTags = (product: Product) => tags.filter((tag) => !product.tags.includes(tag.name));

  return (
    <Layout title="Products" actions={<ServerPicker/>}>
      {readOnly && <Alert severity="info" sx={{mb: 2}}>Slaves are read-only. Changes come from the master.</Alert>}
      {!readOnly && <FormCard onSubmit={submit}>
        <TextField size="small" label="SKU" value={sku} onChange={(event) => setSku(event.target.value)} required/>
        <TextField size="small" select label="Category" value={categoryCode}
                   onChange={(event) => setCategoryCode(event.target.value)} required sx={{minWidth: 180}}>
          {categories.map((category) => (
            <MenuItem key={category.code} value={category.code}>{category.code} - {category.name}</MenuItem>
          ))}
        </TextField>
        <TextField size="small" label="Name" value={name} onChange={(event) => setName(event.target.value)} required
                   sx={{flex: '1 1 200px'}}/>
        <TextField size="small" type="number" label="Price" value={price}
                   onChange={(event) => setPrice(event.target.value)} required/>
        <Button type="submit" variant="contained">Save product</Button>
      </FormCard>}
      <OfflineAlert offline={isError}/>
      {readOnly ? (
        <SlaveTable columns={columns} rows={products} rowKey={productKey} cells={productCells}/>
      ) : (
        <MasterTable
          name="products"
          copies={(slaveApi) => slaveApi.products.list()}
          columns={columns}
          rows={products}
          rowKey={productKey}
          compare={compareProducts}
          cells={(product, extra) => extra ? productCells(product) : [
            product.sku,
            product.categoryCode,
            product.name,
            product.price,
            <Stack direction="row" spacing={0.5} sx={{alignItems: "center", flexWrap: "wrap"}}>
              {product.tags.map((tag) => (
                <Chip key={tag} size="small" label={tag} onDelete={() => removeTag.mutate({sku: product.sku, name: tag})}/>
              ))}
              {freeTags(product).length > 0 && (
                <TextField
                  select
                  size="small"
                  variant="standard"
                  value=""
                  onChange={(event) => addTag.mutate({sku: product.sku, name: event.target.value})}
                  slotProps={{select: {displayEmpty: true, renderValue: () => 'add tag'}}}
                  sx={{minWidth: 80}}
                >
                  {freeTags(product).map((tag) => <MenuItem key={tag.name} value={tag.name}>{tag.name}</MenuItem>)}
                </TextField>
              )}
            </Stack>,
          ]}
          actions={(product) => (
            <Stack direction="row" spacing={1} sx={{justifyContent: "flex-end"}}>
              <Button size="small" onClick={() => {
                setSku(product.sku);
                setCategoryCode(product.categoryCode);
                setName(product.name);
                setPrice(String(product.price));
              }}>Edit</Button>
              <Button size="small" color="error" onClick={() => remove.mutate(product.sku)}>Delete</Button>
            </Stack>
          )}
        />
      )}
    </Layout>
  );
}
