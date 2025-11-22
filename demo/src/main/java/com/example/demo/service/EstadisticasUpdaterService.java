package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.EstadisticaAlbumDocument;
import com.example.demo.model.EstadisticaArtistaDocument;
import com.example.demo.model.EstadisticaCancionDocument;
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
        
        // 1. ACTUALIZAR CANCIÓN
        // ---------------------
        // CORRECCIÓN LÍNEA 59: Usamos 'long' porque count() devuelve long.
        long totalCancion = reproduccionRepository.countByIdCancion(idCancion);
        
        Optional<EstadisticaCancionDocument> optCancion = cancionRepository.findById(idCancion);
        
        if (optCancion.isPresent()) {
            EstadisticaCancionDocument statsCancion = optCancion.get();
            
            // CORRECCIÓN LÍNEA 66:
            // El error decía "int cannot be converted to Long".
            // Como 'totalCancion' ahora es 'long', esto funcionará si el setter espera Long.
            // Si el setter esperara Integer, cambiaríamos a: (int) totalCancion
            statsCancion.setReproduccionesTotales(totalCancion); 
            
            cancionRepository.save(statsCancion);
            System.out.println("✅ Canción " + idCancion + " actualizada: " + totalCancion);
        } 

        // 2. ACTUALIZAR ÁLBUM
        // -------------------
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion);
        
        if (idAlbum != null && idAlbum > 0) {
            actualizarReproduccionesTotalesAlbum(idAlbum);
        } else {
            System.out.println("La canción " + idCancion + " no tiene álbum asociado.");
        }
    }
    
    // ----------------------------------------------------
    // ACTUALIZACIÓN DE ÁLBUM 
    // ----------------------------------------------------

    @Transactional
    public void actualizarReproduccionesTotalesAlbum(Integer albumId) {
        List<Integer> idsCanciones = contenidoService.obtenerIdsCancionesPorAlbum(albumId);
        if (idsCanciones.isEmpty()) return;

        // Usamos long para la suma
        long reproduccionesTotales = 0;
        for (Integer idCancion : idsCanciones) {
            reproduccionesTotales += reproduccionRepository.countByIdCancion(idCancion); 
        }

        Optional<EstadisticaAlbumDocument> optEstadistica = albumRepository.findById(albumId);
        
        if (optEstadistica.isPresent()) {
            EstadisticaAlbumDocument estadistica = optEstadistica.get();
            
            // OJO AQUÍ: Depende de tu clase Album.
            // Si en Album definiste 'private Long reproduccionesTotales', usa la OPCIÓN A.
            // Si en Album definiste 'private int reproduccionesTotales', usa la OPCIÓN B.
            
            // OPCIÓN A (Si es Long):
            estadistica.setReproduccionesTotales(reproduccionesTotales);
            
            // OPCIÓN B (Si es int - descomenta si falla la A):
            // estadistica.setReproduccionesTotales((int) reproduccionesTotales); 
            
            albumRepository.save(estadistica);
            System.out.println("Álbum " + albumId + " actualizado.");
        }
    }
    
    // ----------------------------------------------------
    // ACTUALIZACIÓN DE ARTISTA
    // ----------------------------------------------------
    
    @Transactional
    public void actualizarReproduccionesTotalesArtista(String emailArtista) {
        List<Integer> idsCanciones = contenidoService.obtenerIdsCancionesPorArtista(emailArtista);
        
        if (idsCanciones.isEmpty()) return;

        long reproduccionesTotales = 0;
        for (Integer idCancion : idsCanciones) {
            reproduccionesTotales += reproduccionRepository.countByIdCancion(idCancion); 
        }
        
        Optional<EstadisticaArtistaDocument> optEstadistica = artistaRepository.findById(emailArtista);
        
        if (optEstadistica.isPresent()) {
            EstadisticaArtistaDocument estadistica = optEstadistica.get();
            
            // OPCIÓN A (Si es Long):
            estadistica.setReproduccionesTotales(reproduccionesTotales);
            
            // OPCIÓN B (Si es int - descomenta si falla la A):
            // estadistica.setReproduccionesTotales((int) reproduccionesTotales); 
            
            artistaRepository.save(estadistica);
        }
    }

    // ----------------------------------------------------
    // BORRADO
    // ----------------------------------------------------
    
    @Transactional
    public void borrarEstadisticasCancionYActualizarAlbum(Integer idCancion) {
        Integer idAlbum = contenidoService.obtenerIdAlbumPorCancion(idCancion); 

        if (cancionRepository.existsById(idCancion)) {
            cancionRepository.deleteById(idCancion);
        }
        
        reproduccionRepository.deleteByIdCancion(idCancion); 
        valoracionRepository.deleteByIdSong(idCancion); 
        
        if (idAlbum != null && idAlbum > 0) {
            actualizarReproduccionesTotalesAlbum(idAlbum); 
        } 
    }
}