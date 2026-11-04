package br.com.debcontrol.service;

import br.com.debcontrol.dao.FormaPagamentoDAO;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.FormaPagamento;
import br.com.debcontrol.util.Validador;

import java.util.List;

public class FormaPagamentoService {
    private final FormaPagamentoDAO dao;

    public FormaPagamentoService(FormaPagamentoDAO dao) {
        this.dao = dao;
    }

    public FormaPagamento salvar(FormaPagamento forma) {
        forma.setNome(Validador.textoObrigatorio(forma.getNome(), "Nome", 40).toUpperCase(java.util.Locale.ROOT));
        forma.setDescricao(Validador.textoOpcional(forma.getDescricao(), "Descrição", 150));
        dao.buscarPorNome(forma.getNome()).ifPresent(existente -> {
            if (forma.getIdFormaPagamento() == null
                    || !existente.getIdFormaPagamento().equals(forma.getIdFormaPagamento())) {
                throw new RegraNegocioException("Já existe uma forma de pagamento com este nome.");
            }
        });
        if (forma.getIdFormaPagamento() == null) {
            forma.setAtivo(true);
            return dao.inserir(forma);
        }
        dao.atualizar(forma);
        return forma;
    }

    public List<FormaPagamento> listar(String filtro, boolean somenteAtivas) {
        return dao.listar(filtro, somenteAtivas);
    }

    public FormaPagamento buscar(long id) {
        return dao.buscarPorId(id).orElseThrow(() -> new RegraNegocioException("Forma de pagamento não encontrada."));
    }

    public void desativar(long id) {
        FormaPagamento forma = buscar(id);
        if (!forma.isAtivo()) throw new RegraNegocioException("A forma de pagamento já está desativada.");
        dao.desativar(id);
    }

    public void excluir(long id) {
        if (id <= 0) throw new RegraNegocioException("Selecione uma forma de pagamento válida.");
        if (!dao.excluir(id)) {
            throw new RegraNegocioException("Forma de pagamento não encontrada ou já utilizada. Utilize Desativar para preservar o histórico.");
        }
    }
}
