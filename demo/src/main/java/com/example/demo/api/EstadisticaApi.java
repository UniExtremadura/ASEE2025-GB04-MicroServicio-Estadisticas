package com.example.demo.api;

import com.example.demo.model.EstadisticaArtista;
import com.example.demo.model.EstadisticaCancion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface EstadisticaApi {

    @GetMapping("/estadistica/artista/{email}")
    ResponseEntity<EstadisticaArtista> obtenerMeticasDesempenoArtista(@PathVariable("email") String email, @RequestParam(value = "periodo", required = false) String periodo);

    @GetMapping("/Estadistica/cancion/{id}")
    ResponseEntity<EstadisticaCancion> obtenerMeticasDetalladasCancion(@PathVariable("id") Integer id, @RequestHeader(value = "X-User-Email", required = false) String xUserEmail);
}
