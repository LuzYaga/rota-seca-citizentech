package br.com.rotaseca.dao;

import br.com.rotaseca.model.AreaCritica;
import br.com.rotaseca.model.Bairro;
import br.com.rotaseca.model.Rua;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento e persistência de AreaCritica via JDBC.
 */
public class AreaCriticaDAO implements Repositorio<AreaCritica> {
    private final BairroDAO bairroDAO = new BairroDAO();
    private final RuaDAO ruaDAO = new RuaDAO();

    @Override
    public AreaCritica inserir(AreaCritica area) {
        String sql = "INSERT INTO AREA_CRITICA (id_bairro, id_rua, total_reportes, data_marcacao, nivel_alerta) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, area.getBairro().getId());
            ps.setLong(2, area.getRua().getId());
            ps.setInt(3, area.getTotalReportes());
            ps.setTimestamp(4, Timestamp.valueOf(area.getDataMarcacao() != null ? area.getDataMarcacao() : LocalDateTime.now()));
            ps.setString(5, area.getNivelAlerta());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    area.setId(rs.getLong(1));
                }
            }
            return area;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AreaCritica.inserir] " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean atualizar(AreaCritica area) {
        String sql = "UPDATE AREA_CRITICA SET id_bairro = ?, id_rua = ?, total_reportes = ?, nivel_alerta = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, area.getBairro().getId());
            ps.setLong(2, area.getRua().getId());
            ps.setInt(3, area.getTotalReportes());
            ps.setString(4, area.getNivelAlerta());
            ps.setLong(5, area.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AreaCritica.atualizar] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM AREA_CRITICA WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AreaCritica.excluir] " + e.getMessage());
            return false;
        }
    }

    @Override
    public AreaCritica buscarPorId(Long id) {
        String sql = "SELECT id, id_bairro, id_rua, total_reportes, data_marcacao, nivel_alerta FROM AREA_CRITICA WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAreaCritica(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AreaCritica.buscarPorId] " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<AreaCritica> listarTodos() {
        List<AreaCritica> lista = new ArrayList<>();
        String sql = "SELECT id, id_bairro, id_rua, total_reportes, data_marcacao, nivel_alerta FROM AREA_CRITICA ORDER BY total_reportes DESC";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearAreaCritica(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AreaCritica.listarTodos] " + e.getMessage());
        }
        return lista;
    }

    public AreaCritica buscarPorRua(Long idRua) {
        String sql = "SELECT id, id_bairro, id_rua, total_reportes, data_marcacao, nivel_alerta FROM AREA_CRITICA WHERE id_rua = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRua);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAreaCritica(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AreaCritica.buscarPorRua] " + e.getMessage());
        }
        return null;
    }

    private AreaCritica mapearAreaCritica(ResultSet rs) throws SQLException {
        Bairro bairro = bairroDAO.buscarPorId(rs.getLong("id_bairro"));
        Rua rua = ruaDAO.buscarPorId(rs.getLong("id_rua"));
        Timestamp ts = rs.getTimestamp("data_marcacao");
        LocalDateTime dataMarcacao = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

        return new AreaCritica(
            rs.getLong("id"),
            bairro,
            rua,
            rs.getInt("total_reportes"),
            dataMarcacao,
            rs.getString("nivel_alerta")
        );
    }
}
