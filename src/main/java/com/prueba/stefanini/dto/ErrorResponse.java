package com.prueba.stefanini.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private int status;
    private String error;
    private List<String> messages;
    private OffsetDateTime timestamp;
}
