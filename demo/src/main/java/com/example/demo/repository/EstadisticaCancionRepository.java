package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import  com.example.demo.model.EstadisticaCancionDocument;

/**
 * Repositorio para la gestión de estadísticas de Canciones en MongoDB.
 * La clave primaria (ID) es un Integer, correspondiente al idCancion.
 */
public interface EstadisticaCancionRepository extends MongoRepository<EstadisticaCancionDocument, Integer> {
    
}