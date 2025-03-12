package com.example.demo.control.goldprice.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.example.demo.control.dto.GoldPriceDTO;
import com.example.demo.entity.model.GoldPriceEntity;
import com.example.demo.entity.repository.GoldPriceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;

@Component
public class GoldPriceMessageConsumer {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private GoldPriceRepository goldPriceRepository;
  ObjectMapper mapper;

  @PostConstruct
  public void init() {
    this.mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
  }


  @KafkaListener(topics = "gold-price", groupId = "test-consumer-group")
  public void publish(@Payload String message,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) throws JsonProcessingException {
    logger.info("Received partition in group: {}", partition);
    logger.info("Received Message in group: {}", message);

    var dto = this.mapper.readValue(message, GoldPriceDTO.class);
    var entity = new GoldPriceEntity();
    entity.setBoughPrice(dto.getBoughPrice());
    entity.setSoldPrice(dto.getSoldPrice());

    this.goldPriceRepository.save(entity);

  }
}
