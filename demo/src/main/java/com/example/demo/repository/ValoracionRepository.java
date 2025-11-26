package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.ValoracionDocument;

public interface ValoracionRepository extends MongoRepository<ValoracionDocument, String> {
  
    List<ValoracionDocument> findByIdSong(Integer idSong);
    
    List<ValoracionDocument> findByIdAlbum(Integer idAlbum);
    
    Optional<ValoracionDocument> findByEmailUserAndIdSong(String emailUser, Integer idSong);

    Optional<ValoracionDocument> findByEmailUserAndIdAlbum(String emailUser, Integer idAlbum);

    List<ValoracionDocument> findByEmailUser(String emailUser);
    
    List<ValoracionDocument> findByValoracionGreaterThan(int valoracion);

    Optional<ValoracionDocument> deleteByIdSong(Integer idSong);

}