package com.example.demo.control.goldprice.message;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GoldPriceMessageProducer {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private NewTopic goldPriceTopic;

  public void publish(String message) {
    try {
      kafkaTemplate.send(goldPriceTopic.name(), message);
    } catch (Exception e) {
      logger.error("Exception when sending kafka message", e);
    }
  }
}
