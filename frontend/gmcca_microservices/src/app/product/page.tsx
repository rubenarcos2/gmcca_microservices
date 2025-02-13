'use client';
import * as React from 'react';
import './pageCard.css';
import './loader.css';
import Copyright from '@/components/Copyright';
import { useSearchParams } from 'next/navigation';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import { useRouter } from 'next/navigation';

export default function Product() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const code = searchParams.get('code') || '';

  const [data, setData] = React.useState<any>();
  const [file, setFile] = React.useState<File | null | any>(null);
  const [fileUploaded, setFileUploaded] = React.useState();

  React.useEffect(() => {
    fetch('http://localhost:8083/api/product/code?code=' + code)
      .then(response => response.json())
      .then(json => setData(json))
      .catch(error => console.log(error));
  }, []);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setFile(new File([e.target.files[0]], data?.code + '.pdf', { type: e.target.files[0].type }));
    }
  };

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append('file', file);
    const requestOptions = {
      method: 'POST',
      body: formData,
    };

    fetch('http://localhost:8084/docs/upload', requestOptions)
      .then(response => response.json())
      .then(json => {
        setFileUploaded(json);
        (document.getElementById('fileBtnUpload') as HTMLButtonElement).disabled = true;
      });
    // .catch(error => {
    //   console.log(error);
    //   alert('Ha habido un error en la subida o el procesamiento del fichero');
    //   (document.getElementById('fileBtnUpload') as HTMLButtonElement).disabled = false;
    // })

    (document.getElementById('fileBtnUpload') as HTMLButtonElement).disabled = true;
    router.push('/');
  };

  return (
    <>
      <Paper style={{ margin: '100px 2em 2em 2em', padding: '2em' }} elevation={3}>
        {data ? (
          <div className="pageCard">
            <Card className="cardProduct">
              <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                  {data.name}
                </Typography>
                <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                  Código: {data.code}
                </Typography>
                <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                  Precio: {data.price.toLocaleString('es-ES', { minimumFractionDigits: 2 }) + ' €'}
                </Typography>
                <Typography variant="body2" sx={{ color: 'text.secondary', textAlign: 'justify' }}>
                  Descripción:
                  <br />
                  {data.description}
                </Typography>
              </CardContent>
            </Card>
            <Card className="cardPDF">
              {data.status == 'PROCESSED' ? (
                <iframe
                  src={'http://localhost:8084/docs/download?fileName=' + data.code + '.pdf'}
                  width="100%"
                  height="600px"
                />
              ) : data.status == 'PROCESSING' ? (
                <div>
                  <h3>El manual del producto se está procesando</h3>
                  <p>
                    Le avisaremos mediante una notifiación cuando esté listo y podrá verse en esta
                    sección de la página web.
                  </p>
                  <div className="lds-roller">
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                    <div></div>
                  </div>
                </div>
              ) : (
                <>
                  <Typography gutterBottom variant="h5" component="div">
                    Manual del producto no disponible
                  </Typography>
                  <p>¿Dispone del manual del producto?</p>
                  <div>
                    <input id="file" type="file" accept=".pdf" onChange={handleFileChange} />
                  </div>
                  {file && (
                    <section>
                      <p>Nombre: {file.name}</p>
                      <p>Tipo: {file.type}</p>
                      <p>Tamaño: {file.size} bytes</p>
                    </section>
                  )}

                  {file && (
                    <button id="fileBtnUpload" onClick={handleUpload} className="submit">
                      Subir fichero PDF
                    </button>
                  )}
                </>
              )}
            </Card>
          </div>
        ) : (
          <h3 style={{ textAlign: 'center' }}>No existe el producto</h3>
        )}
      </Paper>
      <Copyright />
    </>
  );
}
