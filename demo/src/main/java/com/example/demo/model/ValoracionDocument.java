package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document; 

@Document(collection = "valoraciones")
public class ValoracionDocument {

    @Id 
    private String id;
    
    private String emailUsuario;
    private Integer idSong;
    private Integer idAlbum;
    private String emailArtista;
    private Integer valoracion;
    private String comentario;
    
    // El compilador necesita que definas estos métodos explícitamente:

    // Getters
    public String getId() { return id; }
    public String getEmailUsuario() { return emailUsuario; }
    public Integer getIdSong() { return idSong; }
    public Integer getIdAlbum() { return idAlbum; }
   public String getEmailArtista() { return emailArtista; }
    public Integer getValoracion() { return valoracion; }
    public String getComentario() { return comentario; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }
    public void setIdSong(Integer idSong) { this.idSong = idSong; }
    public void setIdAlbum(Integer idAlbum) { this.idAlbum = idAlbum; }
    public void setEmailArtista(String emailArtista) { this.emailArtista = emailArtista; }
    public void setValoracion(Integer valoracion) { this.valoracion = valoracion; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}