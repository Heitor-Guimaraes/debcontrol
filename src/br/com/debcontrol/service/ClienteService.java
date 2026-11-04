package br.com.debcontrol.service;

import br.com.debcontrol.dao.ClienteDAO;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.util.Validador;

import java.util.List;

public class ClienteService {
    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public Cliente salvar(Cliente cliente) {
        cliente.setNome(Validador.textoObrigatorio(cliente.getNome(), "Nome", 120));
        cliente.setCpf(Validador.somenteNumerosOuNull(cliente.getCpf()));
        cliente.setTelefone(Validador.somenteNumerosOuNull(cliente.getTelefone()));
        cliente.setEndereco(Validador.textoOpcional(cliente.getEndereco(), "Endereço", 255));
        if (cliente.getCpf() == null || cliente.getCpf().length() != 11) {
            throw new RegraNegocioException("O CPF deve conter 11 números.");
        }
        if (!Validador.cpfValido(cliente.getCpf())) {
            throw new RegraNegocioException("CPF inválido. Confira os dígitos informados.");
        }
        if (cliente.getTelefone() == null || !cliente.getTelefone().matches("[0-9]{10,13}")) {
            throw new RegraNegocioException("Informe um telefone com DDD, contendo de 10 a 13 números.");
        }
        clienteDAO.buscarPorCpf(cliente.getCpf()).ifPresent(existing -> {
            if (cliente.getIdCliente() == null || !existing.getIdCliente().equals(cliente.getIdCliente())) {
                throw new RegraNegocioException("Já existe um cliente cadastrado com este CPF.");
            }
        });
        if (cliente.getIdCliente() == null) {
            cliente.setAtivo(true);
            return clienteDAO.inserir(cliente);
        }
        clienteDAO.atualizar(cliente);
        return cliente;
    }

    public List<Cliente> listar(String filtro, boolean somenteAtivos) {
        return clienteDAO.listar(filtro, somenteAtivos);
    }

    public Cliente buscar(long id) {
        return clienteDAO.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));
    }

    public void desativar(long id) {
        Cliente cliente = buscar(id);
        if (!cliente.isAtivo()) {
            throw new RegraNegocioException("O cliente já está desativado.");
        }
        clienteDAO.desativar(id);
    }

    public void excluir(long id) {
        if (id <= 0) throw new RegraNegocioException("Selecione um cliente válido.");
        if (!clienteDAO.excluir(id)) {
            throw new RegraNegocioException("Cliente não encontrado ou com histórico de vendas. Utilize Desativar para preservar o histórico.");
        }
    }
}
