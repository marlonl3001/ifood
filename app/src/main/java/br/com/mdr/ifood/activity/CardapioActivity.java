package br.com.mdr.ifood.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.adapter.ProdutoEmpresaAdapter;
import br.com.mdr.ifood.helper.ConfiguracaoFirebase;
import br.com.mdr.ifood.helper.UsuarioFirebase;
import br.com.mdr.ifood.listener.RecyclerItemClickListener;
import br.com.mdr.ifood.model.Empresa;
import br.com.mdr.ifood.model.ItemPedido;
import br.com.mdr.ifood.model.Pedido;
import br.com.mdr.ifood.model.Produto;
import br.com.mdr.ifood.model.Usuario;
import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {
    private ImageView imgEmpresa;
    private TextView txtEmpresa, txtDescricao, txtQtdTotal, txtVlrTotal;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProdutoEmpresaAdapter adapter;
    private DatabaseReference reference;
    private List<Produto> produtos = new ArrayList<>();
    private Empresa empresa = null;
    private Usuario usuario;
    private AlertDialog dialog;
    private int qtdItens = 0;
    private Double vlrTotal = 0.00;
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private Pedido pedidoAtual;

    private enum StatusPedido {
        PENDENTE("pendente"),
        FINALIZADO("finalizado");

        private String descricao;

        StatusPedido(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);
        adapter = new ProdutoEmpresaAdapter(produtos);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            empresa = (Empresa) bundle.getSerializable("empresa");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        //Mostra o botão voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniciaComponentes();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        confirmarQtd(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        carregaProdutos();
        carregaDadosUsuario();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_confirmar_pedido: {
                if (itensCarrinho.size() > 0)
                    confirmaPedido();
                else
                    mostraMensagem("Carrinho está vazio.");

                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }

    }

    private Integer metodoPagamento = 0;

    private void confirmaPedido() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecione um método de pagamento");
            //Adiciona uma RadioButton ao dialog
            String[] itens = new String[]{"Dinheiro", "Máquina cartão"};

            builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    metodoPagamento = position;
                }
            });
            final EditText edtObs = new EditText(this);
            edtObs.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edtObs.setHint("Digite uma observação.");
            builder.setView(edtObs);
            builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pedidoAtual.setStatus(StatusPedido.FINALIZADO.getDescricao());
                    pedidoAtual.setMetodoPagamento(metodoPagamento);
                    pedidoAtual.setObservacao(edtObs.getText().toString());
                    pedidoAtual.confirmar();
                    mostraMensagem("Seu pedido foi confirmado com sucesso!");
                    finish();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        } catch(Exception e) {
            e.printStackTrace();
            mostraMensagem("Erro ao finalizar o pedido! " + e.getLocalizedMessage());
        }
    }

    private void confirmarQtd(Integer position) {
        final Produto produto = produtos.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(produto.getNome());
        builder.setMessage("Digite a quantidade");
        final EditText edtQtd = new EditText(this);
        edtQtd.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtQtd.setText("1");
        builder.setView(edtQtd);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer qtdSelecionada = Integer.parseInt(edtQtd.getText().toString());
                qtdItens += qtdSelecionada;
                vlrTotal += (qtdSelecionada * produto.getValor());
                txtQtdTotal.setText("Quantidade: " + String.valueOf(qtdItens));
                DecimalFormat format = new DecimalFormat("0.00");
                txtVlrTotal.setText("R$ " + format.format(vlrTotal));

                ItemPedido item = new ItemPedido();
                item.setIdProduto(produto.getId());
                item.setNomeProduto(produto.getNome());
                item.setQuantidade(qtdSelecionada);
                item.setPreco(produto.getValor());
                itensCarrinho.add(item);

                String idEmpresa = produto.getIdUsuario();
                if (pedidoAtual == null) {
                    pedidoAtual = new Pedido(usuario.getIdUsuario(), idEmpresa, StatusPedido.PENDENTE.getDescricao());
                }

                pedidoAtual.setNome(usuario.getNome());
                pedidoAtual.setEndereco(usuario.getEndereco());
                pedidoAtual.setItens(itensCarrinho);
                pedidoAtual.salvar();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void iniciaComponentes() {
        imgEmpresa = findViewById(R.id.imgEmpresa);
        txtEmpresa = findViewById(R.id.txtEmpresa);
        txtEmpresa.setText(empresa.getNome());
        txtDescricao = findViewById(R.id.txtDesc);
        txtDescricao.setText(empresa.getDescricao());
        txtQtdTotal = findViewById(R.id.txtQtdTotal);
        txtVlrTotal = findViewById(R.id.txtVlrTotal);
        Picasso.get().load(empresa.getUrlImagem()).into(imgEmpresa);
        recyclerView = findViewById(R.id.recyclerCardapio);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progress);
    }

    private void carregaDadosUsuario() {
        dialog = new SpotsDialog
                .Builder()
                .setContext(this)
                .setMessage("Carregamendo")
                .setCancelable(false)
                .build();
        dialog.show();

        String idUsuario = UsuarioFirebase.getIdUsuario();

        DatabaseReference usuRef = ConfiguracaoFirebase.getFirebase().child("usuarios").child(idUsuario);
        usuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    usuario = dataSnapshot.getValue(Usuario.class);

                recuperaPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperaPedido() {
        final DatabaseReference pedidoRef = ConfiguracaoFirebase.getFirebase()
                .child("pedidos_usuario")
                .child(empresa.getIdUsuario())
                .child(usuario.getIdUsuario());
        pedidoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pedidoAtual = dataSnapshot.getValue(Pedido.class);
                    String statusPedido = StatusPedido.FINALIZADO.getDescricao();
                    String status = pedidoAtual.getStatus();
                    if (status.equals(statusPedido))
                        pedidoAtual = null;
                    else {
                        itensCarrinho = pedidoAtual.getItens();
                        for (ItemPedido item: itensCarrinho) {
                            qtdItens += item.getQuantidade();
                            vlrTotal += (item.getPreco() * item.getQuantidade());
                        }
                    }
                    DecimalFormat format = new DecimalFormat("0.00");
                    txtQtdTotal.setText("Quantidade: " + qtdItens);
                    txtVlrTotal.setText("R$ " + format.format(vlrTotal));
                }

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    private void carregaProdutos() {
        progressBar.setVisibility(View.VISIBLE);
        String idUsuario = empresa.getIdUsuario();
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

    private void mostraMensagem(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
