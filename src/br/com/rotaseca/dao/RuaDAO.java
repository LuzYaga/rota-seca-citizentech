package br.com.rotaseca.dao;

import br.com.rotaseca.model.Bairro;
import br.com.rotaseca.model.Rua;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento e persistência de Rua via JDBC.
 */
public class RuaDAO implements Repositorio<Rua> {
    private final BairroDAO bairroDAO = new BairroDAO();

    @Override
    public Rua inserir(Rua rua) {
        String sql = "INSERT INTO RUA (nome, cep, id_bairro) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rua.getNome());
            ps.setString(2, rua.getCep());
            ps.setLong(3, rua.getBairro().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rua.setId(rs.getLong(1));
                }
            }
            return rua;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Rua.inserir] " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean atualizar(Rua rua) {
        String sql = "UPDATE RUA SET nome = ?, cep = ?, id_bairro = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rua.getNome());
            ps.setString(2, rua.getCep());
            ps.setLong(3, rua.getBairro().getId());
            ps.setLong(4, rua.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Rua.atualizar] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM RUA WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Rua.excluir] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Rua buscarPorId(Long id) {
        String sql = "SELECT id, nome, cep, id_bairro FROM RUA WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearRua(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Rua.buscarPorId] " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Rua> listarTodos() {
        List<Rua> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cep, id_bairro FROM RUA ORDER BY nome";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearRua(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Rua.listarTodos] " + e.getMessage());
        }
        return lista;
    }

    public List<Rua> listarPorBairro(Long idBairro) {
        List<Rua> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cep, id_bairro FROM RUA WHERE id_bairro = ? ORDER BY nome";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idBairro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRua(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Rua.listarPorBairro] " + e.getMessage());
        }
        return lista;
    }

    private Rua mapearRua(ResultSet rs) throws SQLException {
        Long idBairro = rs.getLong("id_bairro");
        Bairro bairro = bairroDAO.buscarPorId(idBairro);
        return new Rua(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("cep"),
            bairro
        );
    }
}
