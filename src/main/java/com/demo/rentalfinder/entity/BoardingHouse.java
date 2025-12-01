package com.demo.rentalfinder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boarding_house")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BoardingHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    private Double lat;
    private Double lng;
}
