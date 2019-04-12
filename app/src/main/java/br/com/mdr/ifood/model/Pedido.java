package br.com.mdr.ifood.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.com.mdr.ifood.helper.ConfiguracaoFirebase;

/**
 * Created by Marlon D. Rocha on 11/04/2019.
 */
public class Pedido {
    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String endereco;
    private List<ItemPedido> itens;
    private Double total;
    private String status;
    private int metodoPagamento;
    private String observacao;

    public Pedido() {

    }

    public Pedido(String idUsuario, String idEmpresa, String status) {
        this.idUsuario = idUsuario;
        this.idEmpresa = idEmpresa;
        this.status = status;

        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("pedidos_usuario")
                .child(idEmpresa)
                .child(idUsuario);
        setIdPedido(reference.push().getKey());
    }

    public void salvar() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("pedidos_usuario")
                .child(idEmpresa)
                .child(idUsuario);
        reference.setValue(this);
    }

    public void confirmar() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("pedidos")
                .child(idEmpresa)
                .child(idUsuario);
        reference.setValue(this);
        remover();
    }

    private void remover() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("pedidos_usuario")
                .child(idEmpresa)
                .child(idUsuario);
        reference.removeValue();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
