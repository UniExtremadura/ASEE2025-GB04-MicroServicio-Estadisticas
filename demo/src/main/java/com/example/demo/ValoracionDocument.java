package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document; 

@Document(collection = "valoraciones")
public class ValoracionDocument {

    @Id 
    private String id;
    
    private String emailUser;
    private Integer idSong;
    private Integer idAlbum;
    private Integer idArtist;
    private Integer valoracion;
    private String comentarios;
    
    // El compilador necesita que definas estos métodos explícitamente:

    // Getters
    public String getId() { return id; }
    public String getEmailUser() { return emailUser; }
    public Integer getIdSong() { return idSong; }
    public Integer getIdAlbum() { return idAlbum; }
    public Integer getIdArtist() { return idArtist; }
    public Integer getValoracion() { return valoracion; }
    public String getComentarios() { return comentarios; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmailUser(String emailUser) { this.emailUser = emailUser; }
    public void setIdSong(Integer idSong) { this.idSong = idSong; }
    public void setIdAlbum(Integer idAlbum) { this.idAlbum = idAlbum; }
    public void setIdArtist(Integer idArtist) { this.idArtist = idArtist; }
    public void setValoracion(Integer valoracion) { this.valoracion = valoracion; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
}