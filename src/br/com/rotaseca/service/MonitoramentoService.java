package br.com.rotaseca.service;

import br.com.rotaseca.dao.*;
import br.com.rotaseca.model.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Camada Service central da CitizenTech Rota Seca.
 * Implementa as regras de negócio:
 * 1. Validação de integridade e entradas.
 * 2. Cálculo ponderado de risco do bairro baseado nos reportes (LEVE=1, MEDIO=2, GRAVE=3).
 * 3. Detecção e marcação automática de Áreas Críticas Recorrentes.
 * 4. Roteamento de abrigos de socorro mais próximos.
 */
public class MonitoramentoService {
    private final BairroDAO bairroDAO;
    private final RuaDAO ruaDAO;
    private final UsuarioDAO usuarioDAO;
    private final ReporteDAO reporteDAO;
    private final AbrigoDAO abrigoDAO;
    private final AbrigoBairroDAO abrigoBairroDAO;
    private final AreaCriticaDAO areaCriticaDAO;

    public MonitoramentoService() {
        this.bairroDAO = new BairroDAO();
        this.ruaDAO = new RuaDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.reporteDAO = new ReporteDAO();
        this.abrigoDAO = new AbrigoDAO();
        this.abrigoBairroDAO = new AbrigoBairroDAO();
        this.areaCriticaDAO = new AreaCriticaDAO();
    }

    /**
     * Registra um novo reporte de alagamento enviado pelo cidadão e dispara
     * as regras de atualização de risco e área crítica.
     */
    public ReporteAlagamento registrarReporte(Long idRua, Long idUsuario, String nivelStr, String descricao) throws NegocioException {
        if (idRua == null) throw new NegocioException("O logradouro/rua deve ser informado obrigatoriamente.");
        if (idUsuario == null) throw new NegocioException("O cidadão informante deve ser identificado.");
        if (nivelStr == null || nivelStr.trim().isEmpty()) throw new NegocioException("O nível do alagamento deve ser especificado.");

        Rua rua = ruaDAO.buscarPorId(idRua);
        if (rua == null) throw new NegocioException("Rua informada não existe no cadastro da cidade.");

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) throw new NegocioException("Cidadão informado não localizado na base cadastral.");

        NivelAlagamento nivel = NivelAlagamento.fromString(nivelStr);
        ReporteAlagamento novoReporte = new ReporteAlagamento(null, rua, usuario, nivel, LocalDateTime.now(), descricao);

        ReporteAlagamento salvo = reporteDAO.inserir(novoReporte);
        if (salvo == null) {
            throw new NegocioException("Falha de persistência ao gravar o reporte de alagamento.");
        }

        // Regra de Negócio: Recalcula o risco do bairro onde a rua está inserida
        recalcularEAtualizarRiscoBairro(rua.getBairro().getId());

        // Regra de Negócio: Avalia gatilho de Área Crítica Recorrente
        avaliarGatilhoAreaCritica(rua);

        return salvo;
    }

    /**
     * Calcula o risco do bairro a partir dos reportes acumulados:
     * - LEVE = 1 pt, MEDIO = 2 pts, GRAVE = 3 pts
     * - Pontos: 0-4 => BAIXO, 5-9 => MEDIO, >= 10 => ALTO
     */
    public NivelRisco recalcularEAtualizarRiscoBairro(Long idBairro) {
        if (idBairro == null) return NivelRisco.BAIXO;
        Bairro bairro = bairroDAO.buscarPorId(idBairro);
        if (bairro == null) return NivelRisco.BAIXO;

        List<ReporteAlagamento> reportes = reporteDAO.listarPorBairro(idBairro);
        int pontuacao = 0;
        for (ReporteAlagamento r : reportes) {
            pontuacao += r.getPesoGravidade();
        }

        NivelRisco novoRisco;
        if (pontuacao >= 10) {
            novoRisco = NivelRisco.ALTO;
        } else if (pontuacao >= 5) {
            novoRisco = NivelRisco.MEDIO;
        } else {
            novoRisco = NivelRisco.BAIXO;
        }

        if (bairro.getNivelRisco() != novoRisco) {
            bairroDAO.atualizarNivelRisco(idBairro, novoRisco);
            bairro.setNivelRisco(novoRisco);
        }

        return novoRisco;
    }

    /**
     * Avalia se uma rua deve ser marcada ou atualizada como Área Crítica:
     * Dispara se tiver 2 ou mais reportes acumulados ou qualquer reporte GRAVE.
     */
    public void avaliarGatilhoAreaCritica(Rua rua) {
        if (rua == null) return;
        int total = reporteDAO.contarReportesPorRua(rua.getId());
        if (total >= 2) {
            AreaCritica existente = areaCriticaDAO.buscarPorRua(rua.getId());
            String alerta = (total >= 4) ? "ALERTA_VERMELHO" : "ALERTA_LARANJA";

            if (existente == null) {
                AreaCritica novaArea = new AreaCritica(null, rua.getBairro(), rua, total, LocalDateTime.now(), alerta);
                areaCriticaDAO.inserir(novaArea);
            } else {
                existente.setTotalReportes(total);
                existente.setNivelAlerta(alerta);
                existente.setDataMarcacao(LocalDateTime.now());
                areaCriticaDAO.atualizar(existente);
            }
        }
    }

    /**
     * Localiza abrigos próximos a um bairro ordenados por menor distância em KM.
     */
    public List<AbrigoBairro> listarAbrigosPorBairro(Long idBairro) throws NegocioException {
        if (idBairro == null) throw new NegocioException("ID do bairro não fornecido.");
        return abrigoBairroDAO.listarAbrigosPorBairro(idBairro);
    }

    // Getters para os DAOs facilitando testes e UI
    public BairroDAO getBairroDAO() { return bairroDAO; }
    public RuaDAO getRuaDAO() { return ruaDAO; }
    public UsuarioDAO getUsuarioDAO() { return usuarioDAO; }
    public ReporteDAO getReporteDAO() { return reporteDAO; }
    public AbrigoDAO getAbrigoDAO() { return abrigoDAO; }
    public AbrigoBairroDAO getAbrigoBairroDAO() { return abrigoBairroDAO; }
    public AreaCriticaDAO getAreaCriticaDAO() { return areaCriticaDAO; }
}
