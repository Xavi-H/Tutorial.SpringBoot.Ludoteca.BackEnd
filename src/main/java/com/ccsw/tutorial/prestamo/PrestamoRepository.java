package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.prestamo.model.Prestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Xavi-H
 */
public interface PrestamoRepository extends CrudRepository<Prestamo, Long> {

    /**
     * Por defecto Hibernate carga las relaciones (juego y cliente) con consultas
     * adicionales (problema "N+1").
     * EntityGraph indica qué traiga las relaciones en la misma consulta,
     * mediante JOIN por lo que es mas optimo (evita consultas innecesarias)
     *
     * @param pageable datos de paginación (página, tamaño y orden)
     * @return {@link Page} de {@link Prestamo}
     */
    @EntityGraph(attributePaths = { "game", "game.category", "game.author", "client" })

    Page<Prestamo> findAll(Pageable pageable);
}