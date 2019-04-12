package br.com.mdr.ifood.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.mdr.ifood.R;
import br.com.mdr.ifood.model.Empresa;

/**
 * Created by Marlon D. Rocha on 09/04/2019.
 */
public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.MyViewHolder> {

    private List<Empresa> empresas;

    public EmpresaAdapter(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empresa_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        Empresa empresa = empresas.get(position);
        viewHolder.txtNome.setText(empresa.getNome());
        viewHolder.txtDescricao.setText(empresa.getDescricao());
        Picasso.get().load(empresa.getUrlImagem()).into(viewHolder.imgEmpresa);
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgEmpresa;
        private TextView txtNome, txtDescricao;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtDescricao = itemView.findViewById(R.id.txtDesc);
            imgEmpresa = itemView.findViewById(R.id.imgEmpresa);
        }
    }
}
