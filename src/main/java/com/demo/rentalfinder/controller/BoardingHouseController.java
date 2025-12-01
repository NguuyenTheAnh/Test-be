package com.demo.rentalfinder.controller;

import com.demo.rentalfinder.entity.BoardingHouse;
import com.demo.rentalfinder.service.BoardingHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/houses")
@RequiredArgsConstructor
public class BoardingHouseController {

    private final BoardingHouseService service;

    @GetMapping
    public List<BoardingHouse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BoardingHouse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public BoardingHouse create(@RequestBody BoardingHouse house) {
        return service.save(house);
    }
}
