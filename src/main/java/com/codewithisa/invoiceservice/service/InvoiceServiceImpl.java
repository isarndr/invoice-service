package com.codewithisa.invoiceservice.service;

import com.codewithisa.invoiceservice.VO.Seat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService{
    @Autowired
    RestTemplate restTemplate;

    @Value("${getSeatByScheduleIdAndNomorKursi}")
    private String getSeatByScheduleIdAndNomorKursi;

    @Value("${delSeatByScheduleIdAndNomorKursi}")
    private String delSeatByScheduleIdAndNomorKursi;

    @Override
    public void pesanTiket(Long scheduleId, String nomorKursi) {
        Seat seatList = restTemplate.getForObject(
                getSeatByScheduleIdAndNomorKursi + scheduleId + "&nomorKursi=" + nomorKursi,
                Seat.class);
        restTemplate.delete(delSeatByScheduleIdAndNomorKursi + scheduleId + "&nomorKursi=" + nomorKursi);
        log.info("seat successfully ordered");
    }
}
