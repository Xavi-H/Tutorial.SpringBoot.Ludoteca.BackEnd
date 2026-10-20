package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.prestamo.model.Prestamo;
import com.ccsw.tutorial.prestamo.model.PrestamoDto;
import com.ccsw.tutorial.prestamo.model.PrestamoSearchDto;
import org.springframework.data.domain.Page;

public interface PrestamoService {

    Page<Prestamo> findPage(PrestamoSearchDto dto);

    Prestamo get(Long id);

    void save(Long id, PrestamoDto dto);

    void delete(Long id) throws Exception;
}