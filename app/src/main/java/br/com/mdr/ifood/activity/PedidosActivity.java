package br.com.mdr.ifood.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.adapter.PedidoAdapter;
import br.com.mdr.ifood.helper.ConfiguracaoFirebase;
import br.com.mdr.ifood.helper.UsuarioFirebase;
import br.com.mdr.ifood.model.Pedido;

public class PedidosActivity extends AppCompatActivity {
    private List<Pedido> pedidos = new ArrayList<>();
    private ProgressBar progressBar;
    private PedidoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniciaComponentes();
        carregaPedidos();
    }

    private void iniciaComponentes() {
        RecyclerView recyclerPedidos;
        adapter = new PedidoAdapter(pedidos);
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        recyclerPedidos.setAdapter(adapter);
        progressBar = findViewById(R.id.progress);
    }

    private void carregaPedidos() {
        progressBar.setVisibility(View.VISIBLE);
        String idUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference pedidosRef = ConfiguracaoFirebase.getFirebase()
                .child("pedidos")
                .child(idUsuario);

        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot item: dataSnapshot.getChildren())
                        pedidos.add(item.getValue(Pedido.class));

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
