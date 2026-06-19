package com.prueba.stefanini.controller;

import com.prueba.stefanini.dto.GetPetResponse;
import com.prueba.stefanini.dto.PetRequest;
import com.prueba.stefanini.dto.PetResponse;
import com.prueba.stefanini.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestion de mascotas.
 *
 * <p>Expone los endpoints del dominio de mascotas y delega la logica
 * de negocio a {@link PetService}. La anotacion {@code @Validated} habilita
 * la validacion de parametros de ruta y query directamente en el controlador.</p>
 */
@Validated
@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /**
     * Consulta una mascota por su identificador unico.
     *
     * @param petId identificador numerico positivo de la mascota a consultar
     * @return {@link ResponseEntity} con {@link GetPetResponse} que contiene
     *         id, nombre y estado de la mascota
     */
    @GetMapping("/{petId}")
    public ResponseEntity<GetPetResponse> getPetById(
            @PathVariable @Positive(message = "El petId debe ser un numero positivo") Long petId) {
        return ResponseEntity.ok(petService.getPetById(petId));
    }

    /**
     * Registra una nueva mascota en el sistema.
     *
     * @param request objeto {@link PetRequest} con los datos de la mascota:
     *                id (obligatorio, positivo), name (obligatorio)
     *                y status (available | pending | sold)
     * @return {@link ResponseEntity} con {@link PetResponse} que contiene
     *         el transactionId generado, fecha de creacion, estado y nombre
     *         de la mascota registrada
     */
    @PostMapping
    public ResponseEntity<PetResponse> addPet(@Valid @RequestBody PetRequest request) {
        return ResponseEntity.ok(petService.addPet(request));
    }
}
