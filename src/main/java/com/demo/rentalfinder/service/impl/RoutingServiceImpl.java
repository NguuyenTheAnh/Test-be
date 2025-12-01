package com.demo.rentalfinder.service.impl;

import com.demo.rentalfinder.dto.DistanceResponse;
import com.demo.rentalfinder.entity.BoardingHouse;
import com.demo.rentalfinder.repository.BoardingHouseRepository;
import com.demo.rentalfinder.service.GeocodingService;
import com.demo.rentalfinder.service.RoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoutingServiceImpl implements RoutingService {

    private final BoardingHouseRepository repository;
    private final GeocodingService geocodingService;

    @Value("${ors.api-key}")
    private String apiKey;

    private final WebClient client = WebClient.create("https://api.openrouteservice.org");

    @Override
    public List<DistanceResponse> findShortestRoutes(String schoolName) {

        double[] geo = geocodingService.getCoordinates(schoolName);
        double schoolLat = geo[0];
        double schoolLng = geo[1];

        List<BoardingHouse> houses = repository.findAll();
        List<DistanceResponse> result = new ArrayList<>();

        for (BoardingHouse house : houses) {
            RouteData routeData = getDistance(house.getLat(), house.getLng(), schoolLat, schoolLng);
            result.add(
                    DistanceResponse.builder()
                            .houseId(house.getId())
                            .houseName(house.getName())
                            .address(house.getAddress())
                            .distanceKm(routeData.getDistanceKm())
                            .geometry(routeData.getCoordinates())
                            .build()
            );
        }

        result.sort(Comparator.comparing(DistanceResponse::getDistanceKm));
        return result;
    }


    private RouteData getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {

        String body = String.format(
                "{\"coordinates\":[[%f,%f],[%f,%f]]}", lng1, lat1, lng2, lat2
        );

        Map<String, Object> response = client.post()
                .uri("/v2/directions/driving-car")
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null)
            return new RouteData(9999, Collections.emptyList());

        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        if (routes == null || routes.isEmpty())
            return new RouteData(9999, Collections.emptyList());

        Map<String, Object> route = routes.get(0);

        Map<String, Object> summary = (Map<String, Object>) route.get("summary");
        double meters = summary != null && summary.get("distance") != null
                ? ((Number) summary.get("distance")).doubleValue()
                : 9999000.0;

        Object geometryObj = route.get("geometry");
        List<List<Double>> coordinates = decodeGeometry(geometryObj);

        return new RouteData(meters / 1000.0, coordinates);
    }

    private List<List<Double>> decodeGeometry(Object geometryObj) {
        if (geometryObj == null)
            return Collections.emptyList();

        // Case 1: geometry = Map => GeoJSON coordinates
        if (geometryObj instanceof Map) {
            Map<String, Object> geo = (Map<String, Object>) geometryObj;
            if (geo.get("coordinates") instanceof List) {
                return (List<List<Double>>) geo.get("coordinates");
            }
        }

        // Case 2: geometry = encoded polyline string
        if (geometryObj instanceof String) {
            return decodePolylineToCoordinates((String) geometryObj);
        }

        return Collections.emptyList();
    }

    private List<List<Double>> decodePolylineToCoordinates(String encoded) {
        List<List<Double>> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double finalLat = lat / 1E5;
            double finalLng = lng / 1E5;
            poly.add(List.of(finalLng, finalLat));
        }
        return poly;
    }


    private static class RouteData {
        private final double distanceKm;
        private final List<List<Double>> coordinates;

        RouteData(double distanceKm, List<List<Double>> coordinates) {
            this.distanceKm = distanceKm;
            this.coordinates = coordinates;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }
    }
}
