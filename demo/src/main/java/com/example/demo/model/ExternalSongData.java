package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalSongData {
    

    @JsonProperty("id")
    private Integer id; 

    private double price;
    private int purchases;
    
    @JsonProperty("idAlbum")
    private Integer idAlbum;

    public ExternalSongData(Integer id, double price, int purchases, Integer idAlbum) {
        this.id = id;
        this.price = price;
        this.purchases = purchases;
        this.idAlbum = idAlbum;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(Integer idAlbum) {
        this.idAlbum = idAlbum;
    }
    
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPurchases() {
        return purchases;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
    }
}