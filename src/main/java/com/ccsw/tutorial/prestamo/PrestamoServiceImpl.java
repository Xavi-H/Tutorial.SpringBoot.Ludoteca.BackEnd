package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.prestamo.model.Prestamo;
import com.ccsw.tutorial.prestamo.model.PrestamoSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    PrestamoRepository prestamoRepository;

    @Override
    public Page<Prestamo> findPage(PrestamoSearchDto dto) {

        PrestamoSpecification gameSpec = new PrestamoSpecification(new SearchCriteria("game.id", ":", dto.getIdGame()));
        PrestamoSpecification clientSpec = new PrestamoSpecification(new SearchCriteria("client.id", ":", dto.getIdClient()));
        PrestamoSpecification startSpec = new PrestamoSpecification(new SearchCriteria("startDate", "<=", dto.getDate()));
        PrestamoSpecification endSpec = new PrestamoSpecification(new SearchCriteria("endDate", ">=", dto.getDate()));

        Specification<Prestamo> spec = gameSpec.and(clientSpec).and(startSpec).and(endSpec);

        return this.prestamoRepository.findAll(spec, dto.getPageable().getPageable());
    }
}