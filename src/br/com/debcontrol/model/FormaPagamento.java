package br.com.debcontrol.model;

public class FormaPagamento {
    private Long idFormaPagamento;
    private String nome;
    private String descricao;
    private boolean ativo = true;

    public FormaPagamento() {
    }

    public Long getIdFormaPagamento() { return idFormaPagamento; }
    public void setIdFormaPagamento(Long idFormaPagamento) { this.idFormaPagamento = idFormaPagamento; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() { return nome; }
}
