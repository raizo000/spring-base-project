package com.example.demo.control.goldprice.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.demo.control.goldprice.message.GoldPriceMessageProducer;
import com.example.demo.entity.repository.OutboxRepository;

@Component
public class GoldPricePoller {

  @Autowired
  private OutboxRepository outboxRepository;

  @Autowired
  private GoldPriceMessageProducer messageProducer;


  @Scheduled(fixedDelay = 8000)
  public void processOutbox() {
    var entities = outboxRepository.findAllByProcessedFalse();

    for (var entity : entities) {
      entity.setProcessed(true);
      messageProducer.publish(entity.getPayload());
      outboxRepository.save(entity);
    }
  }
}
