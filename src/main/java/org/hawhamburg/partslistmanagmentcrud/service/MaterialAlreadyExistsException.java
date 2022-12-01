package org.hawhamburg.partslistmanagmentcrud.service;

public class MaterialAlreadyExistsException extends Exception {
    public MaterialAlreadyExistsException() {
        super("Material already exists");
    }
}
