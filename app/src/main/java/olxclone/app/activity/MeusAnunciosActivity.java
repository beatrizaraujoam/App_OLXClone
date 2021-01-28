package olxclone.app.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import olxclone.app.R;
import olxclone.app.adapter.AdapterAnuncios;
import olxclone.app.helper.ConfiguracaoFirebase;
import olxclone.app.helper.RecyclerItemClickListener;
import olxclone.app.model.Anuncio;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAnuncios;
    private List<Anuncio> listAnuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        databaseReference = ConfiguracaoFirebase.getFirebase().child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnunciosActivity.class));
            }
        });

        recyclerViewAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listAnuncios, this);
        recyclerViewAnuncios.setAdapter(adapterAnuncios);

        recuperarAnuncios();

        recyclerViewAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerViewAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Anuncio anuncioSelecionado = listAnuncios.get(position);
                anuncioSelecionado.removerAnuncio();

                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));
    }

    public void inicializarComponentes(){
        recyclerViewAnuncios = findViewById(R.id.recyclerViewAnuncios);
    }

    public void recuperarAnuncios(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios...")
                .setCancelable(false).build();
        alertDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAnuncios.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listAnuncios.add(dataSnapshot.getValue(Anuncio.class));
                }

                Collections.reverse(listAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}