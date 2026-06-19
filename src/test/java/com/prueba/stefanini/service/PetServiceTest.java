package com.prueba.stefanini.service;

import com.prueba.stefanini.client.PetClient;
import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.exception.PetNotFoundException;
import com.prueba.stefanini.mapper.PetMapper;
import com.prueba.stefanini.model.Pet;
import com.prueba.stefanini.service.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetServiceImpl - Tests unitarios")
class PetServiceTest {

    @Mock
    private PetClient petClient;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetServiceImpl petService;

    private Pet pet;
    private PetRequest petRequest;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("doggie");
        pet.setStatus("available");

        petRequest = new PetRequest();
        petRequest.setId(1L);
        petRequest.setName("doggie");
        petRequest.setStatus("available");
    }

    @Test
    @DisplayName("getPetById - debe retornar GetPetResponse cuando la mascota existe")
    void getPetById_shouldReturnGetPetResponse_whenPetExists() {
        GetPetResponse expected = GetPetResponse.builder().id(1L).name("doggie").status("available").build();
        when(petClient.getPetById(1L)).thenReturn(pet);
        when(petMapper.toGetPetResponse(pet)).thenReturn(expected);

        GetPetResponse response = petService.getPetById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("doggie");
        verify(petClient).getPetById(1L);
        verify(petMapper).toGetPetResponse(pet);
    }

    @Test
    @DisplayName("getPetById - debe lanzar PetNotFoundException cuando la API retorna 404")
    void getPetById_shouldThrowPetNotFoundException_whenApiReturns404() {
        when(petClient.getPetById(99L))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> petService.getPetById(99L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("addPet - debe retornar PetResponse con transactionId consistente")
    void addPet_shouldReturnPetResponse_withConsistentTransactionId() {
        PetResponse mappedResponse = PetResponse.builder()
                .transactionId("placeholder")
                .dateCreated(OffsetDateTime.now())
                .name("doggie")
                .status("available")
                .build();
        when(petMapper.toPet(petRequest)).thenReturn(pet);
        when(petClient.addPet(pet)).thenReturn(pet);
        when(petMapper.toPetResponse(pet)).thenReturn(mappedResponse);

        PetResponse response = petService.addPet(petRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTransactionId()).matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
        verify(petMapper).toPet(petRequest);
        verify(petClient).addPet(pet);
        verify(petMapper).toPetResponse(pet);
    }

    @Test
    @DisplayName("addPet - debe propagar la excepcion cuando el cliente falla")
    void addPet_shouldPropagateException_whenClientFails() {
        when(petMapper.toPet(any())).thenReturn(pet);
        when(petClient.addPet(any())).thenThrow(new RuntimeException("Error al crear pet"));

        assertThatThrownBy(() -> petService.addPet(petRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al crear pet");
    }
}
