package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "estadisticas_album")
public class EstadisticaAlbumDocument {
    
    @Id 
    private Integer idAlbum;

    private Long reproduccionesTotales = 0L;
    private Float valoracionMedia = 0.0f;
    private Integer totalValoraciones = 0;

    // 👉 Constructor vacío (NECESARIO)
    public EstadisticaAlbumDocument() {}

    // 👉 Constructor usado por tu controlador
    public EstadisticaAlbumDocument(Integer idAlbum, Float valoracionMedia, Integer totalValoraciones, Long reproduccionesTotales) {
        this.idAlbum = idAlbum;
        this.valoracionMedia = valoracionMedia;
        this.totalValoraciones = totalValoraciones;
        this.reproduccionesTotales = reproduccionesTotales;
    }

    public Integer getIdAlbum() { return idAlbum; }
    public void setIdAlbum(Integer idAlbum) { this.idAlbum = idAlbum; }
    
    public Long getReproduccionesTotales() { return reproduccionesTotales; }
    public void setReproduccionesTotales(Long reproduccionesTotales) { this.reproduccionesTotales = reproduccionesTotales; }
    
    public Float getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Float valoracionMedia) { this.valoracionMedia = valoracionMedia; }
    
    public Integer getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(Integer totalValoraciones) { this.totalValoraciones = totalValoraciones; }
}
