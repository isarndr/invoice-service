package com.codewithisa.invoiceservice.service;

import com.codewithisa.invoiceservice.VO.Seat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService{
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void pesanTiket(Long scheduleId, String nomorKursi) {
        Seat seatList = restTemplate.getForObject(
                "http://localhost:9004/seat/by-schedule-id-and-nomor-kursi?scheduleId=" + scheduleId +
                        "&nomorKursi=" + nomorKursi,
                Seat.class);
        restTemplate.delete("http://localhost:9004/seat/by-schedule-id-and-nomor-kursi?scheduleId=" + scheduleId + "&nomorKursi=" + nomorKursi);
        log.info("seat successfully ordered");
    }
}
