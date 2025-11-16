package com.example.demo.controller;

// Importa las clases generadas (API y Modelos)
import com.example.demo.api.EstadisticaApi;
import com.example.demo.model.EstadisticaArtista;
import com.example.demo.model.EstadisticaCancion;
// Importa las clases de Spring
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstadisticasApiController implements EstadisticaApi {

    // (Aquí inyectarás el repositorio para buscar estadísticas)

    @Override
    public ResponseEntity<EstadisticaArtista> obtenerMeticasDesempenoArtista(String email, String periodo) {
        // TODO: Implementar la lógica para buscar estadísticas del artista
        System.out.println("Recibido GET en /estadistica/artista/" + email);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<EstadisticaCancion> obtenerMeticasDetalladasCancion(Integer id, String xUserEmail) {
        // TODO: Implementar la lógica para buscar estadísticas de la canción
        System.out.println("Recibido GET en /Estadistica/cancion/" + id);
        return ResponseEntity.ok().build();
    }
}