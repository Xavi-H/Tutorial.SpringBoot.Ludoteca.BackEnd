package com.ccsw.tutorial.author.model;

/**
 * @author ccsw
 *
 */
public class AuthorDto {

    private Long id;
    private String name;
    private String nationality;

    // Getters

    /**
     * @return id
     */
    public Long getId() {

        return this.id;
    }

    /**
     * @return name
     */
    public String getName() {

        return this.name;
    }

    /**
     * @return nationality
     */
    public String getNationality() {

        return this.nationality;
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

    /**
     * @param nationality new value of {@link #getNationality}.
     */
    public void setNationality(String nationality) {

        this.nationality = nationality;
    }

}