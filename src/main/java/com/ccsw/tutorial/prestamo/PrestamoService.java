package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.prestamo.model.Prestamo;
import com.ccsw.tutorial.prestamo.model.PrestamoSearchDto;
import org.springframework.data.domain.Page;

public interface PrestamoService {

    Page<Prestamo> findPage(PrestamoSearchDto dto);
}