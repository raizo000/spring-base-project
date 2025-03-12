package com.example.demo.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class OutboxEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "event_type")
  private String eventType; // e.g., "OrderCreated"
  @Column(name = "payload")
  private String payload; // JSON payload of the event
  private boolean processed = false; // Marks if the message has been sent
}
