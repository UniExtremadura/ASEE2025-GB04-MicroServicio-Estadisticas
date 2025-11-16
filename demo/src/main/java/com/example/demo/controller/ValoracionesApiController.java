package com.example.demo.controller;

import com.example.demo.model.Valoracion;
import com.example.demo.model.ValoracionInput;
import com.example.demo.model.ValoracionMedia;
import com.example.demo.model.ValoracionDocument;
import com.example.demo.repository.ValoracionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Valoraciones", description = "API para registrar y consultar valoraciones de canciones")
public class ValoracionesApiController {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @PostMapping("/valoraciones")
    @Operation(summary = "Dejar valoración", description = "Permite a un usuario registrado valorar una canción")
    public ResponseEntity<Valoracion> dejarValoracion(@RequestBody ValoracionInput valoracionInput) {
        
        ValoracionDocument doc = new ValoracionDocument();
        doc.setEmailUser(valoracionInput.getEmailUser());
        doc.setIdSong(valoracionInput.getIdSong());
        doc.setValoracion(valoracionInput.getValoracion());

        ValoracionDocument docGuardado = valoracionRepository.save(doc);

        Valoracion respuesta = new Valoracion();
        // Asumiendo que el ID de mongo es numérico o se puede convertir. ¡Cuidado en producción!
        try {
            respuesta.setId(Integer.parseInt(docGuardado.getId()));
        } catch (NumberFormatException e) {
            // Si el ID no es un entero, no lo establecemos o usamos otro campo.
            // Para este ejemplo, lo dejamos nulo si falla.
        }
        respuesta.setEmailUser(docGuardado.getEmailUser());
        respuesta.setIdSong(docGuardado.getIdSong());
        respuesta.setValoracion(docGuardado.getValoracion());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/canciones/{id}/valoracion-media")
    @Operation(summary = "Obtener valoración media de una canción", description = "Cualquier usuario puede ver la valoración media y el número total de valoraciones")
    public ResponseEntity<ValoracionMedia> obtenerValoracionMedia(@PathVariable("id") Integer id) {
        
        List<ValoracionDocument> valoraciones = valoracionRepository.findByIdSong(id);

        if (valoraciones.isEmpty()) {
            ValoracionMedia media = new ValoracionMedia();
            media.setIdCancion(id);
            media.setValoracionMedia(0.0f);
            media.setTotalValoraciones(0);
            return ResponseEntity.ok(media);
        }

        int totalValoraciones = valoraciones.size();
        double sumaDePuntuaciones = valoraciones.stream().mapToDouble(ValoracionDocument::getValoracion).sum();
        float mediaCalculada = (float) (sumaDePuntuaciones / totalValoraciones);

        ValoracionMedia respuesta = new ValoracionMedia();
        respuesta.setIdCancion(id);
        respuesta.setTotalValoraciones(totalValoraciones);
        respuesta.setValoracionMedia(mediaCalculada);

        return ResponseEntity.ok(respuesta);
    }
}