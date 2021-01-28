package olxclone.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import olxclone.app.R;
import olxclone.app.model.Anuncio;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselViewImagens;
    private TextView textViewTituloProduto, textViewValorProduto,
            textViewRegiaoProduto, textViewDescricaoProduto;
    private Button buttonTelefoneProduto;

    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        getSupportActionBar().setTitle("Detalhes do produto");

        inicializarComponentes();

        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
        if (anuncioSelecionado != null){

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlImagens = anuncioSelecionado.getListImagens().get(position);
                    Picasso.get().load(urlImagens).into(imageView);
                }
            };
            carouselViewImagens.setPageCount(anuncioSelecionado.getListImagens().size());
            carouselViewImagens.setImageListener(imageListener);

            textViewTituloProduto.setText(anuncioSelecionado.getTitulo());
            textViewValorProduto.setText(anuncioSelecionado.getValor());
            textViewRegiaoProduto.setText(anuncioSelecionado.getEstado());
            textViewDescricaoProduto.setText(anuncioSelecionado.getDescricao());
        }
    }

    private void inicializarComponentes(){
        carouselViewImagens = findViewById(R.id.carouselViewImagens);
        textViewTituloProduto = findViewById(R.id.textViewTituloProduto);
        textViewValorProduto = findViewById(R.id.textViewValorProduto);
        textViewRegiaoProduto = findViewById(R.id.textViewRegiaoProduto);
        textViewDescricaoProduto = findViewById(R.id.textViewDescricaoProduto);
        buttonTelefoneProduto = findViewById(R.id.buttonTelefoneProduto);
    }

    public void visualizarTelefone(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(intent);
    }
}