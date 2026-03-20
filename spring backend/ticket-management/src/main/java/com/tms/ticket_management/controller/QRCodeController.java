package com.tms.ticket_management.controller;

import com.tms.ticket_management.dto.TicketDTO;
import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.service.QRCodeService;
import com.tms.ticket_management.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRCodeController {

    private final QRCodeService qrCodeService;
    private final TicketService ticketService;

    @GetMapping("/generate/{ticketId}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId).map(ticket -> {
            try {
                byte[] qrImage = qrCodeService.generateQRCodeImage(ticket.getQrCode());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"ticket-" + ticketId + ".png\"")
                        .body(qrImage);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/verify/{qrCode}")
    public ResponseEntity<TicketDTO> verifyQRCode(@PathVariable String qrCode) {
        return ticketService.getTicketByQRCode(qrCode)
                .map(TicketDTO::fromTicket)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/scan/{qrCode}")
    public ResponseEntity<TicketDTO> scanQRCode(@PathVariable String qrCode) {
        Ticket ticket = ticketService.markTicketAsUsed(qrCode);
        return ResponseEntity.ok(TicketDTO.fromTicket(ticket));
    }
}

