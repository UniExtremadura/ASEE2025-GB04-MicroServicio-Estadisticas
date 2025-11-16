package com.example.demo.model;

import java.util.List;

public class Recomendacion {
    private String tipoContenido;
    private Integer idContenido;
    private String nomContenido;
    private List<String> artistas;
    private String razon;
    private Float confianza;

    // Getters y Setters
    public String getTipoContenido() { return tipoContenido; }
    public void setTipoContenido(String tipoContenido) { this.tipoContenido = tipoContenido; }
    public Integer getIdContenido() { return idContenido; }
    public void setIdContenido(Integer idContenido) { this.idContenido = idContenido; }
    public String getNomContenido() { return nomContenido; }
    public void setNomContenido(String nomContenido) { this.nomContenido = nomContenido; }
    public List<String> getArtistas() { return artistas; }
    public void setArtistas(List<String> artistas) { this.artistas = artistas; }
    public String getRazon() { return razon; }
    public void setRazon(String razon) { this.razon = razon; }
    public Float getConfianza() { return confianza; }
    public void setConfianza(Float confianza) { this.confianza = confianza; }
}
