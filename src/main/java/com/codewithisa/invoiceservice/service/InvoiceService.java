package com.codewithisa.invoiceservice.service;

import org.springframework.stereotype.Service;

@Service
public interface InvoiceService {
    void generateInvoice();
    void pesanTiket(Long scheduleId, String nomorKursi);
}
