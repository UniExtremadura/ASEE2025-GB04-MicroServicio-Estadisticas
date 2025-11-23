package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ReproduccionDocument;

@Repository
public interface ReproduccionRepository extends MongoRepository<ReproduccionDocument, String> {
    
    List<ReproduccionDocument> findByIdCancion(Integer idCancion);

    List<ReproduccionDocument> findByEmailUser(String emailUser);

    Long countByIdCancion(Integer idCancion);

    Long countByIdCancionIn(List<Long> idCanciones);
    
    void deleteByIdCancion(Integer idCancion);
    @Query(value = "{ 'idCancion': ?0, 'fecha': { $gte: ?1, $lte: ?2 } }", count = true)
    long countByIdCancionAndFechaBetween(Integer idCancion, LocalDateTime start, LocalDateTime end);
    @Query(value = "{ 'idCancion': ?0, 'fecha': { $gte: ?1, $lte: ?2 } }", count = true)
    Integer sumByAlbumIdAndFechaBetween(int albumId, LocalDate inicio, LocalDate fin);}
