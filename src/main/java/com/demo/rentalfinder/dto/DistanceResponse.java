package com.demo.rentalfinder.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DistanceResponse {
    private Long houseId;
    private String houseName;
    private String address;
    private Double distanceKm;
    private List<List<Double>> geometry; // [ [lng, lat], [lng, lat], ... ]
}
