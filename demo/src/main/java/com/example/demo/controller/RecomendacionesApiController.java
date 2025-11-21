package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.ValoracionRepository;

@RestController
@RequestMapping("/api")
public class RecomendacionesApiController {

    @Autowired
    private ValoracionRepository valoracionRepository;

    // Mock de un servicio de catálogo de canciones.
    // En una arquitectura real, esto sería una llamada a otro microservicio.
    private String getSongNameById(Integer id) {
        return "Canción " + id;
    }

    private List<String> getArtistNameBySongId(Integer id) {
        return List.of("Artista del " + id);
    }

    /*@GetMapping("/recomendaciones/{email}")
    public List<Recomendacion> getRecomendaciones(@PathVariable String email) {
        // 1. Obtener las canciones que el usuario ya ha valorado.
        List<Integer> userRatedSongIds = valoracionRepository.findByEmailUser(email)
                .stream()
                .map(ValoracionDocument::getIdSong)
                .collect(Collectors.toList());

        // 2. Encontrar canciones con alta valoración (ej. >= 4) que el usuario no ha valorado.
        List<ValoracionDocument> highRatedSongs = valoracionRepository.findByValoracionGreaterThan(4);

        // 3. Filtrar y crear las recomendaciones
        return highRatedSongs.stream()
                // Filtrar canciones que el usuario ya ha valorado
                .filter(v -> !userRatedSongIds.contains(v.getIdSong()))
                // Evitar duplicados de canciones
                .collect(Collectors.groupingBy(ValoracionDocument::getIdSong, Collectors.counting()))
                .keySet()
                .stream()
                .map(songId -> {
                    Recomendacion rec = new Recomendacion();
                    rec.setTipoContenido("cancion");
                    rec.setIdContenido(songId);
                    // En un sistema real, obtendríamos el nombre y artista de un servicio de catálogo
                    rec.setNomContenido(getSongNameById(songId));
                    rec.setArtistas(getArtistNameBySongId(songId));
                    rec.setRazon("Basado en altas valoraciones de otros usuarios.");
                    rec.setConfianza(0.8f); // Valor de confianza estático para el ejemplo
                    return rec;
                })
                .limit(10) // Limitar el número de recomendaciones
                .collect(Collectors.toList());
    }*/
}
