package br.com.debcontrol.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cobranca {
    private Long idCobranca;
    private Long idVenda;
    private Long idFuncionario;
    private String clienteNome;
    private String funcionarioNome;
    private LocalDateTime dataCobranca;
    private MeioContato meioContato;
    private String resultado;
    private LocalDate proximoContato;
    private String observacao;

    public Cobranca() {
    }

    public Long getIdCobranca() { return idCobranca; }
    public void setIdCobranca(Long idCobranca) { this.idCobranca = idCobranca; }
    public Long getIdVenda() { return idVenda; }
    public void setIdVenda(Long idVenda) { this.idVenda = idVenda; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getFuncionarioNome() { return funcionarioNome; }
    public void setFuncionarioNome(String funcionarioNome) { this.funcionarioNome = funcionarioNome; }
    public LocalDateTime getDataCobranca() { return dataCobranca; }
    public void setDataCobranca(LocalDateTime dataCobranca) { this.dataCobranca = dataCobranca; }
    public MeioContato getMeioContato() { return meioContato; }
    public void setMeioContato(MeioContato meioContato) { this.meioContato = meioContato; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public LocalDate getProximoContato() { return proximoContato; }
    public void setProximoContato(LocalDate proximoContato) { this.proximoContato = proximoContato; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
