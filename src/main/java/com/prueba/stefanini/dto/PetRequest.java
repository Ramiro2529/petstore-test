package com.prueba.stefanini.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PetRequest {

    @NotNull(message = "El campo id es obligatorio")
    @Positive(message = "El campo id debe ser un numero positivo")
    private Long id;

    @NotBlank(message = "El campo status es obligatorio")
    @Pattern(
        regexp = "available|pending|sold",
        message = "El campo status debe ser: available, pending o sold"
    )
    private String status;

    @NotBlank(message = "El campo name es obligatorio")
    private String name;
}
