package olxclone.app.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import olxclone.app.R;
import olxclone.app.model.Anuncio;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> listAnuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> listAnuncios, Context context) {
        this.listAnuncios = listAnuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncios, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = listAnuncios.get(position);

        List<String> listURLs = anuncio.getListImagens();
        String imagem = listURLs.get(0);

        Picasso.get().load(Uri.parse(imagem)).into(holder.imagem);
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());
    }

    @Override
    public int getItemCount() {
        return listAnuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imagem;
        TextView titulo;
        TextView valor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imageViewFotoAnuncio);
            titulo = itemView.findViewById(R.id.textViewTituloAnuncio);
            valor = itemView.findViewById(R.id.textViewValorAnuncio);
        }
    }
}
