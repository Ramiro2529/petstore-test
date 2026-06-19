package com.prueba.stefanini.client;

import com.prueba.stefanini.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetClient - Tests unitarios")
class PetClientTest {

    @Mock
    private RestTemplate petstoreRestTemplate;

    @InjectMocks
    private PetClient petClient;

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1L);
        pet.setName("doggie");
        pet.setStatus("available");
    }

    @Test
    @DisplayName("getPetById - debe retornar la mascota cuando la API responde correctamente")
    void getPetById_shouldReturnPet_whenApiRespondsSuccessfully() {
        when(petstoreRestTemplate.getForObject("/pet/{petId}", Pet.class, 1L)).thenReturn(pet);

        Pet result = petClient.getPetById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("doggie");
        assertThat(result.getStatus()).isEqualTo("available");
        verify(petstoreRestTemplate).getForObject("/pet/{petId}", Pet.class, 1L);
    }

    @Test
    @DisplayName("addPet - debe retornar la mascota creada cuando la API responde correctamente")
    void addPet_shouldReturnCreatedPet_whenApiRespondsSuccessfully() {
        when(petstoreRestTemplate.postForObject(eq("/pet"), any(HttpEntity.class), eq(Pet.class)))
                .thenReturn(pet);

        Pet result = petClient.addPet(pet);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("doggie");
        assertThat(result.getStatus()).isEqualTo("available");
        verify(petstoreRestTemplate).postForObject(eq("/pet"), any(HttpEntity.class), eq(Pet.class));
    }
}
