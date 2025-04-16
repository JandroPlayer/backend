const express = require('express');
const cloudinary = require('cloudinary').v2;
const crypto = require('crypto');
const cors = require('cors');

const swaggerUi = require('swagger-ui-express');
const swaggerJsdoc = require('swagger-jsdoc');

const app = express();
const port = 3000;

cloudinary.config({
    cloud_name: 'dglxd4bqz',
    api_key: '648868535917264',
    api_secret: 'BCW1rCcvYuP7p6nik4ps0sMOjFQ',
});

// Swagger configuración
const swaggerOptions = {
    definition: {
        openapi: '3.0.0',
        info: {
            title: 'Cloudinary Signature API',
            version: '1.0.0',
            description: 'API para generar firma de Cloudinary',
        },
        servers: [
            {
                url: 'http://localhost:3000',
            },
        ],
    },
    apis: ['./Cloudinary.js'], // Reemplaza por el nombre del archivo actual si no es index.js
};

const swaggerSpec = swaggerJsdoc(swaggerOptions);

// Middleware Swagger
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

app.use(cors({ origin: 'http://localhost:4200' }));

/**
 * @swagger
 * /api/cloudinary-signature:
 *   get:
 *     summary: Obtener firma y configuración de Cloudinary
 *     responses:
 *       200:
 *         description: Firma generada correctamente
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 timestamp:
 *                   type: integer
 *                 signature:
 *                   type: string
 *                 apiKey:
 *                   type: string
 *                 cloudName:
 *                   type: string
 */
app.get('/api/cloudinary-signature', (req, res) => {
    const timestamp = Math.floor(Date.now() / 1000);
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

function generateSignature(paramsToSign, apiSecret) {
    const sorted = Object.keys(paramsToSign)
        .sort()
        .map(key => `${key}=${paramsToSign[key]}`)
        .join('&');

    return crypto.createHash('sha1').update(sorted + apiSecret).digest('hex');
}

app.listen(port, () => {
    console.log(`Servidor escuchando en http://localhost:${port}`);
    console.log(`Swagger disponible en http://localhost:${port}/api-docs`);
});
