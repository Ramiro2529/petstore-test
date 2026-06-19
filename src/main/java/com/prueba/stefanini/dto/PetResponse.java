package com.prueba.stefanini.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class PetResponse {

    private String transactionId;
    private OffsetDateTime dateCreated;
    private String status;
    private String name;
}
