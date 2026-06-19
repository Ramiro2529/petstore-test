package com.prueba.stefanini.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.exception.PetNotFoundException;
import com.prueba.stefanini.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
@DisplayName("PetController - Tests unitarios")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    private GetPetResponse getResponse;
    private PetResponse postResponse;

    @BeforeEach
    void setUp() {
        getResponse = GetPetResponse.builder()
                .id(1L)
                .name("doggie")
                .status("available")
                .build();

        postResponse = PetResponse.builder()
                .transactionId(UUID.randomUUID().toString())
                .dateCreated(OffsetDateTime.now())
                .status("available")
                .name("doggie")
                .build();
    }

    @Test
    @DisplayName("GET /api/pet/{petId} - debe retornar 200 con id, name y status")
    void getPetById_shouldReturn200_withIdNameAndStatus() throws Exception {
        when(petService.getPetById(1L)).thenReturn(getResponse);

        mockMvc.perform(get("/api/pet/{petId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("doggie"))
                .andExpect(jsonPath("$.status").value("available"));

        verify(petService).getPetById(1L);
    }

    @Test
    @DisplayName("GET /api/pet/{petId} - debe retornar 404 cuando la mascota no existe")
    void getPetById_shouldReturn404_whenPetNotFound() throws Exception {
        when(petService.getPetById(99L)).thenThrow(new PetNotFoundException(99L));

        mockMvc.perform(get("/api/pet/{petId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]").value("No se encontro la mascota con id: 99"));
    }

    @Test
    @DisplayName("GET /api/pet/{petId} - debe retornar 500 cuando el servicio lanza una excepcion generica")
    void getPetById_shouldReturn500_whenServiceThrowsGenericException() throws Exception {
        when(petService.getPetById(99L)).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/pet/{petId}", 99L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @Test
    @DisplayName("POST /api/pet - debe retornar 200 con PetResponse cuando el body es valido")
    void addPet_shouldReturn200_whenRequestBodyIsValid() throws Exception {
        PetRequest request = new PetRequest();
        request.setId(1L);
        request.setName("doggie");
        request.setStatus("available");

        when(petService.addPet(any(PetRequest.class))).thenReturn(postResponse);

        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.dateCreated").isNotEmpty())
                .andExpect(jsonPath("$.status").value("available"))
                .andExpect(jsonPath("$.name").value("doggie"));

        verify(petService).addPet(any(PetRequest.class));
    }

    @Test
    @DisplayName("POST /api/pet - debe retornar 400 cuando el campo id es nulo")
    void addPet_shouldReturn400_whenIdIsNull() throws Exception {
        PetRequest request = new PetRequest();
        request.setName("doggie");
        request.setStatus("available");

        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.messages[0]").value("'id': El campo id es obligatorio"));
    }

    @Test
    @DisplayName("POST /api/pet - debe retornar 400 cuando el body es un JSON malformado")
    void addPet_shouldReturn400_whenBodyIsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed Request"));
    }
}
