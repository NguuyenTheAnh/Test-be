package com.demo.rentalfinder.service;

import com.demo.rentalfinder.dto.DistanceResponse;
import java.util.List;

public interface RoutingService {
    List<DistanceResponse> findShortestRoutes(String schoolName);
}