package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.ExternalSongData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ContenidoService {

@Value("${contenido.service.url:http://localhost:8080/api}") 
private String CONTENIDO_BASE_URL; 

// Para el usuario, hacemos lo mismo:
@Value("${usuario.service.url:http://localhost:8000/api}") 
private String USUARIO_BASE_URL;
    
    private final RestTemplate restTemplate;

    public ContenidoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ------------------------------------
    // MÉTODOS PARA OBTENER IDs DEL SERVICIO EXTERNO
    // ------------------------------------

// 🚩 CORRECCIÓN 3: Eliminamos /api del path, ya que está en CONTENIDO_BASE_URL
public List<Integer> obtenerIdsCanciones() {
        // Llama a: http://localhost:8080/api/canciones
        String url = CONTENIDO_BASE_URL + "/canciones"; 
        
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


// 🚩 CORRECCIÓN 4: Eliminamos /api del path
    public List<Integer> obtenerIdsAlbumes() {
        // Llama a: http://localhost:8080/api/albumes
        String url = CONTENIDO_BASE_URL + "/albumes"; 
        
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
    
// 🚩 CORRECCIÓN 5: Eliminamos /api del path (ya que el path /albumes/{id} no lleva /api)
    public List<Integer> obtenerIdsCancionesPorAlbum(Integer albumId) {
        // Llama a: http://localhost:8080/api/albumes/{albumId}
       String url = CONTENIDO_BASE_URL + "/albumes/" + albumId;
        
        try {
            // ... (cuerpo de método que usa ObjectMapper para JSON parsing)
            String jsonResponse = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode cancionesIdsNode = root.get("canciones_ids");

            if (cancionesIdsNode != null && cancionesIdsNode.isArray()) {
                return StreamSupport.stream(cancionesIdsNode.spliterator(), false)
                    .map(JsonNode::asInt)
                    .collect(Collectors.toList());
            }
            return Collections.emptyList();
            
        } catch (Exception e) {
            System.err.println("Error al obtener IDs de canciones para álbum " + albumId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

// 🚩 CORRECCIÓN 6: Eliminamos /api del path
    public Integer obtenerIdAlbumPorCancion(Integer idCancion) {
        // Llama a: http://localhost:8080/api/canciones/{song_id}
        String url = CONTENIDO_BASE_URL + "/canciones/" + idCancion;
        
        try {
            ExternalSongData info = restTemplate.getForObject(url, ExternalSongData.class);
            return info != null ? info.getIdAlbum() : null;

        } catch (Exception e) {
            System.err.println("Error al obtener ID de álbum para canción " + idCancion + ": " + e.getMessage());
            return null;
        }
    }
    
// 🚩 CORRECCIÓN 7: Eliminamos /api del path
    public List<Integer> obtenerIdsCancionesPorArtista(String emailArtista) {
        // Llama a: http://localhost:8080/api/artistas/{email_artista}/canciones
        String url = CONTENIDO_BASE_URL + "/artistas/" + emailArtista + "/canciones";
        
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
          System.err.println("Error al obtener IDs de canciones para el artista " + emailArtista + ": " + e.getMessage());
          return Collections.emptyList();
        }
    }
}