'use client';
import * as React from 'react';
import Copyright from '@/components/Copyright';
import DataTable from '@/components/DataTable';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBook, faRotate, faFileCircleExclamation } from '@fortawesome/free-solid-svg-icons';

export default function Main() {
  const faBookIcon = <FontAwesomeIcon icon={faBook} title="Manual disponible" />;
  const faRotateIcon = <FontAwesomeIcon icon={faRotate} title="Manual procesándose" />;
  const faFileCircleExclamationIcon = (
    <FontAwesomeIcon icon={faFileCircleExclamation} title="Manual no disponible" />
  );
  const [data, setData] = React.useState([]);

  interface Column {
    id: 'code' | 'name' | 'description' | 'price' | 'status';
    label: string;
    minWidth?: number;
    align?: 'right' | 'center';
    format?: any;
  }

  const columns: readonly Column[] = [
    { id: 'code', label: 'Código', minWidth: 10 },
    { id: 'name', label: 'Producto', minWidth: 50 },
    {
      id: 'description',
      label: 'Descripción',
      minWidth: 100,
      format: (value: string) => (typeof value === 'string' ? value.substring(0, 100) + '...' : ''),
    },
    {
      id: 'price',
      label: 'Precio',
      minWidth: 50,
      align: 'right',
      format: (value: number) => value.toLocaleString('es-ES', { minimumFractionDigits: 2 }) + ' €',
    },
    {
      id: 'status',
      label: 'Manual',
      minWidth: 10,
      align: 'center',
      format: (value: string) => {
        switch (value) {
          case 'DISABLE':
            return faFileCircleExclamationIcon;
          case 'PROCESSING':
            return faRotateIcon;
          case 'PROCESSED':
            return faBookIcon;
        }
      },
    },
  ];

  React.useEffect(() => {
    fetch('https://gmcca-microservices-backend.rarcos.com/products/api/product')
      .then(response => response.json())
      .then(json => {
        setData(json);
      })
      .catch(error => console.log(error));
  }, []);

  return (
    <>
      <h1 style={{ textAlign: 'center', marginTop: '100px' }}>Listado de productos</h1>
      <DataTable columns={columns} data={data} onClickUrl={{ url: '/product', param: 'code' }} />
      <Copyright />
    </>
  );
}
