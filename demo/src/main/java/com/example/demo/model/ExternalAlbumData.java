package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para capturar los datos de un álbum desde el microservicio externo.
 * Solo mapea los campos necesarios para las estadísticas.
 */
public class ExternalAlbumData {

    @JsonProperty("id")
    private String id;

    // Se asume que el email del artista viene en un campo llamado "artist_email".
    // Si el nombre en el JSON es diferente, se debe ajustar este @JsonProperty.
    @JsonProperty("artist_email")
    private String emailArtista;

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailArtista() {
        return emailArtista;
    }

    public void setEmailArtista(String emailArtista) {
        this.emailArtista = emailArtista;
    }
}