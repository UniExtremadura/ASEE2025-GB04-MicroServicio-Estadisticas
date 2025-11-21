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

@Value("${contenido.service.url:http://localhost:8080}") 
    private String CONTENIDO_BASE_URL; 

    // Asumimos que la URL de Usuario es http://localhost:8000/api
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
        // La URL final es http://localhost:8080/api/canciones
        String url = CONTENIDO_BASE_URL + "/api/canciones"; 
        
        try {
            // 1. Cambiamos el tipo de la respuesta para que pueda mapear la lista de objetos JSON
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            // 2. Extraemos el campo "id" de cada objeto JSON y lo convertimos a List<Integer>
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
        // La URL final es http://localhost:8080/api/canciones
        String url = CONTENIDO_BASE_URL + "/api/albumes"; 
        
        try {
            // 1. Cambiamos el tipo de la respuesta para que pueda mapear la lista de objetos JSON
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            // 2. Extraemos el campo "id" de cada objeto JSON y lo convertimos a List<Integer>
            return response.getBody() != null ? response.getBody().stream()
                .map(map -> (Integer) map.get("id")) 
                .collect(Collectors.toList())
                : Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Error al obtener IDs de canciones: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public List<Integer> obtenerIdsCancionesPorAlbum(Integer albumId) {
        String url = CONTENIDO_BASE_URL + "/albumes/" + albumId;
        
        try {
            // Llama al endpoint y obtiene la respuesta como String
            String jsonResponse = restTemplate.getForObject(url, String.class);
            
            // Usamos ObjectMapper para parsear el JSON y extraer el array "canciones_ids"
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode cancionesIdsNode = root.get("canciones_ids");

            if (cancionesIdsNode != null && cancionesIdsNode.isArray()) {
                // Mapea los nodos a Integer
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

 public Integer obtenerIdAlbumPorCancion(Integer idCancion) {
        // Endpoint: GET /api/canciones/{song_id}
        String url = CONTENIDO_BASE_URL + "/canciones/" + idCancion;
        
        try {
            // 🚩 Mapeamos a la clase ExternalSongData (que incluye idAlbum)
            ExternalSongData info = restTemplate.getForObject(url, ExternalSongData.class);
            return info != null ? info.getIdAlbum() : null; // Usamos getIdAlbum() de ExternalSongData

        } catch (Exception e) {
            System.err.println("Error al obtener ID de álbum para canción " + idCancion + ": " + e.getMessage());
            return null;
        }
    }
    
    public List<Integer> obtenerIdsCancionesPorArtista(String emailArtista) {
    // Endpoint: GET /api/artistas/{email_artista}/canciones
    String url = CONTENIDO_BASE_URL + "/artistas/" + emailArtista + "/canciones";
    
    try {
        // La respuesta es una lista de objetos complejos, los mapeamos a ExternalSongData
        ResponseEntity<List<ExternalSongData>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<ExternalSongData>>() {}
        );
        
        // Extraemos solo el campo 'id' de cada ExternalSongData
        return response.getBody() != null ? response.getBody().stream()
            .map(ExternalSongData::getId) // Asumiendo que ExternalSongData tiene getId() y es Integer
            .collect(Collectors.toList())
            : Collections.emptyList();

        } catch (Exception e) {
          System.err.println("Error al obtener IDs de canciones para el artista " + emailArtista + ": " + e.getMessage());
          return Collections.emptyList();
        }
    }
}