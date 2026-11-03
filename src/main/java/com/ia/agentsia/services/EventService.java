package com.ia.agentsia.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ia.agentsia.exceptions.AttendeeNotFoundException;
import com.ia.agentsia.exceptions.EmailAlreadyExistsException;
import com.ia.agentsia.models.Attendee;
import com.ia.agentsia.repos.AttendeeRepo;
import com.ia.agentsia.utils.QrGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final AttendeeRepo repo;

    @Value("${app.qr.base-url}")
    private String qrBaseUrl;

    public Attendee register(String name, String email, String rawPhone) {

        if (repo.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Ya existe un registro con el email: " + email);
        }

        // Ej: Si el usuario escribió +56 9 1234-5678, para dejarlo puro: 56912345678
        String cleanPhone = rawPhone.replaceAll("[^0-9]", "");

        if (cleanPhone.length() > 9) {
            cleanPhone = cleanPhone.substring(cleanPhone.length() - 9);
        }

        String formattedPhone = "+56" + cleanPhone;

        String qrCode = UUID.randomUUID().toString();

        Attendee attendee = Attendee.builder()
                .name(name)
                .email(email)
                .phone(formattedPhone) // formato limpio y unificado
                .qrCode(qrCode)
                .build();

        return repo.save(attendee);
    }

    public String buildQrImageBase64For(Attendee attendee) {
        String targetUrl = buildTargetUrl(attendee.getQrCode());
        return QrGenerator.generateQrImageBase64(targetUrl, 400, 400);
    }

    public byte[] buildQrImagePngFor(Attendee attendee) {
        try {
            String targetUrl = buildTargetUrl(attendee.getQrCode());
            return QrGenerator.generateQrImagePng(targetUrl, 400, 400);
        } catch (Exception ex) {
            throw new RuntimeException("Error crítico al procesar el renderizado del archivo QR en PNG", ex);
        }
    }

    public Attendee findByQr(String qrCode) {
        return repo.findByQrCode(qrCode)
                .orElseThrow(
                        () -> new AttendeeNotFoundException("El código proporcionado no es válido o no existe"));
    }

    private String buildTargetUrl(String qrCode) {
        return qrBaseUrl.endsWith("/") ? qrBaseUrl + qrCode : qrBaseUrl + "/" + qrCode;
    }

}
