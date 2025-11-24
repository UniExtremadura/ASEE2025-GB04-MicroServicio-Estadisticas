package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "estadisticas_album")
public class EstadisticaAlbumDocument {
    
    // Usamos el ID de la canción como ID de MongoDB.
    @Id 
    private Integer idAlbum;
    
    private Long reproduccionesTotales = 0L;
    private Float valoracionMedia = 0.0f;
    private Integer totalValoraciones = 0;
    private double ingresos;

    public Integer getIdAlbum() { return idAlbum; }
    public void setIdAlbum(Integer idAlbum) { this.idAlbum = idAlbum; }
    
    public Long getReproduccionesTotales() { return reproduccionesTotales; }
    public void setReproduccionesTotales(Long reproduccionesTotales) { this.reproduccionesTotales = reproduccionesTotales; }
    
    public Float getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Float valoracionMedia) { this.valoracionMedia = valoracionMedia; }
    
    public Integer getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(Integer totalValoraciones) { this.totalValoraciones = totalValoraciones; }

    public void setIngresos(double ingresos) { this.ingresos = ingresos; }
    public double getIngresos() { return ingresos; }
}