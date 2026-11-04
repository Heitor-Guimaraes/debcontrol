package br.com.debcontrol.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VendaFiada {
    private Long idVenda;
    private Long idCliente;
    private String clienteNome;
    private Long idFuncionario;
    private String funcionarioNome;
    private LocalDateTime dataVenda;
    private LocalDate dataVencimento;
    private String descricaoCompra;
    private BigDecimal valorTotal;
    private BigDecimal valorPago;
    private BigDecimal saldo;
    private StatusVenda status;
    private String observacao;

    public VendaFiada() {
        valorTotal = BigDecimal.ZERO;
        valorPago = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        status = StatusVenda.ABERTA;
    }

    public boolean isAtrasada() {
        return status == StatusVenda.ATRASADA || ((status == StatusVenda.ABERTA || status == StatusVenda.PARCIAL)
                && saldo.compareTo(BigDecimal.ZERO) > 0
                && dataVencimento != null
                && dataVencimento.isBefore(LocalDate.now()));
    }

    public Long getIdVenda() { return idVenda; }
    public void setIdVenda(Long idVenda) { this.idVenda = idVenda; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getFuncionarioNome() { return funcionarioNome; }
    public void setFuncionarioNome(String funcionarioNome) { this.funcionarioNome = funcionarioNome; }
    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public String getDescricaoCompra() { return descricaoCompra; }
    public void setDescricaoCompra(String descricaoCompra) { this.descricaoCompra = descricaoCompra; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public StatusVenda getStatus() { return status; }
    public void setStatus(StatusVenda status) { this.status = status; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
