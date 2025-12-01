package com.demo.rentalfinder.controller;

import com.demo.rentalfinder.dto.DistanceResponse;
import com.demo.rentalfinder.service.RoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RoutingController {

    private final RoutingService routingService;

    @GetMapping
    public List<DistanceResponse> findShortest(@RequestParam String schoolName) {
        return routingService.findShortestRoutes(schoolName);
    }
}
