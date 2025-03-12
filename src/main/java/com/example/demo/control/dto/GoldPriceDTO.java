package com.example.demo.control.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoldPriceDTO {
  private BigDecimal soldPrice;
  private BigDecimal boughPrice;
  private LocalDateTime createdDateTime;

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
  }
}
