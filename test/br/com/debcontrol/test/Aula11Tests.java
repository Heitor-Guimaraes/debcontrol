package br.com.debcontrol.test;

import br.com.debcontrol.dao.ClienteDAO;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.service.ClienteService;
import java.util.*;

/** Testa a integração Service/DAO com armazenamento em memória, não SQL real. */
public final class Aula11Tests {
    private Aula11Tests() { }

    public static void crudService() {
        ClienteDAO dao = new ClienteDAO() {
            private final Map<Long, Cliente> records = new HashMap<>();
            public Cliente inserir(Cliente c) { c.setIdCliente(1L); records.put(1L, c); return c; }
            public Optional<Cliente> buscarPorId(long id) { return Optional.ofNullable(records.get(id)); }
            public Optional<Cliente> buscarPorCpf(String cpf) {
                return records.values().stream().filter(c -> c.getCpf().equals(cpf)).findFirst();
            }
            public void atualizar(Cliente c) { records.put(c.getIdCliente(), c); }
            public boolean excluir(long id) { return records.remove(id) != null; }
        };
        ClienteService service = new ClienteService(dao);
        Cliente c = new Cliente();
        c.setNome("Cliente de teste");
        c.setCpf("52998224725");
        c.setTelefone("(11) 99999-9999");
        c = service.salvar(c);
        check(service.buscar(1L).getNome().equals("Cliente de teste"));
        Cliente duplicate = new Cliente();
        duplicate.setNome("Outro"); duplicate.setCpf(c.getCpf()); duplicate.setTelefone("11999999999");
        rejeita(() -> service.salvar(duplicate));
        c.setNome("Nome alterado"); service.salvar(c);
        check(service.buscar(1L).getNome().equals("Nome alterado"));
        service.excluir(1L);
        rejeita(() -> service.buscar(1L));
        rejeita(() -> service.excluir(0));
    }

    public static void exclusaoProtegida() {
        ClienteService service = new ClienteService(new ClienteDAO() {
            public boolean excluir(long id) { return false; }
        });
        rejeita(() -> service.excluir(1));
    }

    public static void telefoneInvalido() {
        Cliente c = new Cliente();
        c.setNome("Teste"); c.setCpf("52998224725"); c.setTelefone("abc");
        rejeita(() -> new ClienteService(new ClienteDAO()).salvar(c));
    }

    private static void rejeita(Runnable action) {
        try { action.run(); }
        catch (RegraNegocioException expected) { return; }
        throw new AssertionError("Era esperado bloqueio por regra de negócio.");
    }

    private static void check(boolean value) {
        if (!value) throw new AssertionError("Resultado inesperado no CRUD da camada service.");
    }
}
