const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

(async () => {
  console.log('Iniciando geração dos 2 PDFs individuais...');
  const browser = await puppeteer.launch({
    headless: 'new',
    executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
  });
  
  const classesSvg = fs.readFileSync(path.resolve(__dirname, 'diagrama_classes.svg'), 'utf8');
  const erSvg = fs.readFileSync(path.resolve(__dirname, 'diagrama_er.svg'), 'utf8');
  
  const baseCss = `
    @page { size: A4 landscape; margin: 10mm; }
    body {
      margin: 0; padding: 0;
      font-family: Arial, sans-serif;
      background: white; color: #0f172a;
      -webkit-print-color-adjust: exact;
    }
    .header {
      border-bottom: 2px solid #0284c7;
      padding-bottom: 6px; margin-bottom: 12px;
    }
    h1 { margin: 0 0 4px 0; font-size: 16pt; color: #0369a1; }
    p.sub { margin: 0; font-size: 9.5pt; color: #64748b; }
    .diagram-container {
      display: flex; align-items: center; justify-content: center;
      border: 1px solid #cbd5e1; border-radius: 8px;
      background: #ffffff; padding: 12px;
    }
    svg { width: 100% !important; height: auto !important; max-height: 160mm !important; }
  `;

  // 1. PDF - Diagrama de Classes UML
  const htmlUml = `<!DOCTYPE html><html><head><meta charset="utf-8"><style>${baseCss}</style></head>
  <body>
    <div class="header">
      <h1>Rota Seca — Diagrama de Classes UML (Java SE Puro)</h1>
      <p class="sub">Global Solution 2026/2 · FIAP — 2º Ano Engenharia de Software (Turma 2ESOA) · Camadas Model, DAO, Service e Relacionamentos</p>
    </div>
    <div class="diagram-container">${classesSvg}</div>
  </body></html>`;

  const page1 = await browser.newPage();
  await page1.setContent(htmlUml, { waitUntil: 'load' });
  const pdfUmlPath = path.resolve(__dirname, 'diagrama_classes.pdf');
  await page1.pdf({
    path: pdfUmlPath,
    format: 'A4',
    landscape: true,
    printBackground: true,
    margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' }
  });
  console.log('PDF UML salvo em:', pdfUmlPath);

  // 2. PDF - Diagrama Entidade-Relacionamento (ER)
  const htmlEr = `<!DOCTYPE html><html><head><meta charset="utf-8"><style>${baseCss}</style></head>
  <body>
    <div class="header">
      <h1>Rota Seca — Diagrama Entidade-Relacionamento (Modelo Relacional de Dados)</h1>
      <p class="sub">Banco Relacional H2 / ANSI SQL · Tabelas com PK, FK (CASCADE), NOT NULL, CHECK, UNIQUE e Tabela Associativa N:N (ABRIGO_BAIRRO)</p>
    </div>
    <div class="diagram-container">${erSvg}</div>
  </body></html>`;

  const page2 = await browser.newPage();
  await page2.setContent(htmlEr, { waitUntil: 'load' });
  const pdfErPath = path.resolve(__dirname, 'diagrama_er.pdf');
  await page2.pdf({
    path: pdfErPath,
    format: 'A4',
    landscape: true,
    printBackground: true,
    margin: { top: '10mm', right: '10mm', bottom: '10mm', left: '10mm' }
  });
  console.log('PDF ER salvo em:', pdfErPath);

  await browser.close();
  console.log('Concluído!');
})();
