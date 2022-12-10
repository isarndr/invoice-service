package com.codewithisa.invoiceservice.controller;

import com.codewithisa.invoiceservice.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    InvoiceService invoiceService;

    @PostMapping("/pesan-tiket")
    public ResponseEntity<String> pesanTiket(@RequestParam("scheduleId") Long scheduleId,
                                             @RequestParam("nomorKursi") String nomorKursi){
        log.info("Inside pesanTiket of InvoiceController");
        invoiceService.pesanTiket(scheduleId, nomorKursi);
        return new ResponseEntity<>("Tiket berhasil dipesan", HttpStatus.OK);
    }
}
