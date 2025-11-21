package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "estadisticas_canciones")
@TypeAlias(value="")
public class EstadisticaCancionDocument {
    
    // Usamos el ID de la canción como ID de MongoDB.
    @Id 
    private Integer idCancion;
    
    private Integer reproduccionesTotales = 0;
    private Float valoracionMedia = 0.0f;
    private Integer totalValoraciones = 0;

    public Integer getIdCancion() { return idCancion; }
    public void setIdCancion(Integer idCancion) { this.idCancion = idCancion; }
    
    public Integer getReproduccionesTotales() { return reproduccionesTotales; }
    public void setReproduccionesTotales(Integer reproduccionesTotales) { this.reproduccionesTotales = reproduccionesTotales; }
    
    public Float getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Float valoracionMedia) { this.valoracionMedia = valoracionMedia; }
    
    public Integer getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(Integer totalValoraciones) { this.totalValoraciones = totalValoraciones; }
}