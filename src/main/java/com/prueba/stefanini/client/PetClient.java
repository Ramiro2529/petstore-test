package com.prueba.stefanini.client;

import com.prueba.stefanini.model.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP para la integracion con la API externa Petstore.
 *
 * <p>Encapsula las llamadas al servicio remoto {@code https://petstore.swagger.io/v2}
 * utilizando {@link RestTemplate}. Cada metodo corresponde a un endpoint
 * especifico de la API.</p>
 */
@Component
@RequiredArgsConstructor
public class PetClient {

    private final RestTemplate petstoreRestTemplate;

    /**
     * Consulta una mascota en la API externa Petstore por su identificador.
     *
     * @param petId identificador unico de la mascota a consultar
     * @return objeto {@link Pet} con la informacion retornada por la API externa,
     *         o {@code null} si la respuesta no contiene cuerpo
     */
    public Pet getPetById(Long petId) {
        return petstoreRestTemplate.getForObject("/pet/{petId}", Pet.class, petId);
    }

    /**
     * Registra una nueva mascota en la API externa Petstore.
     *
     * @param pet objeto {@link Pet} con los datos de la mascota a registrar
     * @return objeto {@link Pet} con la informacion de la mascota creada,
     *         incluyendo el id asignado por la API externa
     */
    public Pet addPet(Pet pet) {
        return petstoreRestTemplate.postForObject("/pet", new HttpEntity<>(pet), Pet.class);
    }
}
