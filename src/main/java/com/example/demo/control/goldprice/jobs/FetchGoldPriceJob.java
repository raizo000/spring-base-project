package com.example.demo.control.goldprice.jobs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.control.dto.GoldPriceDTO;
import com.example.demo.control.goldprice.event.GoldPriceEvent;


@Component
public class FetchGoldPriceJob {

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  @Scheduled(fixedDelay = 30000)
  public void runJob() throws Exception {

    var symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator('.');
    String pattern = "0.00";
    DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
    decimalFormat.setParseBigDecimal(true);

    var doc = Jsoup.connect("https://ngoctham.com/bang-gia-vang").get();
    var goldTable = doc.select(".city-0");
    var boughtPrice =
        (BigDecimal) decimalFormat.parse(goldTable.select("tr").get(1).select("td").get(1).text().replace(".", ""));
    var soldPrice =
        (BigDecimal) decimalFormat.parse(goldTable.select("tr").get(1).select("td").get(2).text().replace(".", ""));

    var goldPrice = GoldPriceDTO.builder().boughPrice(boughtPrice).soldPrice(soldPrice)
        .createdDateTime(LocalDateTime.now()).build();


    var event = new GoldPriceEvent(goldPrice);
    applicationEventPublisher.publishEvent(event);
  }

}
