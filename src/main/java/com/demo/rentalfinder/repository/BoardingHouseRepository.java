package com.demo.rentalfinder.repository;

import com.demo.rentalfinder.entity.BoardingHouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardingHouseRepository extends JpaRepository<BoardingHouse, Long> {
}
