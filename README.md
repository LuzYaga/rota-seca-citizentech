# 🌊 Rota Seca — CitizenTech

> **Global Solution 2026/2 — FIAP**  
> **2º Ano de Engenharia de Software**  
> **Tema:** CitizenTech: Tecnologia com o cidadão no centro da inovação  
> **Categoria:** Clima e emergências / Cidades (Região Sul)  
> **Link do Vídeo Pitch (até 3 min):** [https://youtu.be/SEU_LINK_DO_PITCH_AQUI](https://youtu.be/SEU_LINK_DO_PITCH_AQUI) *(Atualize com o link público definitivo)*

---

## 👥 Integrantes do Grupo (Turma 2ESOA)
1. **Integrante 1**: Nome Completo — RM XXXXX
2. **Integrante 2**: Nome Completo — RM XXXXX
3. **Integrante 3**: Nome Completo — RM XXXXX
4. **Integrante 4**: Nome Completo — RM XXXXX

---

## 🎯 1. Problema e Solução ("CitizenTech")

### O Problema Real
Durante temporais e tempestades severas na Zona Sul da capital (e.g. Santo Amaro, Socorro, Campo Limpo), bueiros e córregos transbordam em minutos. Cidadãos comuns ficam ilhados sem saber quais vias estão transitáveis; entregadores de aplicativo arriscam veículos e vidas em áreas de fundos de vale; e **pessoas com deficiência ou mobilidade reduzida** enfrentam barreiras intransponíveis em calçadas submersas, sem rotas alternativas ou acesso rápido a abrigos municipais seguros.

### A Solução "Rota Seca"
O **Rota Seca** é uma CitizenTech colaborativa e analítica que:
1. Permite o registro em tempo real de pontos de alagamento com três níveis de lâmina d'água (`LEVE`, `MEDIO`, `GRAVE`).
2. Recalcula matematicamente o índice de vulnerabilidade e risco do bairro através de regras na camada `service` (`BAIXO`, `MEDIO`, `ALTO`).
3. Mapeia e roteia os **abrigos seguros mais próximos** (Ginásios, CEUs, Escolas), ordenados por menor distância em quilômetros via relacionamento relacional $N:N$.
4. Detecta e consolida automaticamente **Áreas Críticas Recorrentes** (pontos de alto potencial de alagamento) para priorização pelo poder público e alertas preventivos.
5. Personaliza alertas polimórficos de acordo com a vulnerabilidade do cidadão (`CidadaoMorador`, `Entregador`, `CidadaoMobilidadeReduzida`).

### Conexão com os ODS da ONU
- **ODS 11 (Cidades e Comunidades Sustentáveis)**: Redução de perdas e aumento da resiliência comunitária a desastres hídricos.
- **ODS 13 (Ação Contra a Mudança Global do Clima)**: Mecanismo de adaptação e mitigação de eventos climáticos extremos.
- **ODS 10 (Redução das Desigualdades)**: Inclusão prioritária de acessibilidade para cidadãos com mobilidade reduzida.
- **ODS 9 (Indústria, Inovação e Infraestrutura)**: Informação cidadã descentralizada alimentando a gestão de mobilidade urbana.

---

## 🏗️ 2. Arquitetura do Projeto (Fase 7 Compliance)

O projeto respeita **rigorosamente as regras de conteúdo até a fase 7**:
- **NÃO utiliza frameworks de backend** (sem Spring Boot, sem Servlets/JSP, sem JPA/Hibernate).
- **Backend em Java Puro**: Arquitetura em camadas isoladas em seus respectivos pacotes:
  - `br.com.rotaseca.model`: POO completa com herança (`Usuario` -> `CidadaoMorador`, `CidadaoMobilidadeReduzida`, `Entregador`), encapsulamento, enumerações e classes de domínio.
  - `br.com.rotaseca.dao`: Acesso aos dados com JDBC nativo (`PreparedStatement`, `Statement`, `ResultSet`), transações e CRUD completo.
  - `br.com.rotaseca.service`: Regras de negócio, cálculo de risco ponderado, detecção de áreas críticas e lançamento de exceções personalizadas (`NegocioException`).
  - `br.com.rotaseca.ui`: Interface Console interativa (`MenuConsole`) com Scanner para validação direta e independente dos 7 pontos do Java.
  - `br.com.rotaseca.server`: Servidor HTTP REST nativo do próprio JDK (`com.sun.net.httpserver.HttpServer`), sem bibliotecas externas, provendo CORS e comunicação JSON.
- **Banco de Dados Relacional**: H2 Database em modo arquivo/servidor ANSI SQL (compatível com Oracle/Postgres), com integridade referencial (PK, FK, CASCADE, NOT NULL, CHECK, UNIQUE) e relacionamento $N:N$ com tabela associativa `ABRIGO_BAIRRO`.
- **Frontend Web**: Desenvolvido em React, totalmente responsivo, semântico, com validação JS em tempo de tela e build com `base: './'`, abrindo diretamente no navegador (`index.html`) por duplo clique ou celular.

---

## 🚀 3. Instruções de Execução

### Pré-requisitos
- **Java SE Development Kit (JDK 17+)** instalado e configurado no PATH.
- Driver JDBC H2 (`lib/h2-2.2.224.jar`) já incluso no repositório.

---

### Opção A: Executar a Aplicação Web Completa (Backend Java + Frontend)

#### 1. Iniciar o Servidor Backend Java:
No terminal, na raiz do projeto:
```bash
# Compilar todas as classes do backend
javac -encoding UTF-8 -cp "lib/h2-2.2.224.jar" -d bin src/br/com/rotaseca/*/*.java

# Iniciar o servidor HTTP REST (Porta 8080)
java -cp "bin;lib/h2-2.2.224.jar" br.com.rotaseca.server.ServidorHttpApi
```
*(No Linux ou Mac, use dois pontos `:` no classpath: `-cp "bin:lib/h2-2.2.224.jar"`)*.  
O servidor criará automaticamente as tabelas e dados iniciais se necessário e ficará escutando em `http://localhost:8080`.

#### 2. Abrir a Interface Web:
- Basta abrir o arquivo `index.html` na raiz do projeto diretamente no seu navegador de preferência (Google Chrome, Edge, Firefox, Safari ou navegador mobile).
- O site se conectará à API do backend, exibindo os dados do banco H2 em tempo real!

---

### Opção B: Demonstrar o CRUD pelo Console Java (7 Pontos de Java + JDBC)
Se desejar rodar a demonstração pelo terminal interativo sem navegador:
```bash
java -cp "bin;lib/h2-2.2.224.jar" br.com.rotaseca.ui.MenuConsole
```
O console permite:
- Inserir e listar bairros e ruas;
- Listar usuários com mensagens polimórficas de alerta;
- Enviar novo reporte de alagamento com recálculo automático de risco;
- Excluir reportes no banco;
- Consultar abrigos ordenados por distância ($N:N$).

---

### Opção C: Executar Scripts SQL Manualmente
Caso deseje rodar ou inspecionar o script SQL isoladamente:
```bash
java -cp "lib/h2-2.2.224.jar" org.h2.tools.RunScript -url "jdbc:h2:./database/rotaseca" -user "sa" -script "database/schema.sql" -showResults
```

---

## 📊 4. Diagramas Técnicos
Os diagramas de Classes (UML) e Entidade-Relacionamento (ER) foram elaborados em conformidade com o código e estão disponíveis para visualização ou impressão:
- Abra o arquivo `diagramas/diagramas.html` em qualquer navegador para visualizar a renderização Mermaid e salvar/imprimir em PDF.

---

## 🤖 5. Declaração do Uso de Inteligência Artificial Generativa
*(Em estrito cumprimento às diretrizes da FIAP para a Global Solution 2026/2)*

- **Ferramentas utilizadas:** Google Gemini (Antigravity Assistant).
- **Finalidade do uso:**
  - Apoio na estruturação lógica inicial e refinamento de modelagem conceitual (UML e ER).
  - Revisão sintática de scripts SQL e compatibilidade de constraints ANSI.
  - Auxílio na criação de roteiro e cronometragem estruturada do pitch de 3 minutos.
- **Auditoria e Revisão Humana:**
  Todo o código-fonte gerado foi inspecionado, compilado, testado e validado pelos integrantes do grupo, garantindo que nenhum framework proibido fora da fase 7 fosse utilizado e assegurando o pleno domínio dos conceitos de POO, JDBC e integridade relacional.

---

## ⏱️ 6. Roteiro Sugerido para o Vídeo Pitch (3 Minutos)

- **0:00 - 0:30 (O Problema e o Cidadão):** Contextualize os temporais na Zona Sul de SP. Destaque o drama de quem tem mobilidade reduzida e entregadores de aplicativo.
- **0:30 - 0:50 (A Solução CitizenTech):** Apresente o **Rota Seca** como plataforma de colaboração cidadã e governança climática.
- **0:50 - 2:10 (Demonstração Prática):**
  - Mostre a tela de Monitoramento com os bairros e riscos.
  - Faça o envio de um reporte de alagamento `GRAVE`.
  - Mostre o índice de risco do bairro mudando de `MEDIO` para `ALTO` e o surgimento da área crítica.
  - Exiba a tela de abrigos ordenados por proximidade ($N:N$).
- **2:10 - 2:40 (Impacto e ODS):** Mencione os ODS 11, 13 e 10 e o impacto inclusivo na redução de perdas humanas e materiais.
- **2:40 - 3:00 (Fechamento):** Destaque a robustez da arquitetura em camadas Java puro + persistência relacional sem frameworks, alinhada com as melhores práticas de Engenharia de Software.
