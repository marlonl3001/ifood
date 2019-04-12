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
import br.com.mdr.ifood.model.ItemPedido;
import br.com.mdr.ifood.model.Pedido;

/**
 * Created by Marlon D. Rocha on 12/04/2019.
 */
public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.MyViewHolder> {
    private List<Pedido> pedidos;

    public PedidoAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_item, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Pedido pedido = pedidos.get(position);
        myViewHolder.txtCliente.setText(pedido.getNome());
        String desc = "Endereço: " + pedido.getEndereco();
        if (!pedido.getObservacao().isEmpty())
            desc = desc + "\nObs: " + pedido.getObservacao();
        myViewHolder.txtEndereco.setText(desc);
        DecimalFormat format = new DecimalFormat("0.00");
        String descItens = "";
        double totalPedido = 0.0;
        for (ItemPedido itemPedido: pedido.getItens()) {
            descItens += String.valueOf(position + 1) + ") " + itemPedido.getNomeProduto() + "/ (" +
                    String.valueOf(itemPedido.getQuantidade()) + " x R$ " +
                    format.format(itemPedido.getPreco()) + ")\n";
            totalPedido = itemPedido.getQuantidade() * itemPedido.getPreco();
        }
        descItens = descItens + "Total: R$ " + format.format(totalPedido);
        myViewHolder.txtItens.setText(descItens);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtCliente, txtEndereco, txtItens;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtEndereco = itemView.findViewById(R.id.txtEndereco);
            txtItens = itemView.findViewById(R.id.txtItens);
        }
    }
}
