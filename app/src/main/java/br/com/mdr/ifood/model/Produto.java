package br.com.mdr.ifood.model;

import com.google.firebase.database.DatabaseReference;

import br.com.mdr.ifood.helper.ConfiguracaoFirebase;

/**
 * Created by ${USER_NAME} on 09/04/2019.
 */
public class Produto {
    private String id;
    private String idUsuario;
    private String nome;
    private String descricao;
    private Double valor;

    public Produto() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("produtos");
        setId(reference.push().getKey());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void salvar() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("produtos")
                .child(getIdUsuario()).child(getId());
        reference.setValue(this);
    }

    public void deletar() {
        DatabaseReference reference = ConfiguracaoFirebase.getFirebase()
                .child("produtos")
                .child(getIdUsuario())
                .child(getId());
        reference.removeValue();
    }
}
