package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.EstadisticaAlbumDocument;
import com.example.demo.model.EstadisticaCancionDocument;
import com.example.demo.model.ValoracionDocument;
import com.example.demo.repository.EstadisticaAlbumRepository;
import com.example.demo.repository.EstadisticaCancionRepository; 
import com.example.demo.repository.ReproduccionRepository;
import com.example.demo.repository.ValoracionRepository; 


@Service
public class EstadisticasUpdaterService {

    private final ContenidoService contenidoService;
    private final ReproduccionRepository reproduccionRepository;
    private final EstadisticaAlbumRepository albumRepository;
    
    private final EstadisticaCancionRepository cancionRepository;
    private final ValoracionRepository valoracionRepository; 

    public EstadisticasUpdaterService(
        ContenidoService contenidoService, 
        ReproduccionRepository reproduccionRepository, 
        EstadisticaAlbumRepository albumRepository,
        EstadisticaCancionRepository cancionRepository, 
        ValoracionRepository valoracionRepository 
    ) {
        this.contenidoService = contenidoService;
        this.reproduccionRepository = reproduccionRepository;
        this.albumRepository = albumRepository;
        this.cancionRepository = cancionRepository;
        this.valoracionRepository = valoracionRepository;
    }

    // ----------------------------------------------------
    // LÓGICA POST-REPRODUCCIÓN
    // ----------------------------------------------------
    
    @Transactional
    public void actualizarEstadisticasPostReproduccion(Integer idCancion) {
        
        actualizarEstadisticasCancion(idCancion);
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion);
        
        if (idAlbum != null && idAlbum > 0) {
            actualizarReproduccionesTotalesAlbum(idAlbum);
        } else {
            System.out.println("La canción " + idCancion + " no pertenece a un álbum o el ID es cero/nulo. Solo se actualizan estadísticas de canción.");
        }
    }

    // ----------------------------------------------------
    // ACTUALIZACIÓN DE CANCIÓN
    // ----------------------------------------------------
    @Transactional
    public void actualizarEstadisticasCancion(Integer idCancion) {
        List<ValoracionDocument> valoraciones = valoracionRepository.findByIdSong(idCancion);

        EstadisticaCancionDocument estadistica = cancionRepository.findById(idCancion).orElse(new EstadisticaCancionDocument());
        estadistica.setIdCancion(idCancion);

        long reproduccionesTotales = reproduccionRepository.countByIdCancion(idCancion);
        estadistica.setReproduccionesTotales((long) reproduccionesTotales);

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
        cancionRepository.save(estadistica);
        System.out.println("Actualizadas estadísticas de la Canción " + idCancion);
    }
    
    // ----------------------------------------------------
    // ACTUALIZACIÓN DE ÁLBUM (Método Faltante, AÑADIDO AQUÍ)
    // ----------------------------------------------------

    @Transactional
    public void actualizarReproduccionesTotalesAlbum(Integer albumId) {
        List<Integer> idsCanciones = contenidoService.obtenerIdsCancionesPorAlbum(albumId);
        if (idsCanciones.isEmpty()) {
            System.out.println("Álbum " + albumId + " no tiene canciones registradas o hubo un error al obtener la lista.");
            return;
        }
        long reproduccionesTotales = 0;
        for (Integer idCancion : idsCanciones) {
            reproduccionesTotales += reproduccionRepository.countByIdCancion(idCancion); 
        }

        Optional<EstadisticaAlbumDocument> optEstadistica = albumRepository.findById(albumId);
        
        if (optEstadistica.isPresent()) {
            EstadisticaAlbumDocument estadistica = optEstadistica.get();
            
            // 🚩 setReproduccionesTotales espera Long/long
            estadistica.setReproduccionesTotales(reproduccionesTotales); 
            albumRepository.save(estadistica);
            
            System.out.println("Actualizadas reproducciones del Álbum " + albumId + ": " + reproduccionesTotales);
        } else {
            System.err.println("Error: No se encontró el documento de estadística para el Álbum ID: " + albumId);
        }
    }
    
    // ----------------------------------------------------
    // BORRADO Y ACTUALIZACIÓN DE ÁLBUM
    // ----------------------------------------------------
    
    @Transactional
    public void borrarEstadisticasCancionYActualizarAlbum(Integer idCancion) {
        
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion); 

        // --- BORRADO LOCAL ---
        
        if (cancionRepository.existsById(idCancion)) {
            cancionRepository.deleteById(idCancion);
        }
        
        // Estos métodos deben estar definidos en las interfaces de Repository
        reproduccionRepository.deleteByIdCancion(idCancion); 
        valoracionRepository.deleteByIdSong(idCancion); 
        
        // --- ACTUALIZACIÓN AGREGADA ---
        
        if (idAlbum != null && idAlbum > 0) {
            actualizarReproduccionesTotalesAlbum(idAlbum); 
        } 
        
        System.out.println("Proceso de borrado de Canción ID " + idCancion + " finalizado. Álbum afectado: " + idAlbum);
    }
public void registrarCompraCancion(Integer idCancion, Double precio) {
        // 1. Recuperamos el documento o creamos uno nuevo
        EstadisticaCancionDocument stats = cancionRepository.findById(idCancion)
            .orElse(new EstadisticaCancionDocument());
        
        // 2. Inicialización si es nuevo
        if (stats.getIdCancion() == null) {
            stats.setIdCancion(idCancion);
            stats.setReproduccionesTotales(0L);
            stats.setValoracionMedia(0f);
            stats.setTotalValoraciones(0);
            stats.setIngresos(0.0); // Inicializamos explícitamente
        }

        // 3. Sumamos el ingreso
        // Al ser 'double' primitivo, getIngresos() devuelve 0.0 si no se ha tocado, nunca null.
        double ingresosActuales = stats.getIngresos(); 
        stats.setIngresos(ingresosActuales + precio);

        // 4. Guardamos (Esto escribe en MongoDB)
        cancionRepository.save(stats);
        
        System.out.println("💰 Ingresos actualizados Canción " + idCancion + ": +" + precio);

        // Cascada al Álbum
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion);
        if (idAlbum != null && idAlbum > 0) {
            registrarIngresoAlbum(idAlbum, precio);
        }
    }

    public void registrarIngresoAlbum(Integer idAlbum, Double precio) {
        // 1. Recuperamos el documento o creamos uno nuevo
        EstadisticaAlbumDocument stats = albumRepository.findById(idAlbum)
            .orElse(new EstadisticaAlbumDocument());

        // 2. Inicialización si es nuevo
        if (stats.getIdAlbum() == null) {
            stats.setIdAlbum(idAlbum);
            stats.setReproduccionesTotales(0L);
            stats.setValoracionMedia(0f);
            stats.setTotalValoraciones(0);
            stats.setIngresos(0.0);
        }

        // 3. Sumamos
        // ERROR CORREGIDO AQUÍ: Ya no comprobamos null porque es primitive double
        double ingresosActuales = stats.getIngresos(); 
        stats.setIngresos(ingresosActuales + precio);

        // 4. Guardamos (Esto escribe en MongoDB)
        albumRepository.save(stats);

        System.out.println("💰 Ingresos actualizados Álbum " + idAlbum + ": +" + precio);
    }

}