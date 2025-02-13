'use client';
import * as React from 'react';
import Copyright from '@/components/Copyright';
import DataTable from '@/components/DataTable';

export default function Orders() {
  const [data, setData] = React.useState([]);

  interface Column {
    id: 'code' | 'orderNumber' | 'orderItems';
    label: string;
    minWidth?: number;
    align?: 'right' | 'center';
    format?: any;
  }

  const columns: readonly Column[] = [
    { id: 'code', label: 'Código', minWidth: 10 },
    { id: 'orderNumber', label: 'Número pedido', minWidth: 50 },
    {
      id: 'orderItems',
      label: 'Productos',
      minWidth: 100,
      format: (value: string) => (typeof value === 'string' ? value.substring(0, 100) + '...' : ''),
    },
  ];

  React.useEffect(() => {
    fetch('http://localhost:8082/api/order')
      .then(response => response.json())
      .then(json => {
        setData(json);
      })
      .catch(error => console.log(error));
  }, []);

  return (
    <>
      <h1 style={{ textAlign: 'center', marginTop: '100px' }}>Pedidos</h1>
      <DataTable columns={columns} data={data} onClickUrl={{ url: '/product', param: 'code' }} />
      <Copyright />
    </>
  );
}
