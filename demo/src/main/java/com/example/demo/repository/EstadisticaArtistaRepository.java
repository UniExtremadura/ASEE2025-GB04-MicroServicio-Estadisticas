package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.EstadisticaArtistaDocument;
/**
 * Repositorio para la gestión de estadísticas de Artistas en MongoDB.
 * La clave primaria (ID) es un String, correspondiente al emailArtista.
 */
public interface EstadisticaArtistaRepository extends MongoRepository<EstadisticaArtistaDocument, String> {
    
}