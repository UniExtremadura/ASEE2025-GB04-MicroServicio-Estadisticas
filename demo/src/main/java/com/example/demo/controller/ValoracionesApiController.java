package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Valoracion;
import com.example.demo.model.ValoracionDocument;
import com.example.demo.model.ValoracionInput;
import com.example.demo.repository.ValoracionRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
        doc.setEmailUsuario(valoracionInput.getEmailUser());
        doc.setIdSong(valoracionInput.getIdSong());
        doc.setValoracion(valoracionInput.getValoracion());
        doc.setComentario(valoracionInput.getComentario());

        ValoracionDocument docGuardado = valoracionRepository.save(doc);

        Valoracion respuesta = new Valoracion();

        try {
             // ⚠ ATENCIÓN: Esta conversión sigue siendo muy insegura si MongoDB usa ObjectId.
             respuesta.setId(Integer.parseInt(docGuardado.getId()));
        } catch (NumberFormatException e) {
             // Dejamos el ID nulo en la respuesta si la conversión falla.
        }
        
        // 🚩 CORRECCIÓN 3: Usar getEmailUsuario()
        respuesta.setEmailUser(docGuardado.getEmailUsuario());
        respuesta.setIdSong(docGuardado.getIdSong());
        respuesta.setValoracion(docGuardado.getValoracion());
        respuesta.setComentario(docGuardado.getComentario());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/canciones/{id}/valoracion-media")
@Operation(summary = "Obtener valoración media", description = "Devuelve un JSON con la media y el total")
public ResponseEntity<Map<String, Object>> obtenerValoracionMedia(@PathVariable("id") Integer id) {

    // 1. Buscamos las valoraciones
    List<ValoracionDocument> valoraciones = valoracionRepository.findByIdSong(id);

    // 2. Preparamos el "Map" que servirá como respuesta JSON
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("idCancion", id);

    // 3. Si la lista está vacía, devolvemos 0
    if (valoraciones.isEmpty()) {
        respuesta.put("valoracionMedia", 0.0f);
        respuesta.put("totalValoraciones", 0);
        return ResponseEntity.ok(respuesta);
    }

    // 4. Calculamos la media
    int totalValoraciones = valoraciones.size();
    double sumaDePuntuaciones = valoraciones.stream()
                                .mapToDouble(ValoracionDocument::getValoracion)
                                .sum();
    
    float mediaCalculada = (float) (sumaDePuntuaciones / totalValoraciones);

    // 5. Rellenamos el mapa
    respuesta.put("valoracionMedia", mediaCalculada);
    respuesta.put("totalValoraciones", totalValoraciones);

    return ResponseEntity.ok(respuesta);
}
}