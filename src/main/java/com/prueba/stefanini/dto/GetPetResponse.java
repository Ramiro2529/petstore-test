package com.prueba.stefanini.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPetResponse {

    private Long id;
    private String name;
    private String status;
}
