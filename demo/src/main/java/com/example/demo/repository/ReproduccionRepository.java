package com.example.demo.repository;

import java.util.List;

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
}
