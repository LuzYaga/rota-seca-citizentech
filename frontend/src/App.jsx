import React, { useState, useEffect } from 'react';

// URL base da API HTTP embutida Java (quando rodando com o backend)
const API_BASE = 'http://localhost:8080/api';

export default function App() {
  // Hash-based tab navigation (Garante funcionamento direto sem dependência de router complexo)
  const [pagina, setPagina] = useState('monitoramento');

  // Estados de dados
  const [bairros, setBairros] = useState([]);
  const [ruas, setRuas] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [reportes, setReportes] = useState([]);
  const [abrigos, setAbrigos] = useState([]);
  const [areasCriticas, setAreasCriticas] = useState([]);
  
  // Estado de conexão com backend
  const [backendConectado, setBackendConectado] = useState(false);
  const [feedback, setFeedback] = useState({ tipo: '', msg: '' });

  // Formulário de novo reporte
  const [formReporte, setFormReporte] = useState({
    idRua: '',
    idUsuario: '',
    nivel: 'MEDIO',
    descricao: ''
  });

  // Filtro de abrigos por bairro
  const [bairroFiltroAbrigo, setBairroFiltroAbrigo] = useState('');

  // Carregamento inicial de dados
  useEffect(() => {
    carregarDados();
    const interval = setInterval(carregarDados, 10000); // Polling a cada 10s
    return () => clearInterval(interval);
  }, [bairroFiltroAbrigo]);

  const carregarDados = async () => {
    try {
      const resStatus = await fetch(`${API_BASE}/status`).catch(() => null);
      if (resStatus && resStatus.ok) {
        setBackendConectado(true);

        const [resBairros, resRuas, resUsers, resReportes, resAreas] = await Promise.all([
          fetch(`${API_BASE}/bairros`).then(r => r.json()),
          fetch(`${API_BASE}/ruas`).then(r => r.json()),
          fetch(`${API_BASE}/usuarios`).then(r => r.json()),
          fetch(`${API_BASE}/reportes`).then(r => r.json()),
          fetch(`${API_BASE}/areas-criticas`).then(r => r.json())
        ]);

        setBairros(resBairros || []);
        setRuas(resRuas || []);
        setUsuarios(resUsers || []);
        setReportes(resReportes || []);
        setAreasCriticas(resAreas || []);

        // Abrigos filtrados ou todos
        const urlAbrigo = bairroFiltroAbrigo 
          ? `${API_BASE}/abrigos?bairroId=${bairroFiltroAbrigo}`
          : `${API_BASE}/abrigos`;
        const resAbrigos = await fetch(urlAbrigo).then(r => r.json());
        setAbrigos(resAbrigos || []);
      } else {
        // Fallback demonstrativo caso abra o index.html antes de subir o backend
        setBackendConectado(false);
        carregarDadosDemonstrativos();
      }
    } catch (e) {
      setBackendConectado(false);
      carregarDadosDemonstrativos();
    }
  };

  const carregarDadosDemonstrativos = () => {
    if (bairros.length === 0) {
      setBairros([
        { id: 1, nome: 'Santo Amaro', zona: 'Zona Sul', nivelRisco: 'MEDIO' },
        { id: 2, nome: 'Socorro', zona: 'Zona Sul', nivelRisco: 'ALTO' },
        { id: 3, nome: 'Campo Limpo', zona: 'Zona Sul', nivelRisco: 'BAIXO' },
        { id: 4, nome: 'Jabaquara', zona: 'Zona Sul', nivelRisco: 'BAIXO' }
      ]);
      setRuas([
        { id: 1, nome: 'Avenida das Nações Unidas (Marginal)', cep: '04795-100', idBairro: 1, nomeBairro: 'Santo Amaro' },
        { id: 2, nome: 'Rua Barão do Rio Branco', cep: '04753-000', idBairro: 1, nomeBairro: 'Santo Amaro' },
        { id: 3, nome: 'Avenida Interlagos', cep: '04661-100', idBairro: 2, nomeBairro: 'Socorro' },
        { id: 4, nome: 'Rua Olivia Guedes Penteado', cep: '04766-001', idBairro: 2, nomeBairro: 'Socorro' }
      ]);
      setUsuarios([
        { id: 1, nome: 'Carlos Silva', contato: '11987654321', tipo: 'MORADOR', alerta: 'Alerta Residência: Monitore níveis de bueiro.' },
        { id: 2, nome: 'Renato Motoboy', contato: '11977778888', tipo: 'ENTREGADOR', alerta: 'Alerta Viário: Evite rotas baixas sob risco de retenção.' },
        { id: 3, nome: 'Maria Clara Souza', contato: '11966665555', tipo: 'MOBILIDADE_REDUZIDA', alerta: 'ALERTA PRIORITÁRIO: Calçadas submersas, busque abrigo imediato.' }
      ]);
      setReportes([
        { id: 1, idRua: 1, nomeRua: 'Avenida das Nações Unidas', nomeBairro: 'Santo Amaro', idUsuario: 2, nomeUsuario: 'Renato Motoboy', tipoUsuario: 'ENTREGADOR', nivel: 'GRAVE', dataHora: '2026-11-14T17:00:00', descricao: 'Pista expressa completamente alagada sob a ponte.' },
        { id: 2, idRua: 4, nomeRua: 'Rua Olivia Guedes Penteado', nomeBairro: 'Socorro', idUsuario: 3, nomeUsuario: 'Maria Clara', tipoUsuario: 'MOBILIDADE_REDUZIDA', nivel: 'GRAVE', dataHora: '2026-11-14T17:15:00', descricao: 'Bueiros transbordando, cadeirantes sem acesso.' }
      ]);
      setAbrigos([
        { id: 1, idAbrigo: 1, nomeAbrigo: 'Ginásio Santo Amaro', endereco: 'Av. Padre José Maria, 555', capacidade: 350, distanciaKm: 0.8 },
        { id: 2, idAbrigo: 2, nomeAbrigo: 'Centro Defesa Civil Socorro', endereco: 'Rua Cassiano dos Santos, 120', capacidade: 180, distanciaKm: 0.5 }
      ]);
      setAreasCriticas([
        { id: 1, rua: 'Avenida das Nações Unidas', bairro: 'Santo Amaro', totalReportes: 3, alerta: 'ALERTA_VERMELHO' },
        { id: 2, rua: 'Rua Olivia Guedes Penteado', bairro: 'Socorro', totalReportes: 2, alerta: 'ALERTA_LARANJA' }
      ]);
    }
  };

  // Submissão de novo reporte com validação JS rigorosa
  const handleSubmitReporte = async (e) => {
    e.preventDefault();
    setFeedback({ tipo: '', msg: '' });

    // Validação JS
    if (!formReporte.idRua) {
      setFeedback({ tipo: 'danger', msg: 'Por favor, selecione o logradouro / rua afetada.' });
      return;
    }
    if (!formReporte.idUsuario) {
      setFeedback({ tipo: 'danger', msg: 'Por favor, selecione o cidadão informante.' });
      return;
    }
    if (!formReporte.descricao || formReporte.descricao.trim().length < 5) {
      setFeedback({ tipo: 'danger', msg: 'A descrição da situação deve conter no mínimo 5 caracteres.' });
      return;
    }

    if (backendConectado) {
      try {
        const resp = await fetch(`${API_BASE}/reportes`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            idRua: formReporte.idRua,
            idUsuario: formReporte.idUsuario,
            nivel: formReporte.nivel,
            descricao: formReporte.descricao
          })
        });

        if (resp.ok) {
          setFeedback({ tipo: 'success', msg: 'Reporte registrado com sucesso! Risco recalculado e gravado no banco.' });
          setFormReporte({ idRua: '', idUsuario: '', nivel: 'MEDIO', descricao: '' });
          carregarDados();
        } else {
          const err = await resp.json();
          setFeedback({ tipo: 'danger', msg: 'Erro ao gravar no banco: ' + (err.erro || 'Falha de comunicação') });
        }
      } catch (err) {
        setFeedback({ tipo: 'danger', msg: 'Erro de conexão com a API Java.' });
      }
    } else {
      // Demonstração local caso backend não esteja ativo
      const ruaSel = ruas.find(r => String(r.id) === String(formReporte.idRua));
      const userSel = usuarios.find(u => String(u.id) === String(formReporte.idUsuario));
      const novo = {
        id: Date.now(),
        idRua: formReporte.idRua,
        nomeRua: ruaSel ? ruaSel.nome : 'Rua',
        nomeBairro: ruaSel ? ruaSel.nomeBairro : 'Bairro',
        idUsuario: formReporte.idUsuario,
        nomeUsuario: userSel ? userSel.nome : 'Cidadão',
        tipoUsuario: userSel ? userSel.tipo : 'MORADOR',
        nivel: formReporte.nivel,
        dataHora: new Date().toISOString(),
        descricao: formReporte.descricao
      };
      setReportes([novo, ...reportes]);
      setFeedback({ tipo: 'success', msg: 'Reporte registrado localmente (Inicie o backend Java para persistir no H2 Database).' });
      setFormReporte({ idRua: '', idUsuario: '', nivel: 'MEDIO', descricao: '' });
    }
  };

  // Exclusão de reporte (CRUD JDBC)
  const handleExcluirReporte = async (id) => {
    if (!window.confirm(`Deseja realmente excluir o reporte de alagamento #${id}?`)) return;

    if (backendConectado) {
      try {
        const resp = await fetch(`${API_BASE}/reportes?id=${id}`, { method: 'DELETE' });
        if (resp.ok) {
          setFeedback({ tipo: 'success', msg: `Reporte #${id} excluído com sucesso do banco de dados!` });
          carregarDados();
        } else {
          setFeedback({ tipo: 'danger', msg: 'Não foi possível excluir o reporte.' });
        }
      } catch (err) {
        setFeedback({ tipo: 'danger', msg: 'Erro de comunicação ao excluir.' });
      }
    } else {
      setReportes(reportes.filter(r => r.id !== id));
      setFeedback({ tipo: 'success', msg: `Reporte #${id} removido da lista!` });
    }
  };

  return (
    <div>
      {/* Header com Navegação Semântica Unificada */}
      <header>
        <div className="navbar">
          <a href="#monitoramento" className="brand" onClick={() => setPagina('monitoramento')}>
            🌊 Rota Seca <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>CitizenTech FIAP</span>
          </a>

          <nav>
            <ul className="nav-links">
              <li>
                <button 
                  className={`nav-btn ${pagina === 'monitoramento' ? 'active' : ''}`}
                  onClick={() => setPagina('monitoramento')}
                >
                  📡 Monitoramento & Riscos
                </button>
              </li>
              <li>
                <button 
                  className={`nav-btn ${pagina === 'reportar' ? 'active' : ''}`}
                  onClick={() => setPagina('reportar')}
                >
                  ⚠️ Reportar Alagamento
                </button>
              </li>
              <li>
                <button 
                  className={`nav-btn ${pagina === 'abrigos' ? 'active' : ''}`}
                  onClick={() => setPagina('abrigos')}
                >
                  🏠 Abrigos Seguros (N:N)
                </button>
              </li>
              <li>
                <button 
                  className={`nav-btn ${pagina === 'cidadaos' ? 'active' : ''}`}
                  onClick={() => setPagina('cidadaos')}
                >
                  👥 Cidadãos (POO)
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </header>

      <main className="container">
        {/* Status de Conexão com o Backend */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '0.5rem 0 1rem' }}>
          <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>
            Status da Conexão Backend: {backendConectado ? (
              <span style={{ color: '#34d399', fontWeight: 'bold' }}>● ONLINE (Conectado via JDBC H2)</span>
            ) : (
              <span style={{ color: '#fbbf24', fontWeight: 'bold' }}>○ MODO OFFLINE (Execute `java br.com.rotaseca.server.ServidorHttpApi`)</span>
            )}
          </div>
          <button className="btn" style={{ fontSize: '0.8rem', padding: '0.3rem 0.6rem', background: '#334155', color: '#f8fafc' }} onClick={carregarDados}>
            🔄 Atualizar Dados
          </button>
        </div>

        {/* Mensagens de Feedback */}
        {feedback.msg && (
          <div className={`alert alert-${feedback.tipo}`}>
            {feedback.msg}
          </div>
        )}

        {/* PÁGINA 1: MONITORAMENTO & RISCOS */}
        {pagina === 'monitoramento' && (
          <section>
            <h2>Painel Climático da Zona Sul</h2>
            <p style={{ color: 'var(--text-muted)' }}>
              Cálculo ponderado em tempo real de risco por bairro e identificação de áreas críticas recorrentes.
            </p>

            {/* Grid dos Bairros */}
            <div className="grid-3">
              {bairros.map(b => (
                <div key={b.id} className="card">
                  <h3>
                    {b.nome}
                    <span className={`badge badge-${(b.nivelRisco || 'BAIXO').toLowerCase()}`}>
                      {b.nivelRisco}
                    </span>
                  </h3>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Região: {b.zona}</p>
                  <p style={{ marginTop: '0.5rem', fontSize: '0.85rem' }}>
                    {b.nivelRisco === 'ALTO' && '🚨 Atenção máxima: Evite trânsito local. Abrigos em alerta.'}
                    {b.nivelRisco === 'MEDIO' && '⚠️ Tráfego lento em vias principais. Monitore chuvas.'}
                    {b.nivelRisco === 'BAIXO' && '✅ Vias normais e fluxo sem retenções críticas.'}
                  </p>
                </div>
              ))}
            </div>

            {/* Áreas Críticas Identificadas */}
            <div className="card" style={{ marginTop: '2rem' }}>
              <h3>🔥 Áreas Críticas Recorrentes (Histórico Severo de Alagamento)</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1rem' }}>
                Regra da Camada Service: Locais que atingiram limite de múltiplos reportes ou alagamentos graves.
              </p>
              <div className="table-responsive">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Rua / Logradouro</th>
                      <th>Bairro</th>
                      <th>Ocorrências</th>
                      <th>Status de Alerta</th>
                    </tr>
                  </thead>
                  <tbody>
                    {areasCriticas.length === 0 ? (
                      <tr><td colSpan="5">Nenhuma área crítica registrada no momento.</td></tr>
                    ) : (
                      areasCriticas.map(a => (
                        <tr key={a.id}>
                          <td>#{a.id}</td>
                          <td><strong>{a.rua}</strong></td>
                          <td>{a.bairro}</td>
                          <td>{a.totalReportes} reportes</td>
                          <td>
                            <span className={`badge badge-${a.alerta.includes('VERMELHO') ? 'grave' : 'medio'}`}>
                              {a.alerta}
                            </span>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Listagem de Reportes Recentes */}
            <div className="card" style={{ marginTop: '2rem' }}>
              <h3>📋 Últimos Reportes da População</h3>
              <div className="table-responsive">
                <table>
                  <thead>
                    <tr>
                      <th>Horário</th>
                      <th>Logradouro</th>
                      <th>Bairro</th>
                      <th>Cidadão</th>
                      <th>Nível</th>
                      <th>Descrição</th>
                      <th>Ações</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportes.map(r => (
                      <tr key={r.id}>
                        <td>{r.dataHora ? r.dataHora.replace('T', ' ').substring(0, 16) : ''}</td>
                        <td><strong>{r.nomeRua}</strong></td>
                        <td>{r.nomeBairro}</td>
                        <td>{r.nomeUsuario} <small style={{ color: 'var(--text-muted)' }}>({r.tipoUsuario})</small></td>
                        <td>
                          <span className={`badge badge-${(r.nivel || 'LEVE').toLowerCase()}`}>
                            {r.nivel}
                          </span>
                        </td>
                        <td>{r.descricao}</td>
                        <td>
                          <button 
                            className="btn btn-danger"
                            onClick={() => handleExcluirReporte(r.id)}
                            title="Excluir reporte via JDBC"
                          >
                            Excluir
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </section>
        )}

        {/* PÁGINA 2: REPORTAR ALAGAMENTO (FORMULÁRIO CRUD) */}
        {pagina === 'reportar' && (
          <section>
            <h2>Registrar Ocorrência de Alagamento</h2>
            <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
              Colabore com a sua comunidade. O reporte atualiza imediatamente o índice de risco do bairro e notifica entregadores e pessoas com mobilidade reduzida.
            </p>

            <div className="card" style={{ maxWidth: '650px', margin: '0 auto' }}>
              <form onSubmit={handleSubmitReporte}>
                <div className="form-group">
                  <label htmlFor="idRua">Logradouro / Rua Afetada *</label>
                  <select 
                    id="idRua" 
                    className="form-control"
                    value={formReporte.idRua}
                    onChange={e => setFormReporte({ ...formReporte, idRua: e.target.value })}
                  >
                    <option value="">Selecione uma rua cadastrada...</option>
                    {ruas.map(r => (
                      <option key={r.id} value={r.id}>
                        {r.nome} ({r.nomeBairro}) - CEP {r.cep}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="idUsuario">Cidadão Informante *</label>
                  <select 
                    id="idUsuario" 
                    className="form-control"
                    value={formReporte.idUsuario}
                    onChange={e => setFormReporte({ ...formReporte, idUsuario: e.target.value })}
                  >
                    <option value="">Identifique o cidadão...</option>
                    {usuarios.map(u => (
                      <option key={u.id} value={u.id}>
                        {u.nome} - {u.tipo} ({u.contato})
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Nível da Lâmina d'Água (Severidade) *</label>
                  <select 
                    className="form-control"
                    value={formReporte.nivel}
                    onChange={e => setFormReporte({ ...formReporte, nivel: e.target.value })}
                  >
                    <option value="LEVE">LEVE (Água rasa / poça acumulada - Peso 1)</option>
                    <option value="MEDIO">MÉDIO (Água no meio-fio / trânsito difícil - Peso 2)</option>
                    <option value="GRAVE">GRAVE (Via intransitável / perigo iminente - Peso 3)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="descricao">Descrição da Situação *</label>
                  <textarea 
                    id="descricao" 
                    className="form-control"
                    placeholder="Ex: Trecho sob o viaduto está com água na altura do pneu dos carros..."
                    value={formReporte.descricao}
                    onChange={e => setFormReporte({ ...formReporte, descricao: e.target.value })}
                  />
                </div>

                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                  Enviar Reporte para a Base de Dados
                </button>
              </form>
            </div>
          </section>
        )}

        {/* PÁGINA 3: ABRIGOS SEGUROS (RELACIONAMENTO N:N) */}
        {pagina === 'abrigos' && (
          <section>
            <h2>Abrigos de Emergência Comunitária</h2>
            <p style={{ color: 'var(--text-muted)' }}>
              Relacionamento N:N com tabela associativa `ABRIGO_BAIRRO` contendo a distância operacional em quilômetros.
            </p>

            <div className="card" style={{ marginTop: '1.5rem', marginBottom: '1.5rem' }}>
              <label htmlFor="filtroBairro" style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>
                Filtrar abrigos por Bairro de Referência:
              </label>
              <select 
                id="filtroBairro" 
                className="form-control"
                value={bairroFiltroAbrigo}
                onChange={e => setBairroFiltroAbrigo(e.target.value)}
              >
                <option value="">Exibir abrigos de todos os bairros</option>
                {bairros.map(b => (
                  <option key={b.id} value={b.id}>
                    {b.nome} (Risco Atual: {b.nivelRisco})
                  </option>
                ))}
              </select>
            </div>

            <div className="grid-2">
              {abrigos.map((a, idx) => (
                <div key={idx} className="card">
                  <h3>🏠 {a.nomeAbrigo || a.nome}</h3>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>📍 {a.endereco}</p>
                  <p style={{ marginTop: '0.5rem' }}>
                    Capacidade: <strong>{a.capacidade} pessoas</strong>
                  </p>
                  {a.distanciaKm !== undefined && (
                    <p style={{ color: '#38bdf8', fontWeight: 'bold', marginTop: '0.25rem' }}>
                      Distância do Bairro: {a.distanciaKm.toFixed(2)} km
                    </p>
                  )}
                  <div style={{ marginTop: '0.75rem', fontSize: '0.85rem', color: '#10b981' }}>
                    ✓ Ponto seguro com suporte da Defesa Civil e acessibilidade.
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* PÁGINA 4: CIDADÃOS BENEFICIADOS (POO COM HERANÇA E POLIMORFISMO) */}
        {pagina === 'cidadaos' && (
          <section>
            <h2>Cidadãos Atendidos (CitizenTech)</h2>
            <p style={{ color: 'var(--text-muted)' }}>
              Demonstração de POO avançada com herança a partir de `Usuario`: `CidadaoMorador`, `Entregador` e `CidadaoMobilidadeReduzida`.
            </p>

            <div className="grid-3" style={{ marginTop: '1.5rem' }}>
              {usuarios.map(u => (
                <div key={u.id} className="card">
                  <h3>
                    {u.nome}
                    <span className="badge" style={{ background: '#334155', color: '#38bdf8' }}>
                      {u.tipo}
                    </span>
                  </h3>
                  <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Contato: {u.contato}</p>
                  {u.info && <p style={{ fontSize: '0.85rem', marginTop: '0.25rem' }}>Info: {u.info}</p>}
                  
                  <div style={{ marginTop: '1rem', padding: '0.75rem', background: '#0b1120', borderRadius: '6px', fontSize: '0.85rem' }}>
                    <strong style={{ color: '#f59e0b', display: 'block', marginBottom: '0.25rem' }}>Regra Polimórfica de Alerta:</strong>
                    {u.alerta}
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}
      </main>

      {/* Footer */}
      <footer>
        <p><strong>CitizenTech Rota Seca</strong> • Global Solution 2026/2 FIAP • 2º Ano Engenharia de Software</p>
        <p style={{ fontSize: '0.8rem', marginTop: '0.25rem' }}>ODS 11 (Cidades Sustentáveis) | ODS 13 (Ação Climática) | ODS 10 (Redução das Desigualdades)</p>
      </footer>
    </div>
  );
}
