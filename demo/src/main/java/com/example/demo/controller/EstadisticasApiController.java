package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.EstadisticaAlbumDocument;
import com.example.demo.model.EstadisticaCancionDocument;
import com.example.demo.model.ReproduccionDocument;
import com.example.demo.model.ValoracionDocument;
import com.example.demo.repository.ReproduccionRepository;
import com.example.demo.repository.ValoracionRepository;
import com.example.demo.service.ContenidoService;
import com.example.demo.service.EstadisticasUpdaterService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EstadisticasApiController {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private ReproduccionRepository reproduccionRepository;

    @Autowired
    private EstadisticasUpdaterService updaterService;

    @Autowired
    private ContenidoService contenidoService;


    // ----------------------------------------------------
    // GET /estadisticas/canciones/{id}
    // ----------------------------------------------------
    @GetMapping("/estadisticas/canciones/{id}")
    public ResponseEntity<EstadisticaCancionDocument> obtenerEstadisticasCancion(@PathVariable("id") Integer id) {

        List<ValoracionDocument> valoraciones = valoracionRepository.findByIdSong(id);

        EstadisticaCancionDocument estadistica = new EstadisticaCancionDocument();
        estadistica.setIdCancion(id);

        // Reproducciones — SIEMPRE número correcto
        estadistica.setReproduccionesTotales(reproduccionRepository.countByIdCancion(id));

        // Valoraciones
        if (valoraciones.isEmpty()) {
            estadistica.setValoracionMedia(0.0f);
            estadistica.setTotalValoraciones(0);
        } else {
            int total = valoraciones.size();
            double suma = valoraciones.stream().mapToDouble(ValoracionDocument::getValoracion).sum();
            estadistica.setTotalValoraciones(total);
            estadistica.setValoracionMedia((float) (suma / total));
        }

        return ResponseEntity.ok(estadistica);
    }


    // ----------------------------------------------------
    // GET /estadisticas/canciones
    // ----------------------------------------------------
   @GetMapping("/estadisticas/canciones")
public ResponseEntity<List<EstadisticaCancionDocument>> getEstadisticasCanciones(
        @RequestParam(required = false) String fechaInicio,
        @RequestParam(required = false) String fechaFin
) {
    // 1. Configuración de Fechas
    LocalDateTime inicio = null;
    LocalDateTime fin = null;
    boolean filtrarPorFecha = (fechaInicio != null && !fechaInicio.isEmpty() && 
                               fechaFin != null && !fechaFin.isEmpty());

    if (filtrarPorFecha) {
        try {
            // Convertimos "2023-11-01" -> 2023-11-01T00:00:00
            inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            // Convertimos "2023-11-30" -> 2023-11-30T23:59:59.9999
            fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);
        } catch (Exception e) {
            System.err.println("Error parseando fechas: " + e.getMessage());
            // Si fallan las fechas, desactivamos el filtro para evitar errores 500
            filtrarPorFecha = false; 
        }
    }

    // 2. Obtener IDs de canciones (Lógica original)
    List<Integer> idsReproducciones = reproduccionRepository.findAllSongIdsDistinct();

    List<Integer> idsValoraciones = valoracionRepository.findAll()
            .stream()
            .map(ValoracionDocument::getIdSong)
            .distinct()
            .collect(Collectors.toList());

    Set<Integer> allSongIds = new HashSet<>();
    allSongIds.addAll(idsReproducciones);
    allSongIds.addAll(idsValoraciones);

    Map<Integer, EstadisticaCancionDocument> mapa = new HashMap<>();

    // 3. Inicializar mapa (Lógica original)
    for (Integer id : allSongIds) {
        EstadisticaCancionDocument e = new EstadisticaCancionDocument();
        e.setIdCancion(id);
        e.setValoracionMedia(0f);
        e.setTotalValoraciones(0);
        e.setReproduccionesTotales(0L);
        mapa.put(id, e);
    }

    // 4. Contar Reproducciones (AQUÍ ESTÁ EL CAMBIO IMPORTANTE)
    for (Integer id : allSongIds) {
        long totalRep;
        
        if (filtrarPorFecha) {
            // Si hay fechas, usamos el nuevo método del repositorio
            totalRep = reproduccionRepository.countByIdCancionAndFechaBetween(id, inicio, fin);
        } else {
            // Si NO hay fechas, usamos el método histórico de siempre
            totalRep = reproduccionRepository.countByIdCancion(id);
        }
        
        mapa.get(id).setReproduccionesTotales(totalRep);
    }

    // 5. Procesar Valoraciones (Lógica original - Las valoraciones suelen ser históricas)
    List<ValoracionDocument> valoraciones = valoracionRepository.findAll();
    for (ValoracionDocument v : valoraciones) {
        EstadisticaCancionDocument e = mapa.get(v.getIdSong());
        if (e != null) {
            e.setTotalValoraciones(e.getTotalValoraciones() + 1);
            e.setValoracionMedia(e.getValoracionMedia() + v.getValoracion());
        }
    }

    // 6. Calcular medias (Lógica original)
    for (EstadisticaCancionDocument e : mapa.values()) {
        if (e.getTotalValoraciones() > 0) {
            e.setValoracionMedia(e.getValoracionMedia() / e.getTotalValoraciones());
        }
    }

    // 7. Ordenar y devolver (Lógica original)
    List<EstadisticaCancionDocument> out = new ArrayList<>(mapa.values());
    out.sort(Comparator.comparing(EstadisticaCancionDocument::getIdCancion));

    return ResponseEntity.ok(out);
}


    // ----------------------------------------------------
    // GET /estadisticas/albumes
    // ----------------------------------------------------
    
@GetMapping("/estadisticas/albumes")
public ResponseEntity<List<EstadisticaAlbumDocument>> getEstadisticasAlbumes(
        @RequestParam(required = false) String fechaInicio,
        @RequestParam(required = false) String fechaFin
) {

    // 0. CONFIGURACIÓN DE FECHAS
    // -------------------------------------------------------------------------
    LocalDateTime inicio = null;
    LocalDateTime fin = null;
    boolean filtrarPorFecha = (fechaInicio != null && !fechaInicio.isEmpty() && 
                               fechaFin != null && !fechaFin.isEmpty());

    if (filtrarPorFecha) {
        try {
            inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);
        } catch (Exception e) {
            filtrarPorFecha = false;
        }
    }

    Map<Integer, EstadisticaAlbumDocument> mapa = new HashMap<>();

    // PASO 1: Buscar álbumes a través de las canciones (Histórico)
    // -------------------------------------------------------------------------
    // Obtenemos todas las canciones que han tenido actividad alguna vez
    List<Integer> songIdsConPlays = reproduccionRepository.findAllSongIdsDistinct();
    
    Set<Integer> albumIdsActivos = new HashSet<>();

    for (Integer idCancion : songIdsConPlays) {
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion);
        if (idAlbum != null && idAlbum > 0) {
            albumIdsActivos.add(idAlbum);
        }
    }

    // Inicializamos el mapa
    for (Integer idAlbum : albumIdsActivos) {
        EstadisticaAlbumDocument est = new EstadisticaAlbumDocument();
        est.setIdAlbum(idAlbum);
        est.setReproduccionesTotales(0L);
        est.setTotalValoraciones(0);
        est.setValoracionMedia(0f);
        mapa.put(idAlbum, est);
    }

    // PASO 2: Añadir información de VALORACIONES
    // -----------------------------------------------------------------------
    List<ValoracionDocument> valoraciones = valoracionRepository.findAll();
    for (ValoracionDocument v : valoraciones) {
        Integer idAlbum = v.getIdAlbum();
        if (idAlbum == null) continue;

        mapa.putIfAbsent(idAlbum, new EstadisticaAlbumDocument());
        EstadisticaAlbumDocument est = mapa.get(idAlbum);
        
        if (est.getIdAlbum() == null) {
            est.setIdAlbum(idAlbum);
            est.setReproduccionesTotales(0L);
            est.setValoracionMedia(0f);
            est.setTotalValoraciones(0);
        }

        est.setTotalValoraciones(est.getTotalValoraciones() + 1);
        est.setValoracionMedia(est.getValoracionMedia() + v.getValoracion());
    }

    // PASO 3: Calcular REPRODUCCIONES TOTALES por álbum (AQUÍ ESTÁ EL CAMBIO)
    // -----------------------------------------------------------------------
    for (Integer idAlbum : mapa.keySet()) {
        List<Integer> idsCanciones = contenidoService.obtenerIdsCancionesPorAlbum(idAlbum);
        
        if (idsCanciones != null && !idsCanciones.isEmpty()) {
            long totalRep = 0L;
            
            for (Integer idCancion : idsCanciones) {
                // 👇 LÓGICA DE FILTRADO
                if (filtrarPorFecha) {
                    // Sumamos solo las reproducciones en el rango de fechas
                    totalRep += reproduccionRepository.countByIdCancionAndFechaBetween(idCancion, inicio, fin);
                } else {
                    // Sumamos el total histórico
                    totalRep += reproduccionRepository.countByIdCancion(idCancion);
                }
            }
            
            mapa.get(idAlbum).setReproduccionesTotales(totalRep);
        }
    }

    // PASO 4: Calcular media real de valoraciones y Limpieza
    // -----------------------------------------------------------------------
    for (EstadisticaAlbumDocument est : mapa.values()) {
        if (est.getTotalValoraciones() > 0) {
            est.setValoracionMedia(est.getValoracionMedia() / est.getTotalValoraciones());
        }
    }

    // Convertir a lista
    List<EstadisticaAlbumDocument> resultado = new ArrayList<>(mapa.values());

    // OPCIONAL: Si estás filtrando por fecha, quizás quieras quitar los álbumes que quedaron con 0 reproducciones
    /* if (filtrarPorFecha) {
        resultado.removeIf(a -> a.getReproduccionesTotales() == 0 && a.getTotalValoraciones() == 0);
    } 
    */

    return ResponseEntity.ok(resultado);
}

    // ----------------------------------------------------
    // POST /reproducciones
    // ----------------------------------------------------
    @PostMapping("/reproducciones")
    public ResponseEntity<ReproduccionDocument> postReproduccion(@RequestBody ReproduccionDocument reproduccion) {

        reproduccion.setFecha(LocalDateTime.now());
        ReproduccionDocument nueva = reproduccionRepository.save(reproduccion);

        // Actualizar estadísticas
        updaterService.actualizarEstadisticasPostReproduccion(reproduccion.getIdCancion());

        return new ResponseEntity<>(nueva, HttpStatus.CREATED);
    }


    // ----------------------------------------------------
    // POST /estadisticas/albumes/{id}/actualizar-reproducciones
    // ----------------------------------------------------
    @PostMapping("/estadisticas/albumes/{id}/actualizar-reproducciones")
    public ResponseEntity<Void> actualizarReproduccionesAlbum(@PathVariable("id") Integer id) {
        updaterService.actualizarReproduccionesTotalesAlbum(id);
        return ResponseEntity.ok().build();
    }


    // ----------------------------------------------------
    // DELETE /estadisticas/canciones/{id}
    // ----------------------------------------------------
    @DeleteMapping("/estadisticas/canciones/{id}")
    public ResponseEntity<Void> borrarEstadisticasCancion(@PathVariable("id") Integer id) {
        updaterService.borrarEstadisticasCancionYActualizarAlbum(id);
        return ResponseEntity.noContent().build();
    }


    // ----------------------------------------------------
    // POST /estadisticas/canciones/reproducciones
    // ----------------------------------------------------
    @PostMapping("/estadisticas/canciones/reproducciones")
    public ResponseEntity<Map<String, Long>> obtenerSumaReproducciones(@RequestBody List<Integer> ids) {

        long suma = ids.stream()
                .mapToLong(id -> reproduccionRepository.countByIdCancion(id))
                .sum();

        Map<String, Long> r = new HashMap<>();
        r.put("reproducciones_totales", suma);
        return ResponseEntity.ok(r);
    }
}
