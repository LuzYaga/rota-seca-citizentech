const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

(async () => {
  console.log('Iniciando geração de PDF vetorial sem erro...');
  const browser = await puppeteer.launch({
    headless: 'new',
    executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
  });
  const page = await browser.newPage();
  
  const classesSvg = fs.readFileSync(path.resolve(__dirname, 'diagrama_classes.svg'), 'utf8');
  const erSvg = fs.readFileSync(path.resolve(__dirname, 'diagrama_er.svg'), 'utf8');
  
  const html = `<!DOCTYPE html>
  <html>
  <head>
    <meta charset="utf-8">
    <style>
      @page {
        size: A4 landscape;
        margin: 10mm;
      }
      body {
        margin: 0;
        padding: 0;
        font-family: Arial, sans-serif;
        background: white;
        color: #0f172a;
        -webkit-print-color-adjust: exact;
      }
      .page {
        page-break-after: always;
        height: 100vh;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
      }
      .page:last-child {
        page-break-after: auto;
      }
      .header {
        border-bottom: 2px solid #0284c7;
        padding-bottom: 6px;
        margin-bottom: 12px;
      }
      h1 {
        margin: 0 0 4px 0;
        font-size: 16pt;
        color: #0369a1;
      }
      p.sub {
        margin: 0;
        font-size: 9.5pt;
        color: #64748b;
      }
      .diagram-container {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid #cbd5e1;
        border-radius: 8px;
        background: #ffffff;
        padding: 10px;
        overflow: hidden;
      }
      .diagram-container svg {
        width: 100% !important;
        height: auto !important;
        max-height: 155mm !important;
      }
    </style>
  </head>
  <body>
    <!-- PÁGINA 1: CLASSES UML -->
    <div class="page">
      <div class="header">
        <h1>Rota Seca — Diagrama de Classes UML (Java SE Puro)</h1>
        <p class="sub">Global Solution 2026/2 · FIAP — 2º Ano Engenharia de Software (Turma 2ESOA) · Camadas Model, DAO, Service e Relacionamentos</p>
      </div>
      <div class="diagram-container">
        ${classesSvg}
      </div>
    </div>

    <!-- PÁGINA 2: BANCO RELACIONAL ER -->
    <div class="page">
      <div class="header">
        <h1>Rota Seca — Diagrama Entidade-Relacionamento (Modelo Relacional de Dados)</h1>
        <p class="sub">Banco Relacional H2 / ANSI SQL · Tabelas com PK, FK (CASCADE), NOT NULL, CHECK, UNIQUE e Tabela Associativa N:N (ABRIGO_BAIRRO)</p>
      </div>
      <div class="diagram-container">
        ${erSvg}
      </div>
    </div>
  </body>
  </html>`;
  
  const tempHtml = path.resolve(__dirname, 'export_clean.html');
  fs.writeFileSync(tempHtml, html, 'utf8');
  await page.goto('file:///' + tempHtml.replace(/\\/g, '/'), { waitUntil: 'load' });
  
  const destPdf = path.resolve(__dirname, 'diagramas.pdf');
  await page.pdf({
    path: destPdf,
    format: 'A4',
    landscape: true,
    printBackground: true,
    margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' }
  });
  
  console.log('PDF gerado com sucesso em:', destPdf);
  await browser.close();
})();
