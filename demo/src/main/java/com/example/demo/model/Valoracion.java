package com.example.demo.model;

public class Valoracion {
    private Integer id;
    private String emailUser;
    private Integer idSong;
    private Integer valoracion;
    private String comentario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public Integer getIdSong() {
        return idSong;
    }

    public void setIdSong(Integer idSong) {
        this.idSong = idSong;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
