package com.ccsw.tutorial.client.model;

/**
 * Modelo de datos de clientes
 * @author Xavi-H
 */
public class ClientDto {

    // Atributos

    private Long id;
    private String name;

    // Getters

    /**
     * @return id del client
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @return nom del client
     */
    public String getName() {
        return this.name;
    }

    // Setters

    /**
     * @param id new value of {@link #getId}.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param name new value of {@link #getName}.
     */
    public void setName(String name) {
        this.name = name;
    }
}