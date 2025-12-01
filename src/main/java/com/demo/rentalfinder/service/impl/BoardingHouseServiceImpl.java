package com.demo.rentalfinder.service.impl;

import com.demo.rentalfinder.entity.BoardingHouse;
import com.demo.rentalfinder.repository.BoardingHouseRepository;
import com.demo.rentalfinder.service.BoardingHouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardingHouseServiceImpl implements BoardingHouseService {

    private final BoardingHouseRepository repository;

    @Override
    public List<BoardingHouse> getAll() {
        return repository.findAll();
    }

    @Override
    public BoardingHouse save(BoardingHouse house) {
        return repository.save(house);
    }

    @Override
    public BoardingHouse getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("House not found"));
    }
}
