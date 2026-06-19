package com.prueba.stefanini.exception;

/**
 * Excepcion lanzada cuando una mascota no es encontrada en la API externa.
 *
 * <p>Se utiliza para desacoplar el manejo del error 404 de la API externa
 * del resto de errores de cliente, permitiendo un mensaje claro y controlado.</p>
 */
public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(Long petId) {
        super("No se encontro la mascota con id: " + petId);
    }
}
