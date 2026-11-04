package br.com.debcontrol.test;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.dao.ClienteDAO;
import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.service.ClienteService;
import br.com.debcontrol.util.Validador;
import java.security.SecureRandom;

/** Teste opcional: grava e remove apenas um cliente fictício no MySQL configurado. */
public final class MySqlCrudTest {
    public static void main(String[] args) {
        ClienteDAO dao = new ClienteDAO();
        ClienteService service = new ClienteService(dao);
        ConnectionFactory.testConnection();
        System.out.println("[OK] Conexao MySQL real");
        Cliente cliente = new Cliente();
        SecureRandom random = new SecureRandom();
        String cpf;
        do {
            cpf = String.format(java.util.Locale.ROOT, "%011d", random.nextLong(100_000_000_000L));
        } while (!Validador.cpfValido(cpf) || dao.buscarPorCpf(cpf).isPresent());
        cliente.setNome("TESTE INTEGRACAO " + cpf);
        cliente.setCpf(cpf);
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Registro temporario do teste JDBC");
        Long id = null;
        try {
            cliente = service.salvar(cliente);
            id = cliente.getIdCliente();
            check(id != null, "INSERT sem chave gerada");
            System.out.println("[OK] CREATE - cliente temporario ID " + id);
            Cliente lido = service.buscar(id);
            check(cpf.equals(lido.getCpf()), "CPF persistido diferente");
            check(service.listar(cpf, false).stream().anyMatch(c -> c.getIdCliente().equals(lido.getIdCliente())),
                    "Cliente nao encontrado na consulta com filtro");
            System.out.println("[OK] READ - consulta por ID e filtro");
            lido.setNome("TESTE INTEGRACAO ALTERADO " + cpf);
            service.salvar(lido);
            check(lido.getNome().equals(service.buscar(id).getNome()), "Alteracao nao persistida");
            System.out.println("[OK] UPDATE - alteracao confirmada por nova leitura");
            service.desativar(id);
            check(!service.buscar(id).isAtivo(), "Desativacao nao persistida");
            System.out.println("[OK] Desativacao persistida");
            service.excluir(id);
            check(dao.buscarPorId(id).isEmpty(), "Exclusao nao persistida");
            id = null;
            System.out.println("[OK] DELETE - ausencia confirmada por nova leitura");
            System.out.println("Integracao JDBC e CRUD aprovados no MySQL real.");
        } finally {
            if (id != null) {
                service.excluir(id);
                System.out.println("Registro temporario removido apos falha no teste.");
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
