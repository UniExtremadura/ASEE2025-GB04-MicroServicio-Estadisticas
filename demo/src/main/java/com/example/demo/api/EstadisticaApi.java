package com.example.demo.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.EstadisticaArtistaDocument;
import com.example.demo.model.EstadisticaCancionDocument;

public interface EstadisticaApi {

    @GetMapping("/estadistica/artista/{email}")
    ResponseEntity<EstadisticaArtistaDocument> obtenerMeticasDesempenoArtista(@PathVariable("email") String email, @RequestParam(value = "periodo", required = false) String periodo);

    @GetMapping("/Estadistica/cancion/{id}")
    ResponseEntity<EstadisticaCancionDocument> obtenerMeticasDetalladasCancion(@PathVariable("id") Integer id, @RequestHeader(value = "X-User-Email", required = false) String xUserEmail);
}
