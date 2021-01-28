package olxclone.app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import olxclone.app.R;
import olxclone.app.adapter.AdapterAnuncios;
import olxclone.app.helper.ConfiguracaoFirebase;
import olxclone.app.helper.RecyclerItemClickListener;
import olxclone.app.model.Anuncio;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autinticacao;

    private RecyclerView recyclerViewAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;

    private List<Anuncio> listAnuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference referenceAnuncios;
    private DatabaseReference referenceAnunciosPublicos;
    private AlertDialog alertDialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        autinticacao = ConfiguracaoFirebase.getFirebaseAuth();
        referenceAnuncios = ConfiguracaoFirebase.getFirebase().child("anuncios");

        inicializarComponentes();

        recyclerViewAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listAnuncios, this);
        recyclerViewAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();

        recyclerViewAnunciosPublicos.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewAnunciosPublicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Anuncio anuncioSelecionado = listAnuncios.get(position);

                Intent intent = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                intent.putExtra("anuncioSelecionado", anuncioSelecionado);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (autinticacao.getCurrentUser() == null){
            menu.setGroupVisible(R.id.groupDeslogado, true);
        } else {
            menu.setGroupVisible(R.id.groupLogado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_cadastrar :
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                break;
            case R.id.menu_sair :
                autinticacao.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios :
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){

        recyclerViewAnunciosPublicos = findViewById(R.id.recyclerViewAnunciosPublicos);
        buttonRegiao = findViewById(R.id.buttonRegiao);
        buttonCategoria = findViewById(R.id.buttonCategoria);

    }

    public void recuperarAnunciosPublicos(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios...")
                .setCancelable(false).build();
        alertDialog.show();

        listAnuncios.clear();
        referenceAnuncios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotEstados : snapshot.getChildren()){
                    for (DataSnapshot snapshotCategorias : snapshotEstados.getChildren()){
                        for (DataSnapshot snapshotAnuncios : snapshotCategorias.getChildren()){
                            Anuncio anuncio = snapshotAnuncios.getValue(Anuncio.class);
                            listAnuncios.add(anuncio);
                        }
                    }
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

    public void filtrarPorEstados(View view){

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado desejado");

        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinnerEstados = viewSpinner.findViewById(R.id.spinnerFiltro);

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados
        );
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstados.setAdapter(adapterEstados);

        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroEstado = spinnerEstados.getSelectedItem().toString();
                recuperarAnunciosPorEstados();
                filtrandoPorEstado = true;
            }
        });
        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogEstado.create();
        dialog.show();
    }

    public void recuperarAnunciosPorEstados(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios...")
                .setCancelable(false).build();
        alertDialog.show();

        referenceAnunciosPublicos = ConfiguracaoFirebase.getFirebase().child("anuncios").child(filtroEstado);
        referenceAnunciosPublicos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAnuncios.clear();

                for (DataSnapshot snapshotCategorias : snapshot.getChildren()){
                    for (DataSnapshot snapshotAnuncios : snapshotCategorias.getChildren()){
                        Anuncio anuncio = snapshotAnuncios.getValue(Anuncio.class);
                        listAnuncios.add(anuncio);
                    }
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

    public void filtrarPorCategorias(View view){

        if (filtrandoPorEstado == true){

            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
            dialogCategoria.setTitle("Selecione a categoria desejada");

            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            final Spinner spinnerCategorias = viewSpinner.findViewById(R.id.spinnerFiltro);

            String[] categorias = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categorias
            );
            adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategorias.setAdapter(adapterCategorias);

            dialogCategoria.setView(viewSpinner);

            dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filtroCategoria = spinnerCategorias.getSelectedItem().toString();
                    recuperarAnunciosPorCategorias();
                }
            });
            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = dialogCategoria.create();
            dialog.show();

        } else {
            Toast.makeText(this, "Escolha primeiro uma região!", Toast.LENGTH_SHORT).show();
        }
    }

    public void recuperarAnunciosPorCategorias(){
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando anúncios...")
                .setCancelable(false).build();
        alertDialog.show();

        referenceAnunciosPublicos = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroEstado)
                .child(filtroCategoria);
        referenceAnunciosPublicos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAnuncios.clear();

                for (DataSnapshot snapshotAnuncios : snapshot.getChildren()){
                    Anuncio anuncio = snapshotAnuncios.getValue(Anuncio.class);
                    listAnuncios.add(anuncio);
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