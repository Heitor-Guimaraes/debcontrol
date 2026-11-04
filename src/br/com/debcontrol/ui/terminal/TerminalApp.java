package br.com.debcontrol.ui.terminal;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.dao.ClienteDAO;
import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.service.ClienteService;
import br.com.debcontrol.exception.RegraNegocioException;
import java.util.Scanner;

/** Console de testes que utiliza o mesmo Service e DAO das telas Swing. */
public final class TerminalApp {
    private final Scanner input;
    private final ClienteService service;

    public TerminalApp(Scanner input, ClienteService service) {
        this.input = input;
        this.service = service;
    }

    public static void main(String[] args) {
        new TerminalApp(new Scanner(System.in), new ClienteService(new ClienteDAO())).executar();
    }

    public void executar() {
        while (true) {
            System.out.println("\n=== debcontrol | testes de conexao e CRUD ===");
            System.out.println("1 Conexao MySQL | 2 Inserir cliente | 3 Consultar clientes");
            System.out.println("4 Alterar cliente | 5 Excluir cliente | 6 Desativar | 0 Sair");
            System.out.print("Opcao: ");
            if (!input.hasNextLine()) return;
            String option = proximaLinha().trim();
            try {
                switch (option) {
                    case "0": System.out.println("Terminal encerrado."); return;
                    case "1":
                        ConnectionFactory.testConnection();
                        System.out.println("Conexao MySQL validada."); break;
                    case "2": cadastrar(); break;
                    case "3": consultar(); break;
                    case "4": alterar(); break;
                    case "5": excluir(); break;
                    case "6":
                        service.desativar(id());
                        System.out.println("Cliente desativado."); break;
                    default: System.out.println("Opcao invalida.");
                }
            } catch (RuntimeException exception) {
                System.out.println("Operacao nao concluida: " + exception.getMessage());
            }
        }
    }

    private String ler(String label) {
        System.out.print(label + ": ");
        if (!input.hasNextLine()) throw new RegraNegocioException("Entrada encerrada.");
        return proximaLinha();
    }

    // Permite registrar também as entradas em roteiros automatizados de evidência.
    private String proximaLinha() {
        String linha = input.nextLine();
        if (Boolean.getBoolean("debcontrol.echoInput")) System.out.println(linha);
        return linha;
    }

    private long id() {
        try {
            long value = Long.parseLong(ler("ID do cliente").trim());
            if (value <= 0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException exception) {
            throw new RegraNegocioException("Informe um ID inteiro positivo.");
        }
    }

    private void dados(Cliente cliente) {
        cliente.setNome(ler("Nome"));
        cliente.setCpf(ler("CPF"));
        cliente.setTelefone(ler("Telefone com DDD"));
        cliente.setEndereco(ler("Endereco (opcional)"));
    }

    private void cadastrar() {
        Cliente cliente = new Cliente();
        dados(cliente);
        cliente = service.salvar(cliente);
        System.out.println("Cliente inserido. ID: " + cliente.getIdCliente());
    }

    private void consultar() {
        var clientes = service.listar(ler("Filtro (vazio para todos)"), false);
        System.out.println("ID | Nome | CPF | Telefone | Ativo");
        for (Cliente c : clientes) {
            System.out.println(c.getIdCliente() + " | " + c.getNome() + " | " + c.getCpf()
                    + " | " + c.getTelefone() + " | " + c.isAtivo());
        }
        System.out.println("Registros: " + clientes.size());
    }

    private void alterar() {
        Cliente cliente = service.buscar(id());
        System.out.println("Alterando " + cliente.getNome() + ". Preencha todos os campos.");
        dados(cliente);
        service.salvar(cliente);
        System.out.println("Cliente alterado.");
    }

    private void excluir() {
        long id = id();
        if (!ler("Digite EXCLUIR para confirmar a exclusao definitiva").equals("EXCLUIR")) {
            System.out.println("Exclusao cancelada."); return;
        }
        service.excluir(id);
        System.out.println("Cliente excluido.");
    }
}
