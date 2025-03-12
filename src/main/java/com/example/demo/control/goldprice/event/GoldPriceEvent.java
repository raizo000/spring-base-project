package com.example.demo.control.goldprice.event;

import com.example.demo.control.dto.GoldPriceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GoldPriceEvent {
  private GoldPriceDTO goldPrice;
}
