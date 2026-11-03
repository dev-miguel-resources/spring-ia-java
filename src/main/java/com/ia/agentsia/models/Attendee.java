package com.ia.agentsia.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class) // Habilitar auditoria de campos automática e inteligente
@Table(name = "attendees", indexes = {
        @Index(name = "idx_qr_code", columnList = "qr_code", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true)
})
public class Attendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato de email no es válido")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "El código QR es obligatorio")
    @Column(name = "qr_code", unique = true, nullable = false)
    private String qrCode;

    @CreatedDate // Spring pone la fecha y hora exacta del servidor te la guarda de manera
                 // automática
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
