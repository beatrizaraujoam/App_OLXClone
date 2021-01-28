package olxclone.app.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import olxclone.app.R;
import olxclone.app.helper.ConfiguracaoFirebase;
import olxclone.app.helper.Permissoes;
import olxclone.app.model.Anuncio;

public class CadastrarAnunciosActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private ImageView imageViewProduto1, imageViewProduto2, imageViewProduto3;
    private Spinner spinnerEstados, spinnerCategorias;
    private EditText editTextTitulo, editTextDescricao;
    private CurrencyEditText editTextValor;
    private MaskEditText editTextTelefone;
    private Button buttonCadastrarAnuncio;

    private List<String> listImagens = new ArrayList<>();
    private List<String> listURLImagens = new ArrayList<>();
    private Anuncio anuncio;
    private StorageReference storageReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncios);

        Permissoes.validarPermissoes(permissoes, this, 1);
        storageReference = ConfiguracaoFirebase.getStorageReference();

        inicializarComponetes();
        carregarDadosSpinner();

    }

    public void salvarAnuncio(){

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncios!")
                .setCancelable(false).build();
        alertDialog.show();

        for (int i = 0; i < listImagens.size(); i++){
            String urlImagem = listImagens.get(i);
            int totalImgens = listImagens.size();
            salvarImagemStorage(urlImagem, totalImgens, i);
        }

    }

    private void salvarImagemStorage(String urlImagem, final int totalImagens, int contador){

        final StorageReference storage = storageReference.child("imagens")
                .child("anuncios").child(anuncio.getIdAnuncio())
                .child("imagem " + contador);

        UploadTask uploadTask = storage.putFile(Uri.parse(urlImagem));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
                        String urlString = String.valueOf(uri);
                        listURLImagens.add(urlString);

                        if (totalImagens == listURLImagens.size()){
                            anuncio.setListImagens(listURLImagens);
                            anuncio.salvarAnuncio();
                            alertDialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer uploud da imagem");
            }
        });
    }

    private Anuncio configurarAnuncio(){
        String estado = spinnerEstados.getSelectedItem().toString();
        String categoria = spinnerCategorias.getSelectedItem().toString();
        String titulo = editTextTitulo.getText().toString();
        String valor = editTextValor.getText().toString();
        String telefone = editTextTelefone.getText().toString();
        String descricao = editTextDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }

    public void validarDadosAnuncios(View view){
        anuncio = configurarAnuncio();
        String valor = String.valueOf(editTextValor.getRawValue());

        if (listImagens.size() != 0){
            if (!anuncio.getEstado().isEmpty()){
                if (!anuncio.getCategoria().isEmpty()){
                    if (!anuncio.getTitulo().isEmpty()){
                        if (!valor.isEmpty() && !valor.equals("0")){
                            if (!anuncio.getTelefone().isEmpty() && anuncio.getTelefone().length() >= 10){
                                if (!anuncio.getDescricao().isEmpty()){

                                    salvarAnuncio();

                                } else {
                                    exibirMensagemErro("Preencha a descrição!");
                                }
                            } else {
                                exibirMensagemErro("Preencha o telefone!");
                            }
                        } else {
                            exibirMensagemErro("Preencha o valor!");
                        }
                    } else {
                        exibirMensagemErro("Preencha o título!");
                    }
                } else {
                    exibirMensagemErro("Selecione uma categoria!");
                }
            } else {
                exibirMensagemErro("Selecione um estado!");
            }
        } else {
            exibirMensagemErro("Selecione ao menos uma foto!");
        }
    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponetes(){
        imageViewProduto1 = findViewById(R.id.imageViewProduto1);
        imageViewProduto2 = findViewById(R.id.imageViewProduto2);
        imageViewProduto3 = findViewById(R.id.imageViewProduto3);
        spinnerEstados = findViewById(R.id.spinnerEstados);
        spinnerCategorias = findViewById(R.id.spinnerCategoria);
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextValor = findViewById(R.id.editTextValor);
        editTextTelefone = findViewById(R.id.editTextTelefone);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        buttonCadastrarAnuncio = findViewById(R.id.buttonCadastrarAnuncio);

        imageViewProduto1.setOnClickListener(this);
        imageViewProduto2.setOnClickListener(this);
        imageViewProduto3.setOnClickListener(this);

        Locale locale = new Locale("pt", "BR");
        editTextValor.setLocale(locale);
    }

    private void carregarDadosSpinner(){
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados
        );
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstados.setAdapter(adapterEstados);

        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias
        );
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapterCategorias);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas!");
        builder.setMessage("Para utilizar o aplicativo é necessário as pesrmissões.");
        builder.setCancelable(false);
        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageViewProduto1 :
                escolherImagem(1);
                break;
            case R.id.imageViewProduto2 :
                escolherImagem(2);
                break;
            case R.id.imageViewProduto3 :
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            if (requestCode == 1){
                imageViewProduto1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2){
                imageViewProduto2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3){
                imageViewProduto3.setImageURI(imagemSelecionada);
            }

            listImagens.add(caminhoImagem);
        }
    }
}