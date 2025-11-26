package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.ExternalSongData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContenidoService {

    @Value("${contenido.service.url:http://localhost:8080}") 
    private String CONTENIDO_BASE_URL; 

    @Value("${usuario.service.url:http://localhost:8000}") 
    private String USUARIO_BASE_URL;
    
    private final RestTemplate restTemplate;

    public ContenidoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ------------------------------------
    // MÉTODOS PARA OBTENER IDs DEL SERVICIO EXTERNO
    // ------------------------------------

    public List<Integer> obtenerIdsCanciones() {
        String url = CONTENIDO_BASE_URL + "/api/canciones"; 
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            return response.getBody() != null ? response.getBody().stream()
                .map(map -> (Integer) map.get("id")) 
                .collect(Collectors.toList())
                : Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Error al obtener IDs de canciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    public List<Integer> obtenerIdsAlbumes() {
        String url = CONTENIDO_BASE_URL + "/api/albumes"; 
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            return response.getBody() != null ? response.getBody().stream()
                .map(map -> (Integer) map.get("id")) 
                .collect(Collectors.toList())
                : Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Error al obtener IDs de álbumes: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    // -------------------------------------------------------------------------
    // CLIENTE: Llama al endpoint /canciones-ids del otro microservicio
    // -------------------------------------------------------------------------
    public List<Integer> obtenerIdsCancionesPorAlbum(Integer albumId) {
        
        // 1. Usamos la URL que YA EXISTE en tu API de Contenidos:
        //    GET /api/albumes/{album_id}/canciones
        String url = CONTENIDO_BASE_URL + "/api/albumes/" + albumId + "/canciones";
        
        try {
            // 2. Como el endpoint devuelve la lista completa de objetos Canción (con titulo, fecha, etc.),
            //    recibimos una lista de Mapas (JSON objects) en lugar de una lista de Integers.
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            // 3. Recorremos la lista de canciones y extraemos solo el campo "id"
            return response.getBody() != null ? response.getBody().stream()
                .map(songJson -> (Integer) songJson.get("id")) 
                .collect(Collectors.toList())
                : Collections.emptyList();
            
        } catch (Exception e) {
            System.err.println("Error al obtener canciones del álbum " + albumId + ": " + e.getMessage());
            // Si el álbum no tiene canciones o no existe, devolvemos lista vacía para no romper el programa
            return Collections.emptyList();
        }
    }

    public Integer obtenerIdAlbumPorCancion(Integer idCancion) {
        String url = CONTENIDO_BASE_URL + "/api/canciones/" + idCancion;
        
        try {
            ExternalSongData info = restTemplate.getForObject(url, ExternalSongData.class);
            return info != null ? info.getIdAlbum() : null;

        } catch (Exception e) {
            return null;
        }
    }
    
    public List<Integer> obtenerIdsCancionesPorArtista(String emailArtista) {
        String url = CONTENIDO_BASE_URL + "/api/artistas/" + emailArtista + "/canciones";
    
        try {
            ResponseEntity<List<ExternalSongData>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<ExternalSongData>>() {}
            );
            
            return response.getBody() != null ? response.getBody().stream()
                .map(ExternalSongData::getId) 
                .collect(Collectors.toList())
                : Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Error al obtener canciones del artista " + emailArtista + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Integer> obtenerIdsAlbumesPorArtista(String emailArtista) {
        List<Integer> idsCanciones = obtenerIdsCancionesPorArtista(emailArtista);
        
        return idsCanciones.stream()
            .map(this::obtenerIdAlbumPorCancion) 
            .filter(Objects::nonNull) 
            .distinct() 
            .collect(Collectors.toList());
    }
}
