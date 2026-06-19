package com.prueba.stefanini.mapper;

import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.model.Pet;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Componente responsable de la transformacion entre el modelo interno {@link Pet}
 * y los DTOs de entrada/salida del dominio de mascotas.
 *
 * <p>Centraliza el mapeo para que el servicio no contenga logica de construccion
 * de objetos y sea mas facil de testear y mantener.</p>
 */
@Component
public class PetMapper {

    /**
     * Convierte un {@link Pet} al DTO de respuesta del endpoint GET.
     *
     * @param pet modelo interno con los datos de la mascota
     * @return {@link GetPetResponse} con id, name y status
     */
    public GetPetResponse toGetPetResponse(Pet pet) {
        return GetPetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .status(pet.getStatus())
                .build();
    }

    /**
     * Convierte un {@link Pet} al DTO de respuesta del endpoint POST,
     * generando un nuevo transactionId (UUIDv4) y la fecha actual del sistema.
     *
     * @param pet modelo interno con los datos de la mascota registrada
     * @return {@link PetResponse} con transactionId, dateCreated, name y status
     */
    public PetResponse toPetResponse(Pet pet) {
        return PetResponse.builder()
                .transactionId(UUID.randomUUID().toString())
                .dateCreated(OffsetDateTime.now())
                .status(pet.getStatus())
                .name(pet.getName())
                .build();
    }

    /**
     * Convierte un {@link PetRequest} al modelo interno {@link Pet}.
     *
     * @param request DTO de entrada con los datos enviados por el cliente
     * @return {@link Pet} listo para ser enviado a la API externa
     */
    public Pet toPet(PetRequest request) {
        Pet pet = new Pet();
        pet.setId(request.getId());
        pet.setName(request.getName());
        pet.setStatus(request.getStatus());
        return pet;
    }
}
