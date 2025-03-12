package com.example.demo.entity.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.model.OutboxEntity;


public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
  List<OutboxEntity> findAllByProcessedFalse();
}
