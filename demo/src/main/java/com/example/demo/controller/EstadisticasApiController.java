package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.EstadisticaAlbumDocument;
import com.example.demo.model.EstadisticaCancionDocument;
import com.example.demo.model.ReproduccionDocument;
import com.example.demo.model.ValoracionDocument;
import com.example.demo.repository.ReproduccionRepository;
import com.example.demo.repository.ValoracionRepository;
import com.example.demo.service.EstadisticasUpdaterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Estadísticas de Música", description = "Gestión y consulta de métricas de reproducción, valoración y agregación de contenido.")
public class EstadisticasApiController {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private ReproduccionRepository reproduccionRepository;

    @Autowired
    private EstadisticasUpdaterService updaterService;

    // ----------------------------------------------------
    // GET /estadisticas/canciones/{id}
    // ----------------------------------------------------
    @Operation(
        summary = "Obtener estadísticas de una canción",
        description = "Devuelve la valoración media, total de valoraciones y el total de reproducciones de una canción específica por ID."
    )
    @ApiResponse(responseCode = "200", description = "Estadísticas de la canción encontradas.")
    @GetMapping("/estadisticas/canciones/{id}")
    public ResponseEntity<EstadisticaCancionDocument> obtenerEstadisticasCancion(@PathVariable("id") Integer id) {
        
        List<ValoracionDocument> valoraciones = valoracionRepository.findByIdSong(id);

        EstadisticaCancionDocument estadistica = new EstadisticaCancionDocument();
        estadistica.setIdCancion(id);

        long reproduccionesTotales = reproduccionRepository.countByIdCancion(id);
        estadistica.setReproduccionesTotales((int) reproduccionesTotales);

        if (valoraciones.isEmpty()) {
            estadistica.setValoracionMedia(0.0f);
            estadistica.setTotalValoraciones(0);
        } else {
            int totalValoraciones = valoraciones.size();
            double sumaDePuntuaciones = valoraciones.stream().mapToDouble(ValoracionDocument::getValoracion).sum();
            float mediaCalculada = (float) (sumaDePuntuaciones / totalValoraciones);

            estadistica.setValoracionMedia(mediaCalculada);
            estadistica.setTotalValoraciones(totalValoraciones);
        }

        return ResponseEntity.ok(estadistica);
    }

    // ----------------------------------------------------
    // GET /estadisticas/canciones
    // ----------------------------------------------------
    @Operation(
        summary = "Obtener listado de estadísticas de todas las canciones",
        description = "Calcula y devuelve las métricas agregadas (valoración y reproducciones) para todas las canciones que han sido valoradas."
    )
    @ApiResponse(responseCode = "200", description = "Listado de estadísticas de canciones.")
    @GetMapping("/estadisticas/canciones")
    public ResponseEntity<List<EstadisticaCancionDocument>> getEstadisticasCanciones() {
        
        List<ValoracionDocument> valoraciones = valoracionRepository.findAll();
        Map<Integer, EstadisticaCancionDocument> estadisticasMap = new HashMap<>();

        for (ValoracionDocument valoracion : valoraciones) {
            if (valoracion.getIdSong() != null) {
                estadisticasMap.computeIfAbsent(valoracion.getIdSong(), id -> {
                    EstadisticaCancionDocument estadistica = new EstadisticaCancionDocument();
                    estadistica.setIdCancion(id);
                    estadistica.setReproduccionesTotales(0);
                    return estadistica;
                });
                EstadisticaCancionDocument estadistica = estadisticasMap.get(valoracion.getIdSong());
                estadistica.setTotalValoraciones((estadistica.getTotalValoraciones() == null ? 0 : estadistica.getTotalValoraciones()) + 1);
                estadistica.setValoracionMedia((estadistica.getValoracionMedia() == null ? 0 : estadistica.getValoracionMedia()) + valoracion.getValoracion());
            }
        }

        for (EstadisticaCancionDocument estadistica : estadisticasMap.values()) {
            if (estadistica.getTotalValoraciones() > 0) {
                estadistica.setValoracionMedia(estadistica.getValoracionMedia() / estadistica.getTotalValoraciones());
            }
            long totalReproducciones = reproduccionRepository.countByIdCancion(estadistica.getIdCancion());
            estadistica.setReproduccionesTotales((int) totalReproducciones);
        }

        return ResponseEntity.ok(new ArrayList<>(estadisticasMap.values()));
    }

    // ----------------------------------------------------
    // GET /estadisticas/albumes
    // ----------------------------------------------------
    @Operation(
        summary = "Obtener listado de estadísticas de álbumes",
        description = "Calcula y devuelve las métricas agregadas (valoración y reproducciones) para todos los álbumes que han sido valorados."
    )
    @ApiResponse(responseCode = "200", description = "Listado de estadísticas de álbumes.")
    @GetMapping("/estadisticas/albumes")
    public ResponseEntity<List<EstadisticaAlbumDocument>> getEstadisticasAlbumes() {
        
        List<ValoracionDocument> valoraciones = valoracionRepository.findAll();
        Map<Integer, EstadisticaAlbumDocument> estadisticasMap = new HashMap<>();

        for (ValoracionDocument valoracion : valoraciones) {
            if (valoracion.getIdAlbum() != null) {
                estadisticasMap.computeIfAbsent(valoracion.getIdAlbum(), id -> {
                    EstadisticaAlbumDocument estadistica = new EstadisticaAlbumDocument();
                    estadistica.setIdAlbum(id);
                    // 🚩 CORRECCIÓN DEL ERROR DE TIPO (setReproduccionesTotales espera Long/long)
                    estadistica.setReproduccionesTotales(0L); 
                    return estadistica;
                });
                EstadisticaAlbumDocument estadistica = estadisticasMap.get(valoracion.getIdAlbum());
                estadistica.setTotalValoraciones((estadistica.getTotalValoraciones() == null ? 0 : estadistica.getTotalValoraciones()) + 1);
                estadistica.setValoracionMedia((estadistica.getValoracionMedia() == null ? 0 : estadistica.getValoracionMedia()) + valoracion.getValoracion());
            }
        }

        for (EstadisticaAlbumDocument estadistica : estadisticasMap.values()) {
            if (estadistica.getTotalValoraciones() > 0) {
                estadistica.setValoracionMedia(estadistica.getValoracionMedia() / estadistica.getTotalValoraciones());
            }
        }

        return ResponseEntity.ok(new ArrayList<>(estadisticasMap.values()));
    }

    // ----------------------------------------------------
    // POST /reproducciones
    // ----------------------------------------------------
    @Operation(
        summary = "Registrar una nueva reproducción",
        description = "Guarda un registro de reproducción y dispara la actualización síncrona de las estadísticas de la canción y del álbum asociado."
    )
    @ApiResponse(responseCode = "201", description = "Reproducción registrada y proceso de actualización iniciado.")
    @PostMapping("/reproducciones")
    public ResponseEntity<ReproduccionDocument> postReproduccion(@RequestBody ReproduccionDocument reproduccion) {
        
        reproduccion.setFecha(LocalDateTime.now());
        ReproduccionDocument nuevaReproduccion = reproduccionRepository.save(reproduccion);

        // Llamada al servicio que resuelve la dependencia del álbum
        updaterService.actualizarEstadisticasPostReproduccion(nuevaReproduccion.getIdCancion());
        
        return new ResponseEntity<>(nuevaReproduccion, HttpStatus.CREATED);
    }

    // ----------------------------------------------------
    // POST /estadisticas/albumes/{id}/actualizar-reproducciones
    // ----------------------------------------------------
    @Operation(
        summary = "Actualización forzada de reproducciones de álbum",
        description = "Recalcula las reproducciones totales de un álbum específico de forma manual (útil para mantenimiento o corrección de datos).",
        tags = {"Mantenimiento"}
    )
    @ApiResponse(responseCode = "200", description = "Recálculo del álbum iniciado.")
    @PostMapping("/estadisticas/albumes/{id}/actualizar-reproducciones")
    public ResponseEntity<Void> actualizarReproduccionesAlbum(@PathVariable("id") Integer id) {
        updaterService.actualizarReproduccionesTotalesAlbum(id);
        return ResponseEntity.ok().build();
    }

    // ----------------------------------------------------
    // DELETE /estadisticas/canciones/{id}
    // ----------------------------------------------------
    @Operation(
        summary = "Borrar estadísticas y actualizar álbum",
        description = "Usado cuando una canción es eliminada del sistema de Contenido. Borra las estadísticas/reproducciones locales y recalcula las métricas del álbum afectado."
    )
    @ApiResponse(responseCode = "204", description = "Estadísticas eliminadas y álbum afectado actualizado.")
    @DeleteMapping("/estadisticas/canciones/{id}")
    public ResponseEntity<Void> borrarEstadisticasCancion(@PathVariable("id") Integer id) {
        // Llama al servicio para ejecutar la lógica de borrado y actualización del álbum
        updaterService.borrarEstadisticasCancionYActualizarAlbum(id); 
        return ResponseEntity.noContent().build(); 
    }

    // ----------------------------------------------------
    // POST /estadisticas/canciones/reproducciones (Suma por lista de IDs)
    // ----------------------------------------------------
    @Operation(
        summary = "Obtener suma de reproducciones por lista de IDs",
        description = "Calcula la suma total de reproducciones de las canciones proporcionadas en la lista."
    )
    @ApiResponse(responseCode = "200", description = "Suma de reproducciones devuelta.")
    @PostMapping("/estadisticas/canciones/reproducciones")
    public ResponseEntity<Map<String, Long>> obtenerSumaReproducciones(@RequestBody List<Integer> idsCanciones) {
        long reproduccionesTotales = 0;

        for (Integer idCancion : idsCanciones) {
            reproduccionesTotales += reproduccionRepository.countByIdCancion(idCancion);
        }
        Map<String, Long> response = new HashMap<>();

        response.put("reproducciones_totales", reproduccionesTotales);
        return ResponseEntity.ok(response);
    }
}