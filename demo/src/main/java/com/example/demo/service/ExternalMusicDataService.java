package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.ExternalSongData;

@Service
public class ExternalMusicDataService {

    private final RestTemplate restTemplate;

    public ExternalMusicDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExternalSongData getExternalSongData(String cancionId) {
        // Aquí se haría la llamada al microservicio externo
        // La URL debería estar en un fichero de configuración
        String url = "http://localhost:8080/api/canciones/" + cancionId;
        return restTemplate.getForObject(url, ExternalSongData.class);
    }
}