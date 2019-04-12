package br.com.mdr.ifood.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.helper.ConfiguracaoFirebase;
import br.com.mdr.ifood.helper.UsuarioFirebase;
import br.com.mdr.ifood.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {
    private EditText edtNome, edtDescricao, edtPreco;
    private String nome, descricao, preco;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Define a toolbar customizada como ActionBar do app
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        //Mostra o botão voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        instanciaComponentes();
    }

    private void instanciaComponentes() {
        edtNome = findViewById(R.id.edtNome);
        edtDescricao = findViewById(R.id.edtDesc);
        edtPreco = findViewById(R.id.edtPreco);
        progress = findViewById(R.id.progress);
    }

    private boolean validaDados() {
        nome = edtNome.getText().toString();
        descricao = edtDescricao.getText().toString();
        preco = edtPreco.getText().toString();

        return !nome.isEmpty() && !descricao.isEmpty() && !preco.isEmpty();
    }

    public void onClick(View v) {
        try {
            progress.setVisibility(View.VISIBLE);
            if (validaDados())
                salvaProduto();
            else
                progress.setVisibility(View.GONE);
        } catch(Exception e) {
            e.printStackTrace();
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Erro ao salvar produto", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvaProduto() {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setValor(Double.parseDouble(preco));
        produto.setIdUsuario(UsuarioFirebase.getIdUsuario());
        produto.salvar();
        progress.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Produto salvo!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
