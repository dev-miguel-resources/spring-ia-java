package com.ia.agentsia.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrGenerator {

    private QrGenerator() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no puede ser instanciada");
    }

    // Generar un QR en PNG codificado en base64
    // Ideal para mostrar en etiquetas html o en respuestas JSON
    public static String generateQrImageBase64(String content, int width, int height) {
        try {
            byte[] pngData = generateQrImagePng(content, width, height);

            return Base64.getEncoder().encodeToString(pngData);

        } catch (Exception e) {
            throw new RuntimeException("Fallo en el motor gráfico de renderizado del códig QR", e);
        }
    }

    // Generar un QR Code (byte[]) para descargarlo como archivo o procesarlo en
    // ciertos flujos
    public static byte[] generateQrImagePng(String content, int width, int height) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("El contenido para generar el código QR no puede estar vacío");
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            try (ByteArrayOutputStream pngArrayOutputStream = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngArrayOutputStream);

                // Devuelva un arreglo de bytes limpio del PNG Generado
                return pngArrayOutputStream.toByteArray();
            }
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Fallo en el motor gráfico de renderizado del códig QR", e);
        }
    }

}
