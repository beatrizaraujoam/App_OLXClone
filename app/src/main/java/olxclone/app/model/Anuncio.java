package olxclone.app.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import olxclone.app.helper.ConfiguracaoFirebase;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private List<String> listImagens;
    private String estado;
    private String categoria;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;

    public Anuncio() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase().child("meus_anuncios");
        setIdAnuncio(databaseReference.push().getKey());
    }

    public void salvarAnuncio(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase().child("meus_anuncios");
        databaseReference.child(idUsuario).child(getIdAnuncio()).setValue(this);

        salvarAnuncioPublico();

    }

    public void salvarAnuncioPublico(){

        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase().child("anuncios");
        databaseReference.child(getEstado()).child(getCategoria()).child(getIdAnuncio()).setValue(this);

    }

    public void removerAnuncio(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child(idUsuario)
                .child(getIdAnuncio());
        databaseReference.removeValue();

        removerAnuncioPublico();
    }

    public void removerAnuncioPublico(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(getEstado())
                .child(getCategoria())
                .child(getIdAnuncio());
        databaseReference.removeValue();
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public List<String> getListImagens() {
        return listImagens;
    }

    public void setListImagens(List<String> listImagens) {
        this.listImagens = listImagens;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
