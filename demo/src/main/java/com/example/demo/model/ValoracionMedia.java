package com.example.demo.model;

public class ValoracionMedia {
    private Integer idCancion;
    private Float valoracionMedia;
    private Integer totalValoraciones;

    public Integer getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(Integer idCancion) {
        this.idCancion = idCancion;
    }

    public Float getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(Float valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }

    public Integer getTotalValoraciones() {
        return totalValoraciones;
    }

    public void setTotalValoraciones(Integer totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }
}
