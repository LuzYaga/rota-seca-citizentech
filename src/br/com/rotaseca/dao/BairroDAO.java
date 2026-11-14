package br.com.rotaseca.dao;

import br.com.rotaseca.model.Bairro;
import br.com.rotaseca.model.NivelRisco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento e persistência de Bairro via JDBC.
 */
public class BairroDAO implements Repositorio<Bairro> {

    @Override
    public Bairro inserir(Bairro bairro) {
        String sql = "INSERT INTO BAIRRO (nome, zona, nivel_risco) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bairro.getNome());
            ps.setString(2, bairro.getZona());
            ps.setString(3, bairro.getNivelRisco().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bairro.setId(rs.getLong(1));
                }
            }
            return bairro;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Bairro.inserir] " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean atualizar(Bairro bairro) {
        String sql = "UPDATE BAIRRO SET nome = ?, zona = ?, nivel_risco = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bairro.getNome());
            ps.setString(2, bairro.getZona());
            ps.setString(3, bairro.getNivelRisco().name());
            ps.setLong(4, bairro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Bairro.atualizar] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM BAIRRO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Bairro.excluir] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Bairro buscarPorId(Long id) {
        String sql = "SELECT id, nome, zona, nivel_risco FROM BAIRRO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearBairro(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Bairro.buscarPorId] " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Bairro> listarTodos() {
        List<Bairro> lista = new ArrayList<>();
        String sql = "SELECT id, nome, zona, nivel_risco FROM BAIRRO ORDER BY nome";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearBairro(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Bairro.listarTodos] " + e.getMessage());
        }
        return lista;
    }

    public boolean atualizarNivelRisco(Long idBairro, NivelRisco novoRisco) {
        String sql = "UPDATE BAIRRO SET nivel_risco = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoRisco.name());
            ps.setLong(2, idBairro);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Bairro.atualizarNivelRisco] " + e.getMessage());
            return false;
        }
    }

    private Bairro mapearBairro(ResultSet rs) throws SQLException {
        return new Bairro(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("zona"),
            NivelRisco.fromString(rs.getString("nivel_risco"))
        );
    }
}
