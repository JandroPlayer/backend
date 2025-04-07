const express = require('express');
const cloudinary = require('cloudinary').v2;
const crypto = require('crypto');
const cors = require('cors');

const app = express();
const port = 3000; // O el puerto que prefieras

cloudinary.config({
    cloud_name: 'dglxd4bqz',
    api_key: '648868535917264',
    api_secret: 'BCW1rCcvYuP7p6nik4ps0sMOjFQ',
});

// Función para generar la firma
function generateSignature(paramsToSign, apiSecret) {
    const sorted = Object.keys(paramsToSign)
        .sort()
        .map(key => `${key}=${paramsToSign[key]}`)
        .join('&');

    return crypto.createHash('sha1').update(sorted + apiSecret).digest('hex');
}

app.use(cors({ origin: 'http://localhost:4200' }));

// Ruta para obtener la firma
app.get('/api/cloudinary-signature', (req, res) => {
    const timestamp = Math.floor(Date.now() / 1000);

    // Agrega parámetros adicionales aquí (por ejemplo, upload_preset)
    const params = {
        timestamp: timestamp,
        upload_preset: 'paucasesnoves',
    };

    const signature = generateSignature(params, 'BCW1rCcvYuP7p6nik4ps0sMOjFQ');

    res.json({
        timestamp,
        signature,
        apiKey: '648868535917264',
        cloudName: 'dglxd4bqz',
    });
});

// Iniciar el servidor
app.listen(port, () => {
    console.log(`Servidor escuchando en http://localhost:${port}`);
});
