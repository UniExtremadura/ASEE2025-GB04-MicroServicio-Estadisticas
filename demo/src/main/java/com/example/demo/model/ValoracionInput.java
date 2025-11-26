package com.example.demo.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ValoracionInput {
    private String emailUser;
    private Integer idSong;
    private Integer idAlbum;
    
    @Min(0)
    @Max(5)
    private Float valoracion;

    @AssertTrue(message = "La valoración debe ser un múltiplo de 0.5")
    private boolean isValoracionStepValid() {
        if (valoracion == null) {
            return true; // Let @NotNull handle this if needed
        }
        return (valoracion * 2) % 1 == 0;
    }

    @AssertTrue(message = "Debe proporcionar idSong o idAlbum, pero no ambos.")
    private boolean isOnlyOneOfSongOrAlbum() {
        return (idSong == null) != (idAlbum == null);
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

    public Integer getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(Integer idAlbum) {
        this.idAlbum = idAlbum;
    }

    public Float getValoracion() {
        return valoracion;
    }

    public void setValoracion(Float valoracion) {
        this.valoracion = valoracion;
    }
}
