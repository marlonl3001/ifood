package br.com.mdr.ifood.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.adapter.EmpresaAdapter;
import br.com.mdr.ifood.helper.ConfiguracaoFirebase;
import br.com.mdr.ifood.listener.RecyclerItemClickListener;
import br.com.mdr.ifood.model.Empresa;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EmpresaAdapter adapter;
    private DatabaseReference firebaseRef;
    private List<Empresa> empresas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        adapter = new EmpresaAdapter(empresas);
        iniciaComponentes();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Empresa empresa = empresas.get(position);
                        Intent i = new Intent(getApplicationContext(), CardapioActivity.class);
                        i.putExtra("empresa", empresa);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood - restaurantes");
        setSupportActionBar(toolbar);

        buscaEmpresas();
    }

    private void iniciaComponentes() {
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerRestaurantes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progress);
    }

    private void buscaEmpresas() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseRef = ConfiguracaoFirebase.getFirebase().child("empresas");
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empresas.clear();
                for (DataSnapshot item: dataSnapshot.getChildren())
                    empresas.add(item.getValue(Empresa.class));

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
        inflater.inflate(R.menu.manu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_busca);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_busca: {

                return true;
            }
            case R.id.menu_config: {
                startActivity(new Intent(MainActivity.this, ConfigUsuarioActivity.class));
                return true;
            }
            case R.id.menu_sair: {
                signOut();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }

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
