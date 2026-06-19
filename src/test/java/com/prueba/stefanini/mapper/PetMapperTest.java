package com.prueba.stefanini.mapper;

import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PetMapper - Tests unitarios")
class PetMapperTest {

    private PetMapper petMapper;
    private Pet pet;

    @BeforeEach
    void setUp() {
        petMapper = new PetMapper();
        pet = new Pet();
        pet.setId(1L);
        pet.setName("doggie");
        pet.setStatus("available");
    }

    @Test
    @DisplayName("toGetPetResponse - debe mapear id, name y status del modelo al DTO")
    void toGetPetResponse_shouldMapAllFields() {
        GetPetResponse response = petMapper.toGetPetResponse(pet);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("doggie");
        assertThat(response.getStatus()).isEqualTo("available");
    }

    @Test
    @DisplayName("toPetResponse - debe generar transactionId en formato UUIDv4 y dateCreated con fecha actual")
    void toPetResponse_shouldGenerateTransactionIdAndDateCreated() {
        OffsetDateTime before = OffsetDateTime.now();
        PetResponse response = petMapper.toPetResponse(pet);
        OffsetDateTime after = OffsetDateTime.now();

        assertThat(response.getTransactionId()).matches(
                "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
        assertThat(response.getDateCreated()).isAfterOrEqualTo(before);
        assertThat(response.getDateCreated()).isBeforeOrEqualTo(after);
        assertThat(response.getName()).isEqualTo("doggie");
        assertThat(response.getStatus()).isEqualTo("available");
    }

    @Test
    @DisplayName("toPet - debe mapear id, name y status del request al modelo Pet")
    void toPet_shouldMapAllFields() {
        PetRequest request = new PetRequest();
        request.setId(1L);
        request.setName("doggie");
        request.setStatus("available");

        Pet result = petMapper.toPet(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("doggie");
        assertThat(result.getStatus()).isEqualTo("available");
    }
}
