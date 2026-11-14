package br.com.rotaseca.dao;

import br.com.rotaseca.model.NivelAlagamento;
import br.com.rotaseca.model.ReporteAlagamento;
import br.com.rotaseca.model.Rua;
import br.com.rotaseca.model.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento e persistência de ReporteAlagamento via JDBC.
 */
public class ReporteDAO implements Repositorio<ReporteAlagamento> {
    private final RuaDAO ruaDAO = new RuaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public ReporteAlagamento inserir(ReporteAlagamento reporte) {
        String sql = "INSERT INTO REPORTE_ALAGAMENTO (id_rua, id_usuario, nivel, data_hora, descricao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, reporte.getRua().getId());
            ps.setLong(2, reporte.getUsuario().getId());
            ps.setString(3, reporte.getNivel().name());
            ps.setTimestamp(4, Timestamp.valueOf(reporte.getDataHora() != null ? reporte.getDataHora() : LocalDateTime.now()));
            ps.setString(5, reporte.getDescricao());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reporte.setId(rs.getLong(1));
                }
            }
            return reporte;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.inserir] " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean atualizar(ReporteAlagamento reporte) {
        String sql = "UPDATE REPORTE_ALAGAMENTO SET id_rua = ?, id_usuario = ?, nivel = ?, descricao = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reporte.getRua().getId());
            ps.setLong(2, reporte.getUsuario().getId());
            ps.setString(3, reporte.getNivel().name());
            ps.setString(4, reporte.getDescricao());
            ps.setLong(5, reporte.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.atualizar] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM REPORTE_ALAGAMENTO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.excluir] " + e.getMessage());
            return false;
        }
    }

    @Override
    public ReporteAlagamento buscarPorId(Long id) {
        String sql = "SELECT id, id_rua, id_usuario, nivel, data_hora, descricao FROM REPORTE_ALAGAMENTO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearReporte(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.buscarPorId] " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ReporteAlagamento> listarTodos() {
        List<ReporteAlagamento> lista = new ArrayList<>();
        String sql = "SELECT id, id_rua, id_usuario, nivel, data_hora, descricao FROM REPORTE_ALAGAMENTO ORDER BY data_hora DESC";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearReporte(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.listarTodos] " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteAlagamento> listarPorBairro(Long idBairro) {
        List<ReporteAlagamento> lista = new ArrayList<>();
        String sql = "SELECT r.id, r.id_rua, r.id_usuario, r.nivel, r.data_hora, r.descricao " +
                     "FROM REPORTE_ALAGAMENTO r " +
                     "INNER JOIN RUA ru ON r.id_rua = ru.id " +
                     "WHERE ru.id_bairro = ? ORDER BY r.data_hora DESC";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idBairro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearReporte(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.listarPorBairro] " + e.getMessage());
        }
        return lista;
    }

    public int contarReportesPorRua(Long idRua) {
        String sql = "SELECT COUNT(id) FROM REPORTE_ALAGAMENTO WHERE id_rua = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRua);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Reporte.contarReportesPorRua] " + e.getMessage());
        }
        return 0;
    }

    private ReporteAlagamento mapearReporte(ResultSet rs) throws SQLException {
        Rua rua = ruaDAO.buscarPorId(rs.getLong("id_rua"));
        Usuario usuario = usuarioDAO.buscarPorId(rs.getLong("id_usuario"));
        Timestamp ts = rs.getTimestamp("data_hora");
        LocalDateTime dataHora = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

        return new ReporteAlagamento(
            rs.getLong("id"),
            rua,
            usuario,
            NivelAlagamento.fromString(rs.getString("nivel")),
            dataHora,
            rs.getString("descricao")
        );
    }
}
