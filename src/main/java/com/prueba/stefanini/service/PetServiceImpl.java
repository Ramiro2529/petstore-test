package com.prueba.stefanini.service;

import com.prueba.stefanini.client.PetClient;
import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.exception.PetNotFoundException;
import com.prueba.stefanini.mapper.PetMapper;
import com.prueba.stefanini.model.Pet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

/**
 * Implementacion concreta del contrato {@link PetService}.
 *
 * <p>Contiene la logica de negocio del dominio: invoca al {@link PetClient},
 * delega la transformacion al {@link PetMapper}, registra en consola los datos
 * relevantes y gestiona los errores de la API externa con excepciones propias.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private static final String MDC_TRANSACTION_ID = "transactionId";

    private final PetClient petClient;
    private final PetMapper petMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public GetPetResponse getPetById(Long petId) {
        try {
            Pet pet = petClient.getPetById(petId);
            log.info("Mascota consultada exitosamente - id: {}, name: {}, status: {}",
                    petId, pet.getName(), pet.getStatus());
            return petMapper.toGetPetResponse(pet);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new PetNotFoundException(petId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PetResponse addPet(PetRequest request) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(MDC_TRANSACTION_ID, transactionId);
        try {
            Pet pet = petMapper.toPet(request);
            Pet created = petClient.addPet(pet);
            log.info("Mascota registrada exitosamente - id: {}, name: {}, status: {}",
                    created.getId(), created.getName(), created.getStatus());
            PetResponse response = petMapper.toPetResponse(created);
            response.setTransactionId(transactionId);
            return response;
        } finally {
            MDC.remove(MDC_TRANSACTION_ID);
        }
    }
}
