package com.example.demo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.EstadisticaAlbumDocument;
import com.example.demo.model.EstadisticaCancionDocument;
import com.example.demo.repository.EstadisticaAlbumRepository;
import com.example.demo.repository.EstadisticaCancionRepository;
import com.example.demo.service.ContenidoService;
import com.example.demo.service.EstadisticasUpdaterService;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final ContenidoService contenidoService;
    private final EstadisticaCancionRepository cancionRepository;
    private final EstadisticaAlbumRepository albumRepository;
    private final EstadisticasUpdaterService updaterService;

    public DatabaseInitializer(
        ContenidoService contenidoService, 
        EstadisticaCancionRepository cancionRepository,
        EstadisticaAlbumRepository albumRepository,
        EstadisticasUpdaterService updaterService
    ) {
        this.contenidoService = contenidoService;
        this.cancionRepository = cancionRepository;
        this.albumRepository = albumRepository;
        this.updaterService = updaterService;
    }

    @Override
    public void run(String... args) throws Exception {
        inicializarEstadisticasCanciones();
        inicializarEstadisticasAlbumes();
        actualizarEstadisticasExistentes();
    }
    // ------------------------------------
    // INICIALIZACIÓN DE CANCIONES (Lógica Idempotente Correcta)
    // ------------------------------------
    private void inicializarEstadisticasCanciones() {
        System.out.println("--- Inicializando Estadísticas de Canciones ---");

        List<Integer> idsCanciones = contenidoService.obtenerIdsCanciones(); 
        
        if (idsCanciones != null && !idsCanciones.isEmpty()) {
            for (Integer id : idsCanciones) {
                if (id != null && !cancionRepository.existsById(id)) {
                    
                    EstadisticaCancionDocument nuevaEstadistica = new EstadisticaCancionDocument();
                    nuevaEstadistica.setIdCancion(id);
                    
                    cancionRepository.save(nuevaEstadistica);
                    System.out.println("Creada estadística inicial para canción ID: " + id);
                }
            }
        } else {
             System.out.println("No se encontraron IDs de canciones para inicializar.");
        }
    }

    // ------------------------------------
    // INICIALIZACIÓN DE ÁLBUMES (Lógica Idempotente Correcta)
    // ------------------------------------
    private void inicializarEstadisticasAlbumes() {
        System.out.println("--- Inicializando Estadísticas de Álbumes ---");

        List<Integer> idsAlbumes = contenidoService.obtenerIdsAlbumes(); 
        
        if (idsAlbumes != null && !idsAlbumes.isEmpty()) {
            for (Integer id : idsAlbumes) {
                if (id != null && !albumRepository.existsById(id)) {
                    
                    EstadisticaAlbumDocument nuevaEstadistica = new EstadisticaAlbumDocument();
                    nuevaEstadistica.setIdAlbum(id);
                    
                    albumRepository.save(nuevaEstadistica);
                    System.out.println("Creada estadística inicial para álbum ID: " + id);
                }
            }
        } else {
             System.out.println("No se encontraron IDs de álbumes para inicializar.");
        }
    }
    
    // ------------------------------------
    // ACTUALIZACIÓN FORZADA DE ESTADÍSTICAS
    // ------------------------------------
    private void actualizarEstadisticasExistentes() {
        System.out.println("--- Actualizando todas las estadísticas existentes ---");

        List<Integer> idsCanciones = contenidoService.obtenerIdsCanciones();
        if (idsCanciones != null && !idsCanciones.isEmpty()) {
            for (Integer id : idsCanciones) {
                updaterService.actualizarEstadisticasCancion(id);
            }
        }

        List<Integer> idsAlbumes = contenidoService.obtenerIdsAlbumes();
        if (idsAlbumes != null && !idsAlbumes.isEmpty()) {
            for (Integer id : idsAlbumes) {
                updaterService.actualizarReproduccionesTotalesAlbum(id);
            }
        }
    }
}