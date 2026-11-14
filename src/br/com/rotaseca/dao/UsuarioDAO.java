package br.com.rotaseca.dao;

import br.com.rotaseca.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento e persistência de Usuario com polimorfismo via JDBC.
 */
public class UsuarioDAO implements Repositorio<Usuario> {

    @Override
    public Usuario inserir(Usuario usuario) {
        String sql = "INSERT INTO USUARIO (nome, contato, tipo, info_adicional) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getContato());
            ps.setString(3, usuario.getTipo());
            ps.setString(4, usuario.getInfoAdicional());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getLong(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Usuario.inserir] " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE USUARIO SET nome = ?, contato = ?, tipo = ?, info_adicional = ? WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getContato());
            ps.setString(3, usuario.getTipo());
            ps.setString(4, usuario.getInfoAdicional());
            ps.setLong(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Usuario.atualizar] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM USUARIO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Usuario.excluir] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario buscarPorId(Long id) {
        String sql = "SELECT id, nome, contato, tipo, info_adicional FROM USUARIO WHERE id = ?";
        try (Connection conn = ConexaoBanco.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Usuario.buscarPorId] " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, contato, tipo, info_adicional FROM USUARIO ORDER BY nome";
        try (Connection conn = ConexaoBanco.obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(instanciarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO DAO - Usuario.listarTodos] " + e.getMessage());
        }
        return lista;
    }

    private Usuario instanciarUsuario(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nome = rs.getString("nome");
        String contato = rs.getString("contato");
        String tipo = rs.getString("tipo");
        String info = rs.getString("info_adicional");

        if ("ENTREGADOR".equalsIgnoreCase(tipo)) {
            return new Entregador(id, nome, contato, info != null ? info.replace("Veículo: ", "") : "Moto");
        } else if ("MOBILIDADE_REDUZIDA".equalsIgnoreCase(tipo)) {
            return new CidadaoMobilidadeReduzida(id, nome, contato, info);
        } else {
            return new CidadaoMorador(id, nome, contato, info);
        }
    }
}
