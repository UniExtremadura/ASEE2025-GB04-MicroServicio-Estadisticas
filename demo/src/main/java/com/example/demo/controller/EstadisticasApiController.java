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
import com.example.demo.repository.EstadisticaAlbumRepository;
import com.example.demo.repository.EstadisticaCancionRepository;
import com.example.demo.repository.ReproduccionRepository;
import com.example.demo.repository.ValoracionRepository;
import com.example.demo.service.ContenidoService;
import com.example.demo.service.EstadisticasUpdaterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Estadísticas de Música", description = "Gestión y consulta de métricas de reproducción, valoración y agregación de contenido.")
@CrossOrigin(origins = "*")
public class EstadisticasApiController {

    @Autowired private ValoracionRepository valoracionRepository;
    @Autowired private ReproduccionRepository reproduccionRepository;
    @Autowired private EstadisticasUpdaterService updaterService;
    @Autowired private EstadisticaCancionRepository estadisticaCancionRepository;
    @Autowired private EstadisticaAlbumRepository estadisticaAlbumRepository;
    @Autowired private ContenidoService contenidoService; 


    // ----------------------------------------------------
    // POST /estadisticas/cancion
    // ----------------------------------------------------
    @PostMapping("/estadisticas/cancion")
    public ResponseEntity<EstadisticaCancionDocument> crearEstadisticaCancion(@RequestBody Map<String, Integer> request) {
        
        Integer id = request.get("idCancion");
        if (id == null) return ResponseEntity.badRequest().build();

        EstadisticaCancionDocument doc = new EstadisticaCancionDocument();
        doc.setIdCancion(id); 
        doc.setReproduccionesTotales(0L);
        doc.setValoracionMedia(0.0f);
        doc.setTotalValoraciones(0);
        doc.setIngresos(0.0); 

        return ResponseEntity.ok(estadisticaCancionRepository.save(doc));
    }


    // ----------------------------------------------------
    // POST /estadisticas/album
    // ----------------------------------------------------
    @PostMapping("/estadisticas/album")
    public ResponseEntity<EstadisticaAlbumDocument> crearEstadisticaAlbum(@RequestBody Map<String, Integer> request) {
        
        Integer id = request.get("idAlbum");
        if (id == null) return ResponseEntity.badRequest().build();

        EstadisticaAlbumDocument doc = new EstadisticaAlbumDocument();
        doc.setIdAlbum(id); 
        doc.setReproduccionesTotales(0L);
        doc.setValoracionMedia(0.0f);
        doc.setTotalValoraciones(0);
        doc.setIngresos(0.0); 

        return ResponseEntity.ok(estadisticaAlbumRepository.save(doc));
    }


    // ----------------------------------------------------
    // GET /estadisticas/canciones
    // ----------------------------------------------------
    @GetMapping("/estadisticas/canciones")
    public ResponseEntity<List<EstadisticaCancionDocument>> getEstadisticasCanciones(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {

        // FECHAS
        LocalDateTime inicio = null;
        LocalDateTime fin = null;
        boolean filtrar = fechaInicio != null && !fechaInicio.isEmpty()
                       && fechaFin != null && !fechaFin.isEmpty();

        if (filtrar) {
            try {
                inicio = LocalDate.parse(fechaInicio).atStartOfDay();
                fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);
            } catch (Exception e) { filtrar = false; }
        }

        // IDS
        Set<Integer> ids = new HashSet<>();
        ids.addAll(reproduccionRepository.findAll()
                .stream().map(ReproduccionDocument::getIdCancion).toList());
        ids.addAll(valoracionRepository.findAll()
                .stream().map(ValoracionDocument::getIdSong).toList());

        Map<Integer, EstadisticaCancionDocument> mapa = new HashMap<>();

        for (Integer id : ids) {
            EstadisticaCancionDocument e = new EstadisticaCancionDocument();
            e.setIdCancion(id);
            e.setReproduccionesTotales(0L);
            mapa.put(id, e);
        }

        // REPRODUCCIONES
        for (Integer id : ids) {
            Long rep;
            if (filtrar) {
                rep = reproduccionRepository.countByIdCancionAndFechaBetween(id, inicio, fin);
            } else {
                rep = reproduccionRepository.countByIdCancion(id);
            }
            mapa.get(id).setReproduccionesTotales(rep != null ? rep : 0L);
        }

        // VALORACIONES
        for (ValoracionDocument v : valoracionRepository.findAll()) {
            EstadisticaCancionDocument e = mapa.get(v.getIdSong());
            if (e != null) {
                e.setTotalValoraciones(e.getTotalValoraciones() + 1);
                e.setValoracionMedia(e.getValoracionMedia() + v.getValoracion());
            }
        }

        // MEDIA
        for (EstadisticaCancionDocument e : mapa.values()) {
            if (e.getTotalValoraciones() > 0) {
                e.setValoracionMedia(e.getValoracionMedia() / e.getTotalValoraciones());
            }
        }

        List<EstadisticaCancionDocument> out = new ArrayList<>(mapa.values());
        out.sort(Comparator.comparing(EstadisticaCancionDocument::getIdCancion));
        return ResponseEntity.ok(out);
    }


    // ----------------------------------------------------
    // GET /estadisticas/canciones/{id}
    // ----------------------------------------------------
    @GetMapping("/estadisticas/canciones/{id}")
    public ResponseEntity<EstadisticaCancionDocument> getEstadisticasCancionPorId(@PathVariable Integer id) {
        return estadisticaCancionRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    // ----------------------------------------------------
    // GET /estadisticas/albumes/{id}
    // ----------------------------------------------------
    @GetMapping("/estadisticas/albumes/{id}")
    public ResponseEntity<EstadisticaAlbumDocument> getEstadisticasAlbumPorId(@PathVariable Integer id) {
        return estadisticaAlbumRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    // ----------------------------------------------------
    // GET /estadisticas/albumes
    // ----------------------------------------------------
    @GetMapping("/estadisticas/albumes")
    public ResponseEntity<List<EstadisticaAlbumDocument>> getEstadisticasAlbumes(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        LocalDateTime inicio = null;
        LocalDateTime fin = null;
        boolean filtrar = fechaInicio != null && !fechaInicio.isEmpty()
                       && fechaFin != null && !fechaFin.isEmpty();

        if (filtrar) {
            try {
                inicio = LocalDate.parse(fechaInicio).atStartOfDay();
                fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);
            } catch (Exception e) { filtrar = false; }
        }

        // ALBUMES ACTIVOS
        Set<Integer> albumes = new HashSet<>();
        for (Integer idSong : contenidoService.obtenerIdsCanciones()) {
            Integer alb = contenidoService.obtenerIdAlbumPorCancion(idSong);
            if (alb != null && alb > 0) albumes.add(alb);
        }

        Map<Integer, EstadisticaAlbumDocument> mapa = new HashMap<>();

        for (Integer id : albumes) {
            EstadisticaAlbumDocument e = new EstadisticaAlbumDocument();
            e.setIdAlbum(id);
            e.setReproduccionesTotales(0L);
            mapa.put(id, e);
        }

        // VALORACIONES
        for (ValoracionDocument v : valoracionRepository.findAll()) {
            Integer alb = v.getIdAlbum();
            if (alb == null) continue;

            mapa.putIfAbsent(alb, new EstadisticaAlbumDocument());
            EstadisticaAlbumDocument e = mapa.get(alb);

            if (e.getIdAlbum() == null) e.setIdAlbum(alb);
            e.setTotalValoraciones(e.getTotalValoraciones() + 1);
            e.setValoracionMedia(e.getValoracionMedia() + v.getValoracion());
        }

        // REPRODUCCIONES
        for (Integer idAlbum : mapa.keySet()) {
            Long total = 0L;

            for (Integer idCancion : contenidoService.obtenerIdsCancionesPorAlbum(idAlbum)) {
                Long rep;
                if (filtrar) {
                    rep = reproduccionRepository.countByIdCancionAndFechaBetween(idCancion, inicio, fin);
                } else {
                    rep = reproduccionRepository.countByIdCancion(idCancion);
                }
                total += (rep != null ? rep : 0L);
            }

            mapa.get(idAlbum).setReproduccionesTotales(total);
        }

        // MEDIA VALORACIÓN
        for (EstadisticaAlbumDocument e : mapa.values()) {
            if (e.getTotalValoraciones() > 0) {
                e.setValoracionMedia(e.getValoracionMedia() / e.getTotalValoraciones());
            }
        }

        return ResponseEntity.ok(new ArrayList<>(mapa.values()));
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
    // POST /reproducciones
    // ----------------------------------------------------
    @PostMapping("/reproducciones")
    public ResponseEntity<ReproduccionDocument> postReproduccion(@RequestBody ReproduccionDocument reproduccion) {
        reproduccion.setFecha(LocalDateTime.now());
        ReproduccionDocument guardado = reproduccionRepository.save(reproduccion);
        updaterService.actualizarEstadisticasPostReproduccion(guardado.getIdCancion());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }


    // ----------------------------------------------------
    // POST /estadisticas/canciones/reproducciones
    // Obtiene las reproducciones individuales para una lista de IDs (con filtro opcional)
    // ----------------------------------------------------
    @Operation(
        summary = "Obtener reproducciones por lista de IDs",
        description = "Devuelve un mapa con las reproducciones de cada canción solicitada, filtradas por rango de fechas opcional."
    )
    @PostMapping("/estadisticas/canciones/reproducciones")
    public ResponseEntity<Map<String, Long>> obtenerSumaReproducciones(@RequestBody Map<String, Object> request) {
        
        // 1. Extracción y CONVERSIÓN SEGURA de IDs
        List<Object> idsBrutos = (List<Object>) request.get("ids");
        
        if (idsBrutos == null || idsBrutos.isEmpty()) {
            return ResponseEntity.ok(new HashMap<>());
        }

        List<Integer> idsCanciones = idsBrutos.stream()
            .map(idObj -> {
                if (idObj instanceof Number) return ((Number) idObj).intValue();
                try {
                    return Integer.parseInt(String.valueOf(idObj));
                } catch (NumberFormatException e) {
                    return null; // Ignorar IDs inválidos
                }
            })
            .filter(id -> id != null)
            .collect(Collectors.toList());

        // 2. Extracción y Parsing de Fechas
        String fechaInicio = (String) request.get("fechaInicio");
        String fechaFin = (String) request.get("fechaFin");

        LocalDateTime inicio = null;
        LocalDateTime fin = null;
        boolean filtrarPorFecha = (fechaInicio != null && !fechaInicio.isEmpty() && 
                                   fechaFin != null && !fechaFin.isEmpty());

        if (filtrarPorFecha) {
            try {
                inicio = LocalDate.parse(fechaInicio).atStartOfDay();
                fin = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);
            } catch (Exception e) {
                System.err.println("Error parseando fechas en POST: " + e.getMessage());
                filtrarPorFecha = false;
            }
        }

        // 3. Construir el Mapa de Respuesta (ID -> Plays)
        Map<String, Long> resultado = new HashMap<>();

        for (Integer idCancion : idsCanciones) {
            long count;
            if (filtrarPorFecha) {
                // Usamos el método filtrado (Devuelve 0 si no hay datos en esas fechas)
                count = reproduccionRepository.countByIdCancionAndFechaBetween(idCancion, inicio, fin);
            } else {
                // Usamos el total histórico
                count = reproduccionRepository.countByIdCancion(idCancion);
            }
            // Guardamos en el mapa: Clave="1", Valor=10
            resultado.put(String.valueOf(idCancion), count);
        }
        
        return ResponseEntity.ok(resultado);
    }
    // ----------------------------------------------------
    // DELETE /estadisticas/canciones/{id}
    // ----------------------------------------------------
    @DeleteMapping("/estadisticas/canciones/{id}")
    public ResponseEntity<Void> borrarEstadisticasCancion(@PathVariable Integer id) {
        updaterService.borrarEstadisticasCancionYActualizarAlbum(id); 
        return ResponseEntity.noContent().build();
    }


    // ----------------------------------------------------
    // POST /estadisticas/compras/cancion
    // ----------------------------------------------------
      @Operation(summary = "Registrar compra de canción", description = "Suma el precio indicado a los ingresos de la canción y de su álbum.")
    @PostMapping("/estadisticas/compras/cancion")
    public ResponseEntity<Void> registrarCompraCancion(@RequestBody Map<String, Object> request) {
        
        // Extraer datos del JSON de forma segura
        Integer idCancion = (Integer) request.get("idCancion");
        
        // Manejo seguro del precio (puede venir como Integer o Double en JSON)
        Double precio = 0.0;
        if (request.get("precio") instanceof Number) {
            precio = ((Number) request.get("precio")).doubleValue();
        }

        if (idCancion == null || precio == null) {
            return ResponseEntity.badRequest().build();
        }

        updaterService.registrarCompraCancion(idCancion, precio);
        
        return ResponseEntity.ok().build();
    }


    // ----------------------------------------------------
    // POST /estadisticas/compras/album
    // ----------------------------------------------------
      @Operation(summary = "Registrar compra de álbum", description = "Suma el precio indicado a los ingresos del álbum.")
    @PostMapping("/estadisticas/compras/album")
    public ResponseEntity<Void> registrarCompraAlbum(@RequestBody Map<String, Object> request) {
        
        System.out.println(">>> DEBUG: Recibida petición compra ÁLBUM: " + request);

        // 1. Extracción segura del ID
        Object idObj = request.get("idAlbum");
        Integer idAlbum = null;
        if (idObj instanceof Integer) {
            idAlbum = (Integer) idObj;
        } else if (idObj instanceof String) {
            try { idAlbum = Integer.parseInt((String) idObj); } catch (Exception e) {}
        }

        // 2. Extracción segura del Precio
        Object precioObj = request.get("precio");
        Double precio = 0.0;
        if (precioObj instanceof Number) {
            precio = ((Number) precioObj).doubleValue();
        } else if (precioObj instanceof String) {
            try { precio = Double.parseDouble((String) precioObj); } catch (Exception e) {}
        }

        // 3. Validaciones
        if (idAlbum == null) {
            System.err.println(">>> ERROR: idAlbum es NULL");
            return ResponseEntity.badRequest().build();
        }
        if (precio == null || precio <= 0) {
             System.err.println(">>> ERROR: Precio es 0 o inválido: " + precio);
             // A veces esto no es error (si es gratis), pero para debug avisa
        }

        System.out.println(">>> DEBUG: Llamando al servicio -> ID: " + idAlbum + " | Precio: " + precio);

        // 4. Llamada al servicio
        updaterService.registrarIngresoAlbum(idAlbum, precio);
        
        return ResponseEntity.ok().build();
    }

    // ----------------------------------------------------
    // GET /api/canciones/artista/{email}
    // ----------------------------------------------------
    @CrossOrigin(origins = "*")
    @GetMapping("/api/canciones/artista/{email}")
    public ResponseEntity<List<EstadisticaCancionDocument>> getCancionesPorArtista(@PathVariable String email) {
        List<Integer> ids = contenidoService.obtenerIdsCancionesPorArtista(email);
        if (ids.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<EstadisticaCancionDocument> estadisticas = (List<EstadisticaCancionDocument>) estadisticaCancionRepository.findAllById(ids);
        return ResponseEntity.ok(estadisticas);
    }

    // ----------------------------------------------------
    // GET /api/albumes/artista/{email}
    // ----------------------------------------------------
    @CrossOrigin(origins = "*")
    @GetMapping("/api/albumes/artista/{email}")
    public ResponseEntity<List<EstadisticaAlbumDocument>> getAlbumesPorArtista(@PathVariable String email) {
        List<Integer> ids = contenidoService.obtenerIdsAlbumesPorArtista(email);
        if (ids.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<EstadisticaAlbumDocument> estadisticas = (List<EstadisticaAlbumDocument>) estadisticaAlbumRepository.findAllById(ids);
        return ResponseEntity.ok(estadisticas);
    }
}