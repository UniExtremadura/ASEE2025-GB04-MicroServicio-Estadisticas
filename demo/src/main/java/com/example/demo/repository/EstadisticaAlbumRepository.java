package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.EstadisticaAlbumDocument;

/**
 * Repositorio para la gestión de estadísticas de Álbumes en MongoDB.
 * La clave primaria (ID) es un Integer, correspondiente al idAlbum.
 */
public interface EstadisticaAlbumRepository extends MongoRepository<EstadisticaAlbumDocument, Integer> {
    
}