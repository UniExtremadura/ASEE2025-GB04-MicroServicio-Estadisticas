package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.EstadisticaAlbumDocument;
import com.example.demo.model.EstadisticaArtistaDocument;
import com.example.demo.repository.EstadisticaAlbumRepository;
import com.example.demo.repository.EstadisticaArtistaRepository;
import com.example.demo.repository.EstadisticaCancionRepository; 
import com.example.demo.repository.ReproduccionRepository;
import com.example.demo.repository.ValoracionRepository; 


@Service
public class EstadisticasUpdaterService {

    private final ContenidoService contenidoService;
    private final ReproduccionRepository reproduccionRepository;
    private final EstadisticaAlbumRepository albumRepository;
    
    private final EstadisticaArtistaRepository artistaRepository; 
    private final EstadisticaCancionRepository cancionRepository;
    private final ValoracionRepository valoracionRepository; 

    public EstadisticasUpdaterService(
        ContenidoService contenidoService, 
        ReproduccionRepository reproduccionRepository, 
        EstadisticaAlbumRepository albumRepository,
        EstadisticaArtistaRepository artistaRepository, 
        EstadisticaCancionRepository cancionRepository, 
        ValoracionRepository valoracionRepository 
    ) {
        this.contenidoService = contenidoService;
        this.reproduccionRepository = reproduccionRepository;
        this.albumRepository = albumRepository;
        this.artistaRepository = artistaRepository; 
        this.cancionRepository = cancionRepository;
        this.valoracionRepository = valoracionRepository;
    }

    // ----------------------------------------------------
    // LÓGICA POST-REPRODUCCIÓN
    // ----------------------------------------------------
    
    @Transactional
    public void actualizarEstadisticasPostReproduccion(Integer idCancion) {
        
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion);
        
        if (idAlbum != null && idAlbum > 0) {
            actualizarReproduccionesTotalesAlbum(idAlbum);
        } else {
            System.out.println("La canción " + idCancion + " no pertenece a un álbum o el ID es cero/nulo. Solo se actualizan estadísticas de canción.");
        }
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
    // ACTUALIZACIÓN DE ARTISTA (Corrección del Tipo)
    // ----------------------------------------------------
    
    @Transactional
    public void actualizarReproduccionesTotalesArtista(String emailArtista) {
        List<Integer> idsCanciones = contenidoService.obtenerIdsCancionesPorArtista(emailArtista);
        
        if (idsCanciones.isEmpty()) {
            System.out.println("Artista " + emailArtista + " no tiene canciones registradas o hubo un error.");
            return;
        }
        long reproduccionesTotales = 0;
        for (Integer idCancion : idsCanciones) {
            reproduccionesTotales += reproduccionRepository.countByIdCancion(idCancion); 
        }
        
        Optional<EstadisticaArtistaDocument> optEstadistica = artistaRepository.findById(emailArtista);
        
        if (optEstadistica.isPresent()) {
            EstadisticaArtistaDocument estadistica = optEstadistica.get();
            
            // 🚩 CORRECCIÓN 1: setReproduccionesTotales acepta Long/long. Pasamos el long.
            estadistica.setReproduccionesTotales(reproduccionesTotales); 
            
            artistaRepository.save(estadistica);
            
            System.out.println("Actualizadas reproducciones del Artista " + emailArtista + ": " + reproduccionesTotales);
        } else {
            System.err.println("Error: No se encontró el documento de estadística para el Artista Email: " + emailArtista);
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
}