package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "estadisticas_artista")
public class EstadisticaArtistaDocument {
    
    // Usamos el ID de la canción como ID de MongoDB.
    @Id
    private String emailArtista;
    
    private Long reproduccionesTotales = 0L;
    private Float valoracionMedia = 0.0f;
    private Integer totalValoraciones = 0;

    public String getEmailArtista() { return emailArtista; }
    public void setEmailArtista(String emailArtista) { this.emailArtista = emailArtista; }
    
    public Long getReproduccionesTotales() { return reproduccionesTotales; }
    public void setReproduccionesTotales(Long reproduccionesTotales) { this.reproduccionesTotales = reproduccionesTotales; }
    
    public Float getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Float valoracionMedia) { this.valoracionMedia = valoracionMedia; }
    
    public Integer getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(Integer totalValoraciones) { this.totalValoraciones = totalValoraciones; }
}