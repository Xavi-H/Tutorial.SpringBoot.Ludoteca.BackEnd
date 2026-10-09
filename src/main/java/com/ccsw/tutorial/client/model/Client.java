package com.ccsw.tutorial.client.model;

import jakarta.persistence.*;

/**
 * @author Xavi-H
 *
 */
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true) // Extra protecció, no permet clients duplicats
    private String name;

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
