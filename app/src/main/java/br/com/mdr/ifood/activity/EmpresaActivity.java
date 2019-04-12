package br.com.mdr.ifood.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.adapter.ProdutoEmpresaAdapter;
import br.com.mdr.ifood.helper.ConfiguracaoFirebase;
import br.com.mdr.ifood.helper.UsuarioFirebase;
import br.com.mdr.ifood.listener.RecyclerItemClickListener;
import br.com.mdr.ifood.model.Empresa;
import br.com.mdr.ifood.model.Produto;

public class EmpresaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ProdutoEmpresaAdapter adapter;
    private DatabaseReference reference;
    private List<Produto> produtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        adapter = new ProdutoEmpresaAdapter(produtos);
        iniciaComponentes();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Produto produto = produtos.get(position);
                        produto.deletar();
                        Toast.makeText(getApplicationContext(), "Produto excluído!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        carregaProdutos();
    }

    private void iniciaComponentes() {
        recyclerView = findViewById(R.id.recyclerProdutos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood - empresa");
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress);
    }

    private void carregaProdutos() {
        progressBar.setVisibility(View.VISIBLE);
        String idUsuario = UsuarioFirebase.getIdUsuario();
        reference = ConfiguracaoFirebase.getFirebase().child("produtos").child(idUsuario);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    produtos.add(item.getValue(Produto.class));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_empresa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair: {
                signOut();
                break;
            }
            case R.id.menu_add_produto: {
                startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
                break;
            }
            case R.id.menu_pedidos: {
                startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
                break;
            }
            case R.id.menu_config: {
                startActivity(new Intent(EmpresaActivity.this, ConfigEmpresaActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        signOut();
        //super.onBackPressed();
    }

    private void signOut() {
        auth.signOut();
        finish();
    }
}
