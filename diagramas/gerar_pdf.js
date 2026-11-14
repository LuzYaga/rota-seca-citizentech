const puppeteer = require('puppeteer');
const path = require('path');

(async () => {
  console.log('Iniciando navegador para exportar PDF dos diagramas...');
  const browser = await puppeteer.launch({
    headless: 'new',
    executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
  });
  const page = await browser.newPage();
  
  const filePath = 'file:///' + path.resolve(__dirname, 'print.html').replace(/\\/g, '/');
  console.log('Carregando página:', filePath);
  
  await page.goto(filePath, { waitUntil: 'networkidle0' });
  
  // Aguarda renderização completa do SVG do Mermaid
  await page.waitForSelector('svg', { timeout: 15000 });
  await new Promise(r => setTimeout(r, 2000));
  
  const pdfPath = path.resolve(__dirname, 'diagramas.pdf');
  await page.pdf({
    path: pdfPath,
    format: 'A4',
    printBackground: true,
    margin: { top: '15mm', right: '12mm', bottom: '15mm', left: '12mm' }
  });
  
  console.log('PDF gerado com sucesso em:', pdfPath);
  await browser.close();
})();
