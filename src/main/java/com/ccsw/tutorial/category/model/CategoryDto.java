package com.ccsw.tutorial.category.model;

/**
 * Modelo de datos de las categorias (capa de logica)
 * @author Xavi-H
 */
public class CategoryDto {

    // Atributos

    private Long id;
    private String name;

    // Getters

    /**
     * @return id de la categoria
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @return nom de la categoria
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
