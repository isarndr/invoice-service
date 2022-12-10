package com.codewithisa.invoiceservice.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seats {
    private Character studioName;
    private String nomorKursi;
    private Long scheduleId;
}

