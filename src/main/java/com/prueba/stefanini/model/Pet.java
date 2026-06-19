package com.prueba.stefanini.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {

    private Long id;
    private Category category;
    private String name;

    /** available, pending, sold */
    private String status;
}
