package br.com.rotaseca.server;

import br.com.rotaseca.dao.ConexaoBanco;
import br.com.rotaseca.model.*;
import br.com.rotaseca.service.MonitoramentoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servidor HTTP REST embutido do próprio Java JDK (com.sun.net.httpserver.HttpServer).
 * NÃO utiliza Spring Boot nem frameworks externos proibidos pela Fase 7.
 * Expõe endpoints JSON consumidos pelo Front-end com suporte a CORS.
 */
public class ServidorHttpApi {
    private static final int PORTA = 8080;
    private final MonitoramentoService service;

    public ServidorHttpApi() {
        this.service = new MonitoramentoService();
    }

    public static void main(String[] args) throws IOException {
        ConexaoBanco.inicializarBancoSeNecessario();
        ServidorHttpApi api = new ServidorHttpApi();
        api.iniciar();
    }

    public void iniciar() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA), 0);
        System.out.println("===============================================================");
        System.out.println("  SERVIDOR HTTP REST (JAVA PURO - SEM FRAMEWORKS) INICIADO");
        System.out.println("  Porta: http://localhost:" + PORTA);
        System.out.println("===============================================================");

        server.createContext("/api/bairros", new BairrosHandler());
        server.createContext("/api/ruas", new RuasHandler());
        server.createContext("/api/usuarios", new UsuariosHandler());
        server.createContext("/api/reportes", new ReportesHandler());
        server.createContext("/api/abrigos", new AbrigosHandler());
        server.createContext("/api/areas-criticas", new AreasCriticasHandler());
        server.createContext("/api/status", new StatusHandler());

        server.setExecutor(null); // Executor padrão
        server.start();
    }

    private void configurarCorsEResposta(HttpExchange exchange, int statusCode, String respostaJson) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] bytes = respostaJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String lerCorpoRequisicao(HttpExchange exchange) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) {
                sb.append(linha);
            }
            return sb.toString();
        }
    }

    private Map<String, String> extrairJsonSimples(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String limpo = json.trim();
        if (limpo.startsWith("{")) limpo = limpo.substring(1);
        if (limpo.endsWith("}")) limpo = limpo.substring(0, limpo.length() - 1);

        String[] pares = limpo.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String par : pares) {
            String[] kv = par.split(":", 2);
            if (kv.length == 2) {
                String chave = kv[0].trim().replace("\"", "");
                String valor = kv[1].trim();
                if (valor.startsWith("\"") && valor.endsWith("\"")) {
                    valor = valor.substring(1, valor.length() - 1);
                }
                map.put(chave, valor);
            }
        }
        return map;
    }

    // HANDLER: /api/bairros
    class BairrosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Bairro> bairros = service.getBairroDAO().listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < bairros.size(); i++) {
                    Bairro b = bairros.get(i);
                    json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"zona\":\"%s\",\"nivelRisco\":\"%s\"}",
                            b.getId(), escapar(b.getNome()), escapar(b.getZona()), b.getNivelRisco().name()));
                    if (i < bairros.size() - 1) json.append(",");
                }
                json.append("]");
                configurarCorsEResposta(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String corpo = lerCorpoRequisicao(exchange);
                Map<String, String> dados = extrairJsonSimples(corpo);
                String nome = dados.get("nome");
                String zona = dados.get("zona");
                if (nome == null || zona == null) {
                    configurarCorsEResposta(exchange, 400, "{\"erro\":\"Nome e zona são obrigatórios\"}");
                    return;
                }
                Bairro novo = new Bairro(null, nome, zona, NivelRisco.BAIXO);
                service.getBairroDAO().inserir(novo);
                configurarCorsEResposta(exchange, 201, String.format("{\"id\":%d,\"nome\":\"%s\",\"zona\":\"%s\",\"nivelRisco\":\"%s\"}",
                        novo.getId(), escapar(novo.getNome()), escapar(novo.getZona()), novo.getNivelRisco().name()));
            } else {
                configurarCorsEResposta(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            }
        }
    }

    // HANDLER: /api/ruas
    class RuasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Rua> ruas = service.getRuaDAO().listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < ruas.size(); i++) {
                    Rua r = ruas.get(i);
                    Long idBairro = r.getBairro() != null ? r.getBairro().getId() : null;
                    String nomeBairro = r.getBairro() != null ? r.getBairro().getNome() : "";
                    json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"cep\":\"%s\",\"idBairro\":%s,\"nomeBairro\":\"%s\"}",
                            r.getId(), escapar(r.getNome()), escapar(r.getCep()), idBairro, escapar(nomeBairro)));
                    if (i < ruas.size() - 1) json.append(",");
                }
                json.append("]");
                configurarCorsEResposta(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String corpo = lerCorpoRequisicao(exchange);
                Map<String, String> dados = extrairJsonSimples(corpo);
                String nome = dados.get("nome");
                String cep = dados.get("cep");
                String idBairroStr = dados.get("idBairro");
                if (nome == null || cep == null || idBairroStr == null) {
                    configurarCorsEResposta(exchange, 400, "{\"erro\":\"Dados incompletos\"}");
                    return;
                }
                Bairro b = service.getBairroDAO().buscarPorId(Long.parseLong(idBairroStr));
                if (b == null) {
                    configurarCorsEResposta(exchange, 404, "{\"erro\":\"Bairro não existe\"}");
                    return;
                }
                Rua rua = new Rua(null, nome, cep, b);
                service.getRuaDAO().inserir(rua);
                configurarCorsEResposta(exchange, 201, String.format("{\"id\":%d,\"nome\":\"%s\",\"cep\":\"%s\"}", rua.getId(), escapar(rua.getNome()), escapar(rua.getCep())));
            } else {
                configurarCorsEResposta(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            }
        }
    }

    // HANDLER: /api/usuarios
    class UsuariosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Usuario> lista = service.getUsuarioDAO().listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    Usuario u = lista.get(i);
                    json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"contato\":\"%s\",\"tipo\":\"%s\",\"info\":\"%s\",\"alerta\":\"%s\"}",
                            u.getId(), escapar(u.getNome()), escapar(u.getContato()), u.getTipo(), escapar(u.getInfoAdicional()), escapar(u.getDescricaoAlerta())));
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                configurarCorsEResposta(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String corpo = lerCorpoRequisicao(exchange);
                Map<String, String> dados = extrairJsonSimples(corpo);
                String nome = dados.get("nome");
                String contato = dados.get("contato");
                String tipo = dados.get("tipo");
                String info = dados.get("info");
                if (nome == null || contato == null || tipo == null) {
                    configurarCorsEResposta(exchange, 400, "{\"erro\":\"Campos obrigatórios ausentes\"}");
                    return;
                }
                Usuario novo;
                if ("ENTREGADOR".equalsIgnoreCase(tipo)) novo = new Entregador(null, nome, contato, info);
                else if ("MOBILIDADE_REDUZIDA".equalsIgnoreCase(tipo)) novo = new CidadaoMobilidadeReduzida(null, nome, contato, info);
                else novo = new CidadaoMorador(null, nome, contato, info);

                service.getUsuarioDAO().inserir(novo);
                configurarCorsEResposta(exchange, 201, String.format("{\"id\":%d,\"nome\":\"%s\"}", novo.getId(), escapar(novo.getNome())));
            }
        }
    }

    // HANDLER: /api/reportes (GET, POST, DELETE)
    class ReportesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String metodo = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(metodo)) {
                List<ReporteAlagamento> reportes = service.getReporteDAO().listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < reportes.size(); i++) {
                    ReporteAlagamento r = reportes.get(i);
                    String nomeRua = r.getRua() != null ? r.getRua().getNome() : "";
                    String nomeBairro = (r.getRua() != null && r.getRua().getBairro() != null) ? r.getRua().getBairro().getNome() : "";
                    String nomeUser = r.getUsuario() != null ? r.getUsuario().getNome() : "";
                    String tipoUser = r.getUsuario() != null ? r.getUsuario().getTipo() : "";

                    json.append(String.format("{\"id\":%d,\"idRua\":%d,\"nomeRua\":\"%s\",\"nomeBairro\":\"%s\",\"idUsuario\":%d,\"nomeUsuario\":\"%s\",\"tipoUsuario\":\"%s\",\"nivel\":\"%s\",\"dataHora\":\"%s\",\"descricao\":\"%s\"}",
                            r.getId(),
                            r.getRua() != null ? r.getRua().getId() : 0,
                            escapar(nomeRua),
                            escapar(nomeBairro),
                            r.getUsuario() != null ? r.getUsuario().getId() : 0,
                            escapar(nomeUser),
                            escapar(tipoUser),
                            r.getNivel().name(),
                            r.getDataHora().toString(),
                            escapar(r.getDescricao() != null ? r.getDescricao() : "")));
                    if (i < reportes.size() - 1) json.append(",");
                }
                json.append("]");
                configurarCorsEResposta(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(metodo)) {
                try {
                    String corpo = lerCorpoRequisicao(exchange);
                    Map<String, String> dados = extrairJsonSimples(corpo);
                    Long idRua = Long.parseLong(dados.get("idRua"));
                    Long idUsuario = Long.parseLong(dados.get("idUsuario"));
                    String nivel = dados.get("nivel");
                    String desc = dados.get("descricao");

                    ReporteAlagamento r = service.registrarReporte(idRua, idUsuario, nivel, desc);
                    configurarCorsEResposta(exchange, 201, String.format("{\"id\":%d,\"mensagem\":\"Reporte registrado e risco atualizado\"}", r.getId()));
                } catch (Exception e) {
                    configurarCorsEResposta(exchange, 400, "{\"erro\":\"" + escapar(e.getMessage()) + "\"}");
                }
            } else if ("DELETE".equalsIgnoreCase(metodo)) {
                // DELETE /api/reportes?id=123 ou /api/reportes/123
                try {
                    String query = exchange.getRequestURI().getQuery();
                    Long id = null;
                    if (query != null && query.contains("id=")) {
                        id = Long.parseLong(query.split("id=")[1].split("&")[0]);
                    } else if (path.matches(".*/\\d+$")) {
                        id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                    }

                    if (id != null && service.getReporteDAO().excluir(id)) {
                        configurarCorsEResposta(exchange, 200, "{\"sucesso\":true,\"mensagem\":\"Reporte excluído com sucesso\"}");
                    } else {
                        configurarCorsEResposta(exchange, 404, "{\"erro\":\"Reporte não encontrado para exclusão\"}");
                    }
                } catch (Exception e) {
                    configurarCorsEResposta(exchange, 400, "{\"erro\":\"Parâmetro de exclusão inválido\"}");
                }
            } else {
                configurarCorsEResposta(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            }
        }
    }

    // HANDLER: /api/abrigos (GET abrigos e abrigos por bairro)
    class AbrigosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("bairroId=")) {
                Long idBairro = Long.parseLong(query.split("bairroId=")[1].split("&")[0]);
                List<AbrigoBairro> list = service.getAbrigoBairroDAO().listarAbrigosPorBairro(idBairro);
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    AbrigoBairro ab = list.get(i);
                    json.append(String.format("{\"idAbrigo\":%d,\"nomeAbrigo\":\"%s\",\"endereco\":\"%s\",\"capacidade\":%d,\"distanciaKm\":%.2f}",
                            ab.getAbrigo().getId(), escapar(ab.getAbrigo().getNome()), escapar(ab.getAbrigo().getEndereco()),
                            ab.getAbrigo().getCapacidade(), ab.getDistanciaKm()));
                    if (i < list.size() - 1) json.append(",");
                }
                json.append("]");
                configurarCorsEResposta(exchange, 200, json.toString());
            } else {
                List<Abrigo> list = service.getAbrigoDAO().listarTodos();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Abrigo a = list.get(i);
                    json.append(String.format("{\"id\":%d,\"nome\":\"%s\",\"endereco\":\"%s\",\"capacidade\":%d}",
                            a.getId(), escapar(a.getNome()), escapar(a.getEndereco()), a.getCapacidade()));
                    if (i < list.size() - 1) json.append(",");
                }
                json.append("]");
                configurarCorsEResposta(exchange, 200, json.toString());
            }
        }
    }

    // HANDLER: /api/areas-criticas
    class AreasCriticasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }

            List<AreaCritica> areas = service.getAreaCriticaDAO().listarTodos();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < areas.size(); i++) {
                AreaCritica a = areas.get(i);
                String rua = a.getRua() != null ? a.getRua().getNome() : "";
                String bairro = a.getBairro() != null ? a.getBairro().getNome() : "";
                json.append(String.format("{\"id\":%d,\"rua\":\"%s\",\"bairro\":\"%s\",\"totalReportes\":%d,\"alerta\":\"%s\",\"dataMarcacao\":\"%s\"}",
                        a.getId(), escapar(rua), escapar(bairro), a.getTotalReportes(), a.getNivelAlerta(), a.getDataMarcacao().toString()));
                if (i < areas.size() - 1) json.append(",");
            }
            json.append("]");
            configurarCorsEResposta(exchange, 200, json.toString());
        }
    }

    // HANDLER: /api/status
    class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                configurarCorsEResposta(exchange, 204, "");
                return;
            }
            String resp = "{\"status\":\"ONLINE\",\"app\":\"Rota Seca API\",\"versao\":\"1.0\",\"arquitetura\":\"Java SE puro (sem frameworks)\"}";
            configurarCorsEResposta(exchange, 200, resp);
        }
    }

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
