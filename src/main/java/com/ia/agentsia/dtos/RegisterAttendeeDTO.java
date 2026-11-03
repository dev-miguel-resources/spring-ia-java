package com.ia.agentsia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterAttendeeDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "El teléfono celular es obligatorio")
    @Pattern(regexp = "^(\\+?56)?(9\\d{8})$", message = "El teléfono debe ser un número celular chileno válido (ej: 912345678 u opcional con +56)")
    private String phone;

}
