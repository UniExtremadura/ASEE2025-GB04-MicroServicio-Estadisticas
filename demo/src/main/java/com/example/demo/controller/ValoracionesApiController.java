package com.example.demo.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ValoracionDocument;
import com.example.demo.model.ValoracionInput;
import com.example.demo.repository.ValoracionRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Valoraciones", description = "API para registrar y consultar valoraciones de canciones y álbumes")
public class ValoracionesApiController {

    @Autowired
    private ValoracionRepository valoracionRepository;
    // aqui se guardan las valoraciones en la base de datos
    @PostMapping("/valoraciones")
    @Operation(summary = "Dejar valoración", description = "Permite a un usuario registrado valorar una canción o un álbum. Se debe proporcionar idSong o idAlbum, pero no ambos.")
    public ResponseEntity<ValoracionDocument> dejarValoracion(@Valid @RequestBody ValoracionInput valoracionInput) {
        
        ValoracionDocument doc = new ValoracionDocument();
        doc.setEmailUser(valoracionInput.getEmailUser());
        doc.setIdSong(valoracionInput.getIdSong());
        doc.setIdAlbum(valoracionInput.getIdAlbum());
        doc.setValoracion(valoracionInput.getValoracion());

        ValoracionDocument docGuardado = valoracionRepository.save(doc);

        return ResponseEntity.status(HttpStatus.CREATED).body(docGuardado);
    }

    @GetMapping("/valoraciones")
    @Operation(summary = "Obtener valoraciones", description = "Obtiene valoraciones por canción, álbum o usuario.")
    public ResponseEntity<List<ValoracionDocument>> getValoraciones(
            @RequestParam(required = false) Integer idSong,
            @RequestParam(required = false) Integer idAlbum,
            @RequestParam(required = false) String emailUser) {

        if (idSong != null) {
            return ResponseEntity.ok(valoracionRepository.findByIdSong(idSong));
        } else if (idAlbum != null) {
            return ResponseEntity.ok(valoracionRepository.findByIdAlbum(idAlbum));
        } else if (emailUser != null) {
            return ResponseEntity.ok(valoracionRepository.findByEmailUser(emailUser));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PutMapping("/valoraciones")
    @Operation(summary = "Actualizar valoración", description = "Actualiza una valoración existente para una canción o álbum.")
    public ResponseEntity<ValoracionDocument> actualizarValoracion(@Valid @RequestBody ValoracionInput valoracionInput) {
        Optional<ValoracionDocument> optionalDoc;
        if (valoracionInput.getIdSong() != null) {
            optionalDoc = valoracionRepository.findByEmailUserAndIdSong(valoracionInput.getEmailUser(), valoracionInput.getIdSong());
        } else {
            optionalDoc = valoracionRepository.findByEmailUserAndIdAlbum(valoracionInput.getEmailUser(), valoracionInput.getIdAlbum());
        }

        if (optionalDoc.isPresent()) {
            ValoracionDocument doc = optionalDoc.get();
            doc.setValoracion(valoracionInput.getValoracion());
            ValoracionDocument docGuardado = valoracionRepository.save(doc);
            return ResponseEntity.ok(docGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/valoraciones")
    @Operation(summary = "Eliminar valoración", description = "Elimina una valoración de un usuario para una canción o álbum.")
    public ResponseEntity<Void> deleteValoracion(@Valid @RequestBody ValoracionInput identifier) {
        Optional<ValoracionDocument> optionalDoc;
        if (identifier.getIdSong() != null) {
            optionalDoc = valoracionRepository.findByEmailUserAndIdSong(identifier.getEmailUser(), identifier.getIdSong());
        } else {
            optionalDoc = valoracionRepository.findByEmailUserAndIdAlbum(identifier.getEmailUser(), identifier.getIdAlbum());
        }

        if (optionalDoc.isPresent()) {
            valoracionRepository.delete(optionalDoc.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/canciones/{id}/valoracion-media")
    @Operation(summary = "Obtener valoración media de una canción", description = "Devuelve un JSON con la media y el total de valoraciones para una canción.")
    public ResponseEntity<Map<String, Object>> obtenerValoracionMediaCancion(@PathVariable("id") Integer id) {
        List<ValoracionDocument> valoraciones = valoracionRepository.findByIdSong(id);
        Map<String, Object> respuesta = calcularMedia(valoraciones);
        respuesta.put("idCancion", id);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/albumes/{id}/valoracion-media")
    @Operation(summary = "Obtener valoración media de un álbum", description = "Devuelve un JSON con la media y el total de valoraciones para un álbum.")
    public ResponseEntity<Map<String, Object>> obtenerValoracionMediaAlbum(@PathVariable("id") Integer id) {
        List<ValoracionDocument> valoraciones = valoracionRepository.findByIdAlbum(id);
        Map<String, Object> respuesta = calcularMedia(valoraciones);
        respuesta.put("idAlbum", id);
        return ResponseEntity.ok(respuesta);
    }

    private Map<String, Object> calcularMedia(List<ValoracionDocument> valoraciones) {
        Map<String, Object> respuesta = new HashMap<>();
        if (valoraciones.isEmpty()) {
            respuesta.put("valoracionMedia", 0.0f);
            respuesta.put("totalValoraciones", 0);
            return respuesta;
        }

        int totalValoraciones = valoraciones.size();
        double sumaDePuntuaciones = valoraciones.stream()
                                        .mapToDouble(ValoracionDocument::getValoracion)
                                        .sum();
        
        float mediaCalculada = (float) (sumaDePuntuaciones / totalValoraciones);
        float mediaRedondeada = (float) (Math.round(mediaCalculada * 2) / 2.0);

        respuesta.put("valoracionMedia", mediaRedondeada);
        respuesta.put("totalValoraciones", totalValoraciones);
        return respuesta;
    }
}