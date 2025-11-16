package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.ValoracionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ValoracionRepository extends MongoRepository<ValoracionDocument, String> {
    
    // Este método es crucial para la lógica de la media y debe estar aquí.
    List<ValoracionDocument> findByIdSong(Integer idSong);

    // Métodos para la lógica de recomendaciones
    List<ValoracionDocument> findByEmailUser(String email);
    List<ValoracionDocument> findByValoracionGreaterThan(int valoracion);
}