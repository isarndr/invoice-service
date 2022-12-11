package com.codewithisa.invoiceservice.controller;

import com.codewithisa.invoiceservice.VO.Films;
import com.codewithisa.invoiceservice.VO.Schedules;
import com.codewithisa.invoiceservice.VO.Seats;
import com.codewithisa.invoiceservice.VO.Users;
import com.codewithisa.invoiceservice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    InvoiceService invoiceService;
    @Autowired
    RestTemplate restTemplate;

//    @PostMapping("/pesan-tiket")
    public ResponseEntity<String> pesanTiket(@RequestParam("scheduleId") Long scheduleId,
                                             @RequestParam("nomorKursi") String nomorKursi){
        log.info("Inside pesanTiket of InvoiceController");
        invoiceService.pesanTiket(scheduleId, nomorKursi);
        return new ResponseEntity<>("Tiket berhasil dipesan", HttpStatus.OK);
    }

    @Operation(
            summary = "untuk membuat tiket dalam bentuk pdf. tiket hanya akan dibuat jika dan hanya jika " +
                    "pemesanan sukses (tiket tersedia sebelum dipesan)"
    )
    @PostMapping("generate-tiket")
    public void generateTiket(HttpServletResponse response,
                              @Schema(example = "Nemo") @RequestParam("filmName") String filmName,
                              @Schema(example = "18 November 2022") @RequestParam("tanggalTayang") String tanggalTayang,
                              @Schema(example = "20.00 WIB") @RequestParam("jamMulai") String jamMulai,
                              @Schema(example = "A3") @RequestParam("nomorKursi") String nomorKursi,
                              @Schema(example = "isarndr") @RequestParam("username") String username)
            throws IOException {
        log.info("Inside generateTiket of InvoiceController");
        // cek username
        Users user=null;
        try {
            user=restTemplate.getForObject(
                    "http://localhost:9001/users/get-user-by-username/" + username,
                    Users.class
            );
        } catch (Exception e) {

        }
        if(user==null){
            log.error("username is not found");
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // ambil nama studio dari input nomorKursi
        Character studioName = nomorKursi.charAt(0);

        // ambil filmCode dari input filmName
        Long filmCode=null;
        Films film = null;
        try {
            film = restTemplate.getForObject(
                    "http://localhost:9002/films/find-film-by-film-name/" + filmName, Films.class);
            filmCode=film.getFilmCode();
        } catch (Exception e) {

        }

        if(filmCode==null){
            log.error("film doesn't exist");
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return;
        }

        if(!film.isSedangTayang()){
            log.error("film sedang tidak tayang");
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // find schedules berdasarkan kriteria input endpoint
        Schedules schedules = null;

        try {
            schedules=restTemplate.getForObject(
                    "http://localhost:9003/schedules/find-schedule-by-all/?jamMulai=" +
                            jamMulai + "&studioName=" + studioName + "&tanggalTayang=" + tanggalTayang + "&filmCode="
                    + filmCode,
                    Schedules.class
            );
        } catch (Exception e) {

        }
        if(schedules== null){
            log.error("Schedule doesn't exist");
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // pesanTiket preparation

        Long scheduleId = schedules.getScheduleId();

        Seats seat=null;
        try {
            seat=restTemplate.getForObject(
                "http://localhost:9004/seats/find-seat-by-schedule-id-and-nomor-kursi/?scheduleId="+scheduleId+
                        "&nomorKursi="+nomorKursi,
                    Seats.class
            );
        } catch (Exception e) {

        }
        if(seat==null){
            log.error("seats isn't available");
            response.sendError(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // at this line kursi tersedia, film sedang tayang dan film ada di database, so masuk ke method pesanTiket

        try {
            pesanTiket(scheduleId,nomorKursi);
        } catch (Exception e) {

        }

        try{
            JasperReport sourceFileName = JasperCompileManager.compileReport(
                    ResourceUtils.getFile("src/main/resources/tiket.jrxml").getAbsolutePath()
            );
            List<Map<String,String>> dataPemesan = new ArrayList<>();
            Map<String, String> pemesan = new HashMap<>();
            pemesan.put("filmName", filmName);
            pemesan.put("tanggalTayang", tanggalTayang);
            pemesan.put("jamMulai", jamMulai);
            pemesan.put("nomorKursi", nomorKursi);
            pemesan.put("username", username);
            dataPemesan.add(pemesan);

            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataPemesan);
            Map<String,Object> parameters = new HashMap<>();

            JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, parameters, beanColDataSource);

            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=tiket.pdf;");

            log.info("Seat successfully ordered");
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());


        }
        catch (Exception e){
            response.sendError(HttpStatus.BAD_REQUEST.value());
            log.error("error in generating invoice {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
