package com.ia.agentsia.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ia.agentsia.dtos.RegisterAttendeeDTO;
import com.ia.agentsia.models.Attendee;
import com.ia.agentsia.services.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class QREventController {

    private final EventService service;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterAttendeeDTO request) {

        Attendee saved = service.register(request.getName(), request.getEmail(), request.getPhone());
        String qrImageBase64 = service.buildQrImageBase64For(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "qrCode", saved.getQrCode(),
                "qrImageBase64", "data:image/png;base64," + qrImageBase64));

    }

    @GetMapping("/scan/{qrCode}")
    public ResponseEntity<Attendee> scan(@PathVariable String qrCode) {
        Attendee attendee = service.findByQr(qrCode);
        return ResponseEntity.ok(attendee);
    }

    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<byte[]> downloadQr(@PathVariable String qrCode) {
        Attendee attendee = service.findByQr(qrCode);
        byte[] pngData = service.buildQrImagePngFor(attendee);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qr_" + attendee.getId() + ".png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(pngData);
    }

}
