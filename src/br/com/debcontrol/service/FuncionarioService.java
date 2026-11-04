package br.com.debcontrol.service;

import br.com.debcontrol.dao.FuncionarioDAO;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.Funcionario;
import br.com.debcontrol.util.SenhaUtil;
import br.com.debcontrol.util.Validador;

import java.util.List;

public class FuncionarioService {
    private final FuncionarioDAO dao;

    public FuncionarioService(FuncionarioDAO dao) {
        this.dao = dao;
    }

    public Funcionario salvar(Funcionario funcionario, String novaSenha) {
        funcionario.setNome(Validador.textoObrigatorio(funcionario.getNome(), "Nome", 120));
        funcionario.setCargo(Validador.textoObrigatorio(funcionario.getCargo(), "Cargo", 40));
        funcionario.setLogin(Validador.textoObrigatorio(funcionario.getLogin(), "Login", 80));
        dao.buscarPorLogin(funcionario.getLogin()).ifPresent(existing -> {
            if (funcionario.getIdFuncionario() == null
                    || !existing.getIdFuncionario().equals(funcionario.getIdFuncionario())) {
                throw new RegraNegocioException("Já existe um funcionário com este login.");
            }
        });
        if (novaSenha != null && !novaSenha.isBlank()) funcionario.setSenhaHash(SenhaUtil.hash(novaSenha));
        if (funcionario.getSenhaHash() == null) throw new RegraNegocioException("Informe uma senha.");
        if (funcionario.getIdFuncionario() == null) {
            funcionario.setAtivo(true);
            return dao.inserir(funcionario);
        }
        dao.atualizar(funcionario);
        return funcionario;
    }

    public List<Funcionario> listar(String filtro, boolean somenteAtivos) {
        return dao.listar(filtro, somenteAtivos);
    }

    public Funcionario buscar(long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
    }

    public void desativar(long id) {
        Funcionario funcionario = buscar(id);
        if (!funcionario.isAtivo()) throw new RegraNegocioException("O funcionário já está desativado.");
        dao.desativar(id);
    }

    public void excluir(long id) {
        if (id <= 0) throw new RegraNegocioException("Selecione um funcionário válido.");
        if (!dao.excluir(id)) {
            throw new RegraNegocioException("Funcionário não encontrado ou com histórico. Utilize Desativar para preservar o histórico.");
        }
    }
}
