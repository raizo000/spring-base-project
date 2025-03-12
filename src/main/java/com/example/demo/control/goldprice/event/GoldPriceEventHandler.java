package com.example.demo.control.goldprice.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.example.demo.entity.model.OutboxEntity;
import com.example.demo.entity.repository.OutboxRepository;
import jakarta.transaction.Transactional;

@Component
public class GoldPriceEventHandler {

  @Autowired
  private OutboxRepository outboxRepository;

  @EventListener
  @Transactional
  public void handleGoldPriceEvent(GoldPriceEvent event) {
    var entity = new OutboxEntity();
    entity.setEventType("goldPriceEvent");
    entity.setPayload(event.getGoldPrice().toString());
    entity.setProcessed(false);
    outboxRepository.save(entity);
  }
}
