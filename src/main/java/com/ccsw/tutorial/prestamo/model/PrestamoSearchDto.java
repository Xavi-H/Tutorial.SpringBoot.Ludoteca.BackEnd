package com.ccsw.tutorial.prestamo.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;
import java.time.LocalDate;

/**
 * @author Xavi-H
 */
public class PrestamoSearchDto {

    private PageableRequest pageable;
    private Long idGame;
    private Long idClient;
    private LocalDate date;

    /**
     * @return pageable
     */
    public PageableRequest getPageable() {
        return pageable;
    }

    /**
     * @param pageable new value of {@link #getPageable}.
     */
    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }

    /**
     * @return idGame
     */
    public Long getIdGame() {
        return idGame;
    }

    /**
     * @param idGame new value of {@link #getIdGame}.
     */
    public void setIdGame(Long idGame) {
        this.idGame = idGame;
    }

    /**
     * @return idClient
     */
    public Long getIdClient() {
        return idClient;
    }

    /**
     * @param idClient new value of {@link #getIdClient}.
     */
    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    /**
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date new value of {@link #getDate}.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
}