package br.com.mdr.ifood.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.com.mdr.ifood.helper.ConfiguracaoFirebase;

/**
 * Created by ${USER_NAME} on 10/04/2019.
 */
public class Usuario implements Serializable {
    private String idUsuario;
    private String nome;
    private String endereco;
    private String urlImagem;

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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public void salvar() {
        DatabaseReference empRef = ConfiguracaoFirebase.getFirebase().child("usuarios").child(getIdUsuario());
        empRef.setValue(this);
    }
}
