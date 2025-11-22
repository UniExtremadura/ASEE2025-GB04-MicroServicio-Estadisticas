package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.ValoracionDocument;

public interface ValoracionRepository extends MongoRepository<ValoracionDocument, String> {
  
    List<ValoracionDocument> findByIdSong(Integer idSong);
    
   
    List<ValoracionDocument> findByIdAlbum(Integer idAlbum);
    

    List<ValoracionDocument> findByEmailArtista(String emailArtista);

    List<ValoracionDocument> findByEmailUsuario(String emailUsuario);
    
    List<ValoracionDocument> findByValoracionGreaterThan(int valoracion);

    void deleteByIdSong(Integer idSong);
}