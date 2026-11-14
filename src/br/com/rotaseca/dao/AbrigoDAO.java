package br.com.rotaseca.dao;

import br.com.rotaseca.model.Abrigo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento e persistência de Abrigo via JDBC.
 */
public class AbrigoDAO implements Repositorio<Abrigo> {

    @Override
    public Abrigo inserir(Abrigo abrigo) {
        String sql = "INSERT INTO ABRIGO (nome, endereco, capacidade) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, abrigo.getNome());
            ps.setString(2, abrigo.getEndereco());
            ps.setInt(3, abrigo.getCapacidade());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    abrigo.setId(rs.getLong(1));
                }
            }
            return abrigo;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Abrigo.inserir] " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean atualizar(Abrigo abrigo) {
        String sql = "UPDATE ABRIGO SET nome = ?, endereco = ?, capacidade = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, abrigo.getNome());
            ps.setString(2, abrigo.getEndereco());
            ps.setInt(3, abrigo.getCapacidade());
            ps.setLong(4, abrigo.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Abrigo.atualizar] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM ABRIGO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Abrigo.excluir] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Abrigo buscarPorId(Long id) {
        String sql = "SELECT id, nome, endereco, capacidade FROM ABRIGO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Abrigo(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("endereco"),
                        rs.getInt("capacidade")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Abrigo.buscarPorId] " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Abrigo> listarTodos() {
        List<Abrigo> lista = new ArrayList<>();
        String sql = "SELECT id, nome, endereco, capacidade FROM ABRIGO ORDER BY nome";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Abrigo(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("endereco"),
                    rs.getInt("capacidade")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Abrigo.listarTodos] " + e.getMessage());
        }
        return lista;
    }
}
