package com.demo.rentalfinder.service;

import com.demo.rentalfinder.entity.BoardingHouse;
import java.util.List;

public interface BoardingHouseService {
    List<BoardingHouse> getAll();
    BoardingHouse save(BoardingHouse house);
    BoardingHouse getById(Long id);
}
