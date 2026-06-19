package com.prueba.stefanini.service;

import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.exception.PetNotFoundException;

/**
 * Contrato del servicio de negocio para la gestion de mascotas.
 *
 * <p>Define las operaciones disponibles del dominio de mascotas.
 * La implementacion concreta se encuentra en {@link PetServiceImpl}.</p>
 */
public interface PetService {

    /**
     * Consulta una mascota por su identificador y retorna sus datos basicos.
     *
     * @param petId identificador unico de la mascota a consultar
     * @return {@link GetPetResponse} con id, name y status de la mascota
     * @throws PetNotFoundException si no existe una mascota con el id indicado
     */
    GetPetResponse getPetById(Long petId);

    /**
     * Registra una nueva mascota y construye la respuesta estandarizada.
     *
     * @param request objeto {@link PetRequest} con los datos de entrada: id, name y status
     * @return {@link PetResponse} con transactionId (UUIDv4), dateCreated, name y status
     */
    PetResponse addPet(PetRequest request);
}
