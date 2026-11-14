# 🌊 Rota Seca — CitizenTech

> **FIAP — Global Solution 2026/2**  
> **Curso:** Engenharia de Software — Turma 2ESOA  
> **Tema:** CitizenTech: Tecnologia com o cidadão no centro da inovação  
> **Categoria:** Clima e emergências / Cidades  
> **Aplicação Web no Ar (GitHub Pages):** [https://luzyaga.github.io/rota-seca-citizentech/](https://luzyaga.github.io/rota-seca-citizentech/)  
> **Vídeo Pitch da Solução:** [https://youtu.be/LINK](https://youtu.be/LINK) 
---

## 👥 Integrantes do Grupo
- **Aluno 1:** Nome Completo — RM XXXXX
- **Aluno 2:** Nome Completo — RM XXXXX
- **Aluno 3:** Nome Completo — RM XXXXX
- **Aluno 4:** Nome Completo — RM XXXXX

---

## 🎯 1. Visão Geral da Solução

### Problema Atendido
Durante tempestades severas na Zona Sul da cidade de São Paulo (regiões como Santo Amaro, Socorro e Campo Limpo), bueiros e córregos transbordam com rapidez extrema. Moradores ficam desorientados sem saber quais vias estão transitáveis; entregadores de aplicativo enfrentam riscos severos de perda de veículos em vias de fundo de vale; e **cidadãos com deficiência ou mobilidade reduzida** sofrem com barreiras físicas intransponíveis em calçadas inundadas, sem rotas de fuga acessíveis ou conhecimento imediato sobre abrigos municipais seguros.

### A CitizenTech "Rota Seca"
O **Rota Seca** é uma plataforma comunitária de governança climática e apoio emergencial:
1. **Reporte Colaborativo Descentralizado:** O cidadão informa alagamentos em tempo real especificando a via e o nível da lâmina d'água (`LEVE`, `MEDIO` ou `GRAVE`).
2. **Cálculo Dinâmico de Risco por Bairro:** A camada de regras de negócio pondera a gravidade dos chamados acumulados e reclassifica o índice de risco do bairro (`BAIXO`, `MEDIO` ou `ALTO`).
3. **Mapeamento e Roteamento de Abrigos:** O sistema localiza pontos de acolhimento seguros (ginásios, CEUs, postos da Defesa Civil) ordenados por proximidade operacional em quilômetros.
4. **Detecção Automática de Áreas Críticas Recorrentes:** Identificação de locais com histórico severo de inundação para subsidiar ações preventivas do poder público e alertas à comunidade.
5. **Comunicação Polimórfica Adaptada ao Cidadão:** Mensagens de alerta personalizadas conforme o perfil cadastrado (`CidadaoMorador`, `Entregador` ou `CidadaoMobilidadeReduzida`).

### Conexão com os Objetivos de Desenvolvimento Sustentável (ODS) da ONU
- **ODS 11 (Cidades e Comunidades Sustentáveis):** Fortalecimento da resiliência comunitária diante de desastres hídricos urbanos.
- **ODS 13 (Ação Contra a Mudança Global do Clima):** Mecanismo direto de adaptação e resposta a eventos climáticos extremos.
- **ODS 10 (Redução das Desigualdades):** Atenção prioritária e inclusiva para pessoas com mobilidade reduzida.
- **ODS 09 (Indústria, Inovação e Infraestrutura):** Uso inteligente de dados gerados pelos próprios cidadãos para mobilidade e segurança.

---

## 🏗️ 2. Arquitetura e Engenharia de Software

O projeto foi construído respeitando estritamente o conteúdo pedagógico ministrado até a **Fase 7**, sem a utilização de frameworks proibidos de backend (sem Spring Boot, Servlets/JSP ou JPA/Hibernate).

### Organização em Camadas (Java Puro + JDBC)
- `br.com.rotaseca.model`: Entidades de domínio com herança polimórfica (`Usuario` $\rightarrow$ `CidadaoMorador`, `CidadaoMobilidadeReduzida`, `Entregador`), encapsulamento rígido e enumerações de negócio (`NivelRisco`, `NivelAlagamento`).
- `br.com.rotaseca.dao`: Persistência relacional via JDBC puro (`ConexaoBanco`, `PreparedStatement`, `ResultSet`), com interface genérica `Repositorio<T>` e tratamento transacional.
- `br.com.rotaseca.service`: Regras de negócio, cálculo de risco ponderado, validação de regras de domínio, avaliação de gatilhos de áreas críticas e lançamento de exceções personalizadas (`NegocioException`).
- `br.com.rotaseca.ui`: Interface Console interativa (`MenuConsole`) para auditoria direta e isolada de todos os métodos de CRUD no terminal.
- `br.com.rotaseca.server`: Servidor HTTP REST embutido do próprio Java SE (`com.sun.net.httpserver.HttpServer`), sem dependências externas, provendo endpoints JSON e headers CORS.

### Banco de Dados Relacional
- **SGBD Utilizado:** H2 Database em modo arquivo/servidor com sintaxe ANSI SQL compatível com Oracle e PostgreSQL.
- **Integridade Referencial:** Chaves Primárias (PK), Chaves Estrangeiras (FK) com `ON DELETE CASCADE`, constraints `NOT NULL`, `UNIQUE` e `CHECK`.
- **Relacionamento N:N:** Tabela associativa `ABRIGO_BAIRRO` contendo atributo extra (`distancia_km`).
- **Relatórios SQL Analíticos:** Consultas estruturadas com `JOIN` e funções de agregação (`COUNT`, `SUM`, `GROUP BY`, `ORDER BY`) em `database/schema.sql`.

### Frontend Web
- Interface moderna, acessível e responsiva construída em React.
- Validação em tempo de execução via JavaScript e mensagens contextuais de feedback na tela.
- Build gerado com caminhos relativos (`base: './'`), permitindo abrir o `index.html` diretamente em qualquer navegador desktop ou mobile.

---

## 🚀 3. Guia de Execução para os Avaliadores

### Pré-requisitos
- **Java SE Development Kit (JDK 17 ou superior)** instalado.
- Driver JDBC H2 (`lib/h2-2.2.224.jar`) já integrado no projeto.

---

### Procedimento A: Execução da Solução Completa (Backend + Interface Web)

1. **Compilar e Iniciar o Servidor Backend:**
   No terminal, na raiz do projeto:
   ```bash
   # Compilar as classes Java
   javac -encoding UTF-8 -cp "lib/h2-2.2.224.jar" -d bin src/br/com/rotaseca/*/*.java

   # Iniciar o servidor HTTP REST (Porta 8080)
   java -cp "bin;lib/h2-2.2.224.jar" br.com.rotaseca.server.ServidorHttpApi
   ```
   *(No Linux/macOS, substitua `;` por `:` no parâmetro `-cp`)*.  
   O servidor inicializará o banco relacional automaticamente e ficará escutando em `http://localhost:8080`.

2. **Acessar a Interface Web:**
   - Dê um duplo clique no arquivo `index.html` na raiz do projeto (ou abra em qualquer navegador).
   - A interface detectará automaticamente o backend online, consumindo e persistindo dados reais via JDBC.

---

### Procedimento B: Demonstração do CRUD via Console (Terminal Java Puro)

Para validar a persistência JDBC e os tratamentos de erro diretamente no terminal Java, execute:
```bash
java -cp "bin;lib/h2-2.2.224.jar" br.com.rotaseca.ui.MenuConsole
```
O menu interativo permite:
- Cadastrar e listar bairros e logradouros;
- Listar usuários e consultar os alertas polimórficos de cada perfil;
- Registrar alagamento em tempo real com disparo de recálculo de risco;
- Excluir reportes no banco de dados;
- Consultar abrigos ordenados por menor distância em quilômetros.

---

### Procedimento C: Execução Isolada do Script SQL

Para inspecionar ou executar o script SQL de criação de tabelas, constraints e cargas iniciais:
```bash
java -cp "lib/h2-2.2.224.jar" org.h2.tools.RunScript -url "jdbc:h2:./database/rotaseca" -user "sa" -script "database/schema.sql" -showResults
```
O script completo com as queries de relatório analítico está localizado em `database/schema.sql`.

---

## 📊 4. Diagramas Técnicos (Entregues em PDF Separados)

Os diagramas do projeto refletem com precisão a estrutura das classes e o modelo relacional de dados, organizados em dois arquivos PDF independentes na pasta `diagramas/`:
- **`diagramas/diagrama_classes.pdf`**: Diagrama de Classes UML com pacotes `model`, `dao`, `service`, atributos, métodos, herança e interfaces.
- **`diagramas/diagrama_er.pdf`**: Diagrama Entidade-Relacionamento (ER) com as 6 tabelas, PKs, FKs, cardinalidades e tabela associativa N:N.

*(Arquivos auxiliares vetoriais `.svg` e imagem `.png` de alta resolução também estão disponíveis na mesma pasta).*

---

## 📁 5. Estrutura de Arquivos da Entrega

```text
GlobalSolutions/
├── assets/                     # Assets estáticos empacotados para a web
├── bin/                        # Binários compilados do backend Java
├── database/
│   ├── rotaseca.mv.db          # Arquivo do banco de dados relacional H2
│   └── schema.sql              # Script SQL com DDL, DML e relatórios
├── diagramas/
│   ├── diagrama_classes.pdf    # Diagrama de Classes UML em PDF
│   ├── diagrama_er.pdf         # Diagrama Entidade-Relacionamento em PDF
│   ├── diagrama_classes.svg    # Vetorial de Classes
│   ├── diagrama_er.svg         # Vetorial de Dados
│   └── diagrama_classes.png    # Imagem de alta resolução
├── frontend/                   # Código-fonte React (Vite)
├── lib/
│   └── h2-2.2.224.jar          # Driver JDBC do banco de dados
├── src/                        # Código-fonte Java SE puro em camadas
│   └── br/com/rotaseca/
│       ├── dao/                # Camada de acesso a dados (JDBC puro)
│       ├── model/              # Classes de domínio com POO e herança
│       ├── server/             # Servidor HTTP REST (com.sun.net.httpserver)
│       ├── service/            # Regras de negócio e cálculo de riscos
│       └── ui/                 # Interface Console CLI (Scanner)
├── .gitignore                  # Regras de exclusão de arquivos transitórios
├── entrega.txt                 # Metadados do grupo e link do vídeo pitch
├── index.html                  # Interface web executável com duplo clique
└── README.md                   # Documentação completa para a banca
```

---

## 🤖 6. Declaração do Uso de IA Generativa


- **Ferramenta Utilizada:** Google Gemini (Antigravity Assistant).
- **Finalidade:**
  - Apoio na pesquisa conceitual de diretrizes da ONU (ODS 11, 13 e 10) aplicadas à mobilidade urbana;
  - Revisão e validação sintática das constraints ANSI SQL do banco de dados relacional;
  - Apoio na estruturação inicial dos diagramas técnicos e conversão vetorial.
- **Auditoria Humana:**  
  Toda a arquitetura de software, hierarquia de classes, regras de persistência JDBC, tratamento de exceções e testes funcionais foram implementados, revisados e validados integralmente pelos estudantes integrantes do grupo.
