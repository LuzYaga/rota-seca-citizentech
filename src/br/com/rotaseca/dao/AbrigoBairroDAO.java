package br.com.rotaseca.dao;

import br.com.rotaseca.model.Abrigo;
import br.com.rotaseca.model.AbrigoBairro;
import br.com.rotaseca.model.Bairro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento da tabela associativa N:N ABRIGO_BAIRRO via JDBC.
 */
public class AbrigoBairroDAO {
    private final AbrigoDAO abrigoDAO = new AbrigoDAO();
    private final BairroDAO bairroDAO = new BairroDAO();

    public boolean vincular(Long idAbrigo, Long idBairro, double distanciaKm) {
        String sql = "MERGE INTO ABRIGO_BAIRRO (id_abrigo, id_bairro, distancia_km) KEY(id_abrigo, id_bairro) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAbrigo);
            ps.setLong(2, idBairro);
            ps.setDouble(3, distanciaKm);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AbrigoBairro.vincular] " + e.getMessage());
            return false;
        }
    }

    public boolean desvincular(Long idAbrigo, Long idBairro) {
        String sql = "DELETE FROM ABRIGO_BAIRRO WHERE id_abrigo = ? AND id_bairro = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAbrigo);
            ps.setLong(2, idBairro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AbrigoBairro.desvincular] " + e.getMessage());
            return false;
        }
    }

    public List<AbrigoBairro> listarAbrigosPorBairro(Long idBairro) {
        List<AbrigoBairro> lista = new ArrayList<>();
        String sql = "SELECT id_abrigo, id_bairro, distancia_km FROM ABRIGO_BAIRRO WHERE id_bairro = ? ORDER BY distancia_km ASC";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idBairro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Abrigo abrigo = abrigoDAO.buscarPorId(rs.getLong("id_abrigo"));
                    Bairro bairro = bairroDAO.buscarPorId(rs.getLong("id_bairro"));
                    lista.add(new AbrigoBairro(abrigo, bairro, rs.getDouble("distancia_km")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AbrigoBairro.listarAbrigosPorBairro] " + e.getMessage());
        }
        return lista;
    }

    public List<AbrigoBairro> listarTodos() {
        List<AbrigoBairro> lista = new ArrayList<>();
        String sql = "SELECT id_abrigo, id_bairro, distancia_km FROM ABRIGO_BAIRRO ORDER BY id_bairro, distancia_km ASC";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Abrigo abrigo = abrigoDAO.buscarPorId(rs.getLong("id_abrigo"));
                Bairro bairro = bairroDAO.buscarPorId(rs.getLong("id_bairro"));
                lista.add(new AbrigoBairro(abrigo, bairro, rs.getDouble("distancia_km")));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - AbrigoBairro.listarTodos] " + e.getMessage());
        }
        return lista;
    }
}
