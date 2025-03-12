package com.example.demo.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.model.GoldPriceEntity;

public interface GoldPriceRepository extends JpaRepository<GoldPriceEntity, Long> {
  
}
