package com.example.demo.entity.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Collate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gold_price")
@Getter
@Setter
public class GoldPriceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "bough_price")
  private BigDecimal boughPrice;

  @Column(name = "sold_price")
  private BigDecimal soldPrice;

  @Column(name = "created_date")
  @CreationTimestamp
  private LocalDateTime createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private LocalDateTime updatedDate;
}
