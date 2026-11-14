package br.com.rotaseca.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador central de conexões JDBC com o H2 Database.
 * Oferece inicialização automática das tabelas e scripts caso o banco não exista.
 */
public class ConexaoBanco {
    // Modo AUTO_SERVER permite que múltiplos processos (backend Java + CLI ou Browser) acessem o arquivo localmente
    private static final String URL = "jdbc:h2:./database/rotaseca;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERRO CRÍTICO] Driver JDBC H2 não localizado no classpath: " + e.getMessage());
        }
    }

    /**
     * Obtém uma nova conexão aberta com o banco.
     */
    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Inicializa a estrutura do banco de dados caso as tabelas ainda não existam.
     */
    public static void inicializarBancoSeNecessario() {
        try (Connection conn = obterConexao(); Statement stmt = conn.createStatement()) {
            // Verifica se a tabela BAIRRO existe
            try {
                stmt.executeQuery("SELECT 1 FROM BAIRRO LIMIT 1");
            } catch (SQLException checkEx) {
                // Tabela não existe, carregar script database/schema.sql
                System.out.println("[INFO] Banco de dados inicializando estrutura...");
                java.io.File scriptFile = new java.io.File("database/schema.sql");
                if (scriptFile.exists()) {
                    org.h2.tools.RunScript.execute(conn, new java.io.FileReader(scriptFile));
                    System.out.println("[INFO] Estrutura e dados iniciais carregados com sucesso.");
                }
            }
        } catch (Exception e) {
            System.err.println("[AVISO] Inicialização do banco: " + e.getMessage());
        }
    }
}
