package com.codewithisa.invoiceservice.service;

import com.codewithisa.invoiceservice.VO.Seats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService{
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void pesanTiket(Long scheduleId, String nomorKursi) {
        log.info("Inside pesanTiket of SeatServiceImpl");
        Seats seatsList = restTemplate.getForObject(
                "http://localhost:9004/seats/find-seat-by-schedule-id-and-nomor-kursi/?scheduleId=" + scheduleId +
                        "&nomorKursi=" + nomorKursi,
                Seats.class);
        restTemplate.delete("http://localhost:9004/seats/delete-seat-by-schedule-id-and-nomor-kursi/?scheduleId=" + scheduleId + "&nomorKursi=" + nomorKursi);
        log.info("seat successfully ordered");
    }
}
