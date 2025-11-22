package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reproducciones")
public class ReproduccionDocument {

    @Id
    private String id;

    private Integer idCancion;
    private String emailUser;
    private LocalDateTime fecha;

    // Getters
    public String getId() {
        return id;
    }

    public Integer getIdCancion() {
        return idCancion;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setIdCancion(Integer idCancion) {
        this.idCancion = idCancion;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}