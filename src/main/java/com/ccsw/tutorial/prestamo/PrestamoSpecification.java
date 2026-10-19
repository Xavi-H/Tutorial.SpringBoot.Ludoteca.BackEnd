package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.prestamo.model.Prestamo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PrestamoSpecification implements Specification<Prestamo> {

    private static final long serialVersionUID = 1L;

    private final SearchCriteria criteria;

    public PrestamoSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Prestamo> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        // Sense valor no filtra
        if (criteria.getValue() == null) {
            return null;
        }

        switch (criteria.getOperation()) {
        case ":":
            return builder.equal(this.<Object>getPath(root), criteria.getValue());
        case "<=":
            return builder.lessThanOrEqualTo(this.<LocalDate>getPath(root), (LocalDate) criteria.getValue());
        case ">=":
            return builder.greaterThanOrEqualTo(this.<LocalDate>getPath(root), (LocalDate) criteria.getValue());
        default:
            return null;
        }
    }

    // Retorna la ruta per fer la consulta
    @SuppressWarnings("unchecked")
    private <T> Path<T> getPath(Root<Prestamo> root) {
        String[] split = criteria.getKey().split("[.]");

        Path<?> path = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            path = path.get(split[i]);
        }

        return (Path<T>) path;
    }
}