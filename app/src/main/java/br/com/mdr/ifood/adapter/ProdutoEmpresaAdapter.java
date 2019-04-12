package br.com.mdr.ifood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.model.Produto;

/**
 * Created by Marlon D. Rocha on 09/04/2019.
 */
public class ProdutoEmpresaAdapter extends RecyclerView.Adapter<ProdutoEmpresaAdapter.MyViewHolder> {

    private List<Produto> produtos;

    public ProdutoEmpresaAdapter(List<Produto> produtos) {
        this.produtos = produtos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.produto_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        DecimalFormat format = new DecimalFormat("0.00");
        Produto produto = produtos.get(position);
        viewHolder.txtNome.setText(produto.getNome());
        viewHolder.txtDescricao.setText(produto.getDescricao());
        viewHolder.txtPreco.setText("R$ " + format.format(produto.getValor()));
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNome, txtDescricao, txtPreco;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeProduto);
            txtDescricao = itemView.findViewById(R.id.txtDescProduto);
            txtPreco = itemView.findViewById(R.id.txtValorProduto);
        }
    }
}
