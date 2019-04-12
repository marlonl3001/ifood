package br.com.mdr.ifood.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.com.mdr.ifood.helper.ConfiguracaoFirebase;

/**
 * Created by ${USER_NAME} on 09/04/2019.
 */
public class Empresa implements Serializable {
    private static final long serialVersionUID = 7166662221909567874L;
    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String tempo;
    private String categoria;
    private Double taxaEntrega;

    public void salvar() {
        DatabaseReference empRef = ConfiguracaoFirebase.getFirebase().child("empresas").child(getIdUsuario());
        empRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(Double taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public String getDescricao() {
        return getCategoria() + " - " + getTempo() + " Min R$ " + getTaxaEntrega();
    }
}
