package br.com.rotaseca.ui;

import br.com.rotaseca.dao.ConexaoBanco;
import br.com.rotaseca.model.*;
import br.com.rotaseca.service.MonitoramentoService;
import br.com.rotaseca.service.NegocioException;

import java.util.List;
import java.util.Scanner;

/**
 * Interface Console (CLI interativa) para demonstração do CRUD completo e regras de negócio.
 * Garante que os 7 pontos da rubrica de Java + JDBC sejam demonstráveis diretamente via terminal.
 */
public class MenuConsole {
    private final MonitoramentoService service;
    private final Scanner scanner;

    public MenuConsole() {
        this.service = new MonitoramentoService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   CITIZENTECH: ROTA SECA - SISTEMA DE MONITORAMENTO CLIMÁTICO");
        System.out.println("===============================================================");
        ConexaoBanco.inicializarBancoSeNecessario();
        MenuConsole app = new MenuConsole();
        app.executar();
    }

    public void executar() {
        boolean rodando = true;
        while (rodando) {
            System.out.println("\n----------------- MENU PRINCIPAL (JAVA + JDBC) -----------------");
            System.out.println("1. [BAIRROS] Listar e cadastrar bairros");
            System.out.println("2. [RUAS] Listar e cadastrar vias públicas");
            System.out.println("3. [USUÁRIOS] Listar cidadãos (Moradores, Entregadores, Mobilidade)");
            System.out.println("4. [REPORTES] Enviar novo reporte de alagamento (Gatilho de Risco)");
            System.out.println("5. [REPORTES] Listar todos os reportes e histórico");
            System.out.println("6. [ABRIGOS] Consultar abrigos próximos por bairro");
            System.out.println("7. [ÁREAS CRÍTICAS] Listar pontos de alagamento severo recorrente");
            System.out.println("8. [CRUD EXCLUIR] Remover um reporte de alagamento");
            System.out.println("9. [RECALCULAR] Recalcular riscos de todos os bairros agora");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine().trim();
            try {
                switch (opcao) {
                    case "1": gerenciarBairros(); break;
                    case "2": gerenciarRuas(); break;
                    case "3": listarUsuarios(); break;
                    case "4": cadastrarReporte(); break;
                    case "5": listarReportes(); break;
                    case "6": consultarAbrigosPorBairro(); break;
                    case "7": listarAreasCriticas(); break;
                    case "8": excluirReporte(); break;
                    case "9": recalcularTodosOsRiscos(); break;
                    case "0":
                        System.out.println("\nEncerrando Rota Seca Console. Até logo!");
                        rodando = false;
                        break;
                    default:
                        System.out.println("Opção inválida! Digite um número de 0 a 9.");
                }
            } catch (Exception e) {
                System.err.println("[AVISO DO SISTEMA] Ocorreu uma exceção tratada: " + e.getMessage());
            }
        }
    }

    private void gerenciarBairros() {
        System.out.println("\n--- BAIRROS CADASTRADOS ---");
        List<Bairro> lista = service.getBairroDAO().listarTodos();
        for (Bairro b : lista) {
            System.out.printf("ID: %d | Nome: %-15s | Zona: %-10s | Risco Atual: %s%n",
                    b.getId(), b.getNome(), b.getZona(), b.getNivelRisco().name());
        }

        System.out.print("\nDeseja cadastrar novo bairro? (s/n): ");
        if ("s".equalsIgnoreCase(scanner.nextLine().trim())) {
            System.out.print("Nome do Bairro: ");
            String nome = scanner.nextLine().trim();
            System.out.print("Zona (ex: Zona Sul): ");
            String zona = scanner.nextLine().trim();
            if (nome.isEmpty() || zona.isEmpty()) {
                System.out.println("Erro: Nome e Zona são obrigatórios.");
                return;
            }
            Bairro b = new Bairro(null, nome, zona, NivelRisco.BAIXO);
            service.getBairroDAO().inserir(b);
            System.out.println(">> Bairro cadastrado com sucesso! ID: " + b.getId());
        }
    }

    private void gerenciarRuas() {
        System.out.println("\n--- RUAS CADASTRADAS ---");
        List<Rua> ruas = service.getRuaDAO().listarTodos();
        for (Rua r : ruas) {
            System.out.printf("ID: %d | Nome: %-35s | CEP: %-10s | Bairro: %s%n",
                    r.getId(), r.getNome(), r.getCep(), (r.getBairro() != null ? r.getBairro().getNome() : "N/D"));
        }

        System.out.print("\nDeseja cadastrar nova rua? (s/n): ");
        if ("s".equalsIgnoreCase(scanner.nextLine().trim())) {
            System.out.print("Nome da Rua: ");
            String nome = scanner.nextLine().trim();
            System.out.print("CEP: ");
            String cep = scanner.nextLine().trim();
            System.out.print("ID do Bairro correspondente: ");
            try {
                Long idBairro = Long.parseLong(scanner.nextLine().trim());
                Bairro bairro = service.getBairroDAO().buscarPorId(idBairro);
                if (bairro == null) {
                    System.out.println("Erro: Bairro não encontrado.");
                    return;
                }
                Rua rua = new Rua(null, nome, cep, bairro);
                service.getRuaDAO().inserir(rua);
                System.out.println(">> Rua cadastrada com sucesso! ID: " + rua.getId());
            } catch (NumberFormatException e) {
                System.out.println("ID inválido.");
            }
        }
    }

    private void listarUsuarios() {
        System.out.println("\n--- CIDADÃOS CADASTRADOS (POO com Polimorfismo) ---");
        List<Usuario> usuarios = service.getUsuarioDAO().listarTodos();
        for (Usuario u : usuarios) {
            System.out.printf("ID: %d | Nome: %-18s | Tipo: %-20s | Contato: %s%n",
                    u.getId(), u.getNome(), u.getTipo(), u.getContato());
            System.out.println("   └─ Mensagem de Alerta Personalizada: " + u.getDescricaoAlerta());
        }
    }

    private void cadastrarReporte() {
        System.out.println("\n--- REGISTRAR NOVO REPORTE DE ALAGAMENTO EM TEMPO REAL ---");
        try {
            System.out.print("Informe o ID da Rua afetada: ");
            Long idRua = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Informe o ID do Cidadão que está reportando: ");
            Long idUsuario = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Nível do Alagamento (LEVE, MEDIO, GRAVE): ");
            String nivel = scanner.nextLine().trim();

            System.out.print("Descrição da situação observada: ");
            String desc = scanner.nextLine().trim();

            ReporteAlagamento salvo = service.registrarReporte(idRua, idUsuario, nivel, desc);
            System.out.println("\n>> REPORTE SALVO COM SUCESSO NO BANCO!");
            System.out.println(">> Rota Seca atualizou o risco e checou áreas críticas.");
            System.out.printf("   ID Reporte: %d | Data: %s | Gravidade: %s%n",
                    salvo.getId(), salvo.getDataHora(), salvo.getNivel());
        } catch (NumberFormatException e) {
            System.out.println("Erro: Entrada numérica inválida.");
        } catch (NegocioException e) {
            System.out.println("Erro de Negócio: " + e.getMessage());
        }
    }

    private void listarReportes() {
        System.out.println("\n--- HISTÓRICO DE REPORTES DE ALAGAMENTO (VIA JDBC) ---");
        List<ReporteAlagamento> reportes = service.getReporteDAO().listarTodos();
        for (ReporteAlagamento r : reportes) {
            System.out.printf("ID: %d | Data: %s | Gravidade: %-6s | Rua: %-30s | Cidadão: %s%n",
                    r.getId(),
                    r.getDataHora().toString().replace("T", " "),
                    r.getNivel().name(),
                    (r.getRua() != null ? r.getRua().getNome() : "N/D"),
                    (r.getUsuario() != null ? r.getUsuario().getNome() : "N/D"));
            System.out.println("   Obs: " + r.getDescricao());
        }
    }

    private void consultarAbrigosPorBairro() {
        System.out.println("\n--- CONSULTA DE ABRIGOS SEGUROS PRÓXIMOS (RELACIONAMENTO N:N) ---");
        System.out.print("Informe o ID do Bairro desejado: ");
        try {
            Long idBairro = Long.parseLong(scanner.nextLine().trim());
            List<AbrigoBairro> abrigos = service.listarAbrigosPorBairro(idBairro);
            if (abrigos.isEmpty()) {
                System.out.println("Nenhum abrigo cadastrado diretamente para este bairro.");
            } else {
                for (AbrigoBairro ab : abrigos) {
                    System.out.printf("Abrigo: %-35s | Endereço: %-35s | Capacidade: %d pessoas | Distância: %.2f km%n",
                            ab.getAbrigo().getNome(), ab.getAbrigo().getEndereco(), ab.getAbrigo().getCapacidade(), ab.getDistanciaKm());
                }
            }
        } catch (Exception e) {
            System.out.println("Entrada inválida ou erro na consulta: " + e.getMessage());
        }
    }

    private void listarAreasCriticas() {
        System.out.println("\n--- ÁREAS CRÍTICAS IDENTIFICADAS AUTOMATICAMENTE PELO SERVICE ---");
        List<AreaCritica> areas = service.getAreaCriticaDAO().listarTodos();
        for (AreaCritica a : areas) {
            System.out.printf("ID: %d | Rua: %-30s | Bairro: %-15s | Reportes Acumulados: %d | Alerta: %s%n",
                    a.getId(),
                    (a.getRua() != null ? a.getRua().getNome() : "N/D"),
                    (a.getBairro() != null ? a.getBairro().getNome() : "N/D"),
                    a.getTotalReportes(),
                    a.getNivelAlerta());
        }
    }

    private void excluirReporte() {
        System.out.print("\nInforme o ID do reporte de alagamento a ser excluído: ");
        try {
            Long id = Long.parseLong(scanner.nextLine().trim());
            boolean excluiu = service.getReporteDAO().excluir(id);
            if (excluiu) {
                System.out.println(">> Reporte " + id + " excluído com sucesso do banco!");
            } else {
                System.out.println("Aviso: Nenhum reporte localizado com o ID informado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private void recalcularTodosOsRiscos() {
        System.out.println("\n>> Recalculando riscos de todos os bairros via Service...");
        List<Bairro> bairros = service.getBairroDAO().listarTodos();
        for (Bairro b : bairros) {
            NivelRisco risco = service.recalcularEAtualizarRiscoBairro(b.getId());
            System.out.printf("Bairro: %-15s => Novo Risco: %s%n", b.getNome(), risco.name());
        }
        System.out.println(">> Concluído!");
    }
}
