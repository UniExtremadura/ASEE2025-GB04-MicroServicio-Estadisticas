package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ReproduccionDocument;

@Repository
public interface ReproduccionRepository extends MongoRepository<ReproduccionDocument, String> {
    
    List<ReproduccionDocument> findByIdCancion(Integer idCancion);

    List<ReproduccionDocument> findByEmailUser(String emailUser);

    long countByIdCancion(Integer idCancion);

    long countByIdCancionIn(List<Integer> idCanciones);
    
    void deleteByIdCancion(Integer idCancion);
    /**
     * Obtiene una lista de todos los IDs de canciones que tienen al menos una reproducción.
     * Usamos una agregación para agrupar por idCancion (equivalente a un DISTINCT).
     */
    @Aggregation(pipeline = { "{ '$group': { '_id': '$idCancion' } }" })
    List<Integer> findAllSongIdsDistinct();

    long countByIdCancionAndFechaBetween(Integer idCancion, LocalDateTime start, LocalDateTime end);
}
