package br.com.debcontrol.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private Long idPagamento;
    private Long idVenda;
    private Long idFuncionario;
    private String funcionarioNome;
    private Long idFormaPagamento;
    private String formaPagamentoNome;
    private Long idCliente;
    private String clienteNome;
    private LocalDateTime dataPagamento;
    private BigDecimal valorPago;
    private String observacao;

    public Pagamento() {
    }

    public Long getIdPagamento() { return idPagamento; }
    public void setIdPagamento(Long idPagamento) { this.idPagamento = idPagamento; }
    public Long getIdVenda() { return idVenda; }
    public void setIdVenda(Long idVenda) { this.idVenda = idVenda; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getFuncionarioNome() { return funcionarioNome; }
    public void setFuncionarioNome(String funcionarioNome) { this.funcionarioNome = funcionarioNome; }
    public Long getIdFormaPagamento() { return idFormaPagamento; }
    public void setIdFormaPagamento(Long idFormaPagamento) { this.idFormaPagamento = idFormaPagamento; }
    public String getFormaPagamentoNome() { return formaPagamentoNome; }
    public void setFormaPagamentoNome(String formaPagamentoNome) { this.formaPagamentoNome = formaPagamentoNome; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
