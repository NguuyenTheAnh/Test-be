package com.demo.rentalfinder.service.impl;

import com.demo.rentalfinder.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {

    private final WebClient client = WebClient.create("https://nominatim.openstreetmap.org");

    @Override
    public double[] getCoordinates(String query) {
        List<Map<String, Object>> resp = client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", query)
                        .queryParam("format", "json")
                        .queryParam("limit", 1)
                        .path("/search")
                        .build()
                )
                .header("User-Agent", "demo-app")
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if (resp == null || resp.isEmpty()) {
            throw new RuntimeException("Location not found: " + query);
        }

        Map<String, Object> place = resp.get(0);
        double lat = Double.parseDouble((String) place.get("lat"));
        double lon = Double.parseDouble((String) place.get("lon"));

        return new double[]{lat, lon};
    }
}
