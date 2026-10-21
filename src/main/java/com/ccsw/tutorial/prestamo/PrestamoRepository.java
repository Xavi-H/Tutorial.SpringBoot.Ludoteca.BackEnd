package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.prestamo.model.Prestamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Xavi-H
 */
public interface PrestamoRepository extends CrudRepository<Prestamo, Long>, JpaSpecificationExecutor<Prestamo> {

    /**
     * Por defecto Hibernate carga las relaciones (juego y cliente) con consultas
     * adicionales (problema "N+1").
     * EntityGraph indica qué traiga las relaciones en la misma consulta,
     * mediante JOIN por lo que es mas optimo (evita consultas innecesarias)
     *
     * @param pageable datos de paginación (página, tamaño y orden)
     * @return {@link Page} de {@link Prestamo}
     */
    @Override
    @EntityGraph(attributePaths = { "game", "game.category", "game.author", "client" })

    Page<Prestamo> findAll(Specification<Prestamo> spec, Pageable pageable);

    // Consulta per obtenir els prestams d'un joc en un rang determinat
    @Query("SELECT p FROM Prestamo p WHERE p.game.id = :gameId AND p.startDate <= :endDate AND p.endDate >= :startDate")
    List<Prestamo> findPrestmasGameRang(@Param("gameId") Long gameId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Consulta per obtenir els prestams d'un client en un rang determinat
    @Query("SELECT p FROM Prestamo p WHERE p.client.id = :clientId AND p.startDate <= :endDate AND p.endDate >= :startDate")
    List<Prestamo> findPrestamsClientRang(@Param("clientId") Long clientId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}