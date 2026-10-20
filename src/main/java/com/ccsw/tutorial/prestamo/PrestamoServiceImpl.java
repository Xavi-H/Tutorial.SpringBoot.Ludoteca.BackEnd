package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.prestamo.model.Prestamo;
import com.ccsw.tutorial.prestamo.model.PrestamoDto;
import com.ccsw.tutorial.prestamo.model.PrestamoSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    PrestamoRepository prestamoRepository;

    @Autowired
    GameService gameService;

    @Autowired
    ClientService clientService;

    @Override
    public Page<Prestamo> findPage(PrestamoSearchDto dto) {

        PrestamoSpecification gameSpec = new PrestamoSpecification(new SearchCriteria("game.id", ":", dto.getIdGame()));
        PrestamoSpecification clientSpec = new PrestamoSpecification(new SearchCriteria("client.id", ":", dto.getIdClient()));
        PrestamoSpecification startSpec = new PrestamoSpecification(new SearchCriteria("startDate", "<=", dto.getDate()));
        PrestamoSpecification endSpec = new PrestamoSpecification(new SearchCriteria("endDate", ">=", dto.getDate()));

        Specification<Prestamo> spec = gameSpec.and(clientSpec).and(startSpec).and(endSpec);

        return this.prestamoRepository.findAll(spec, dto.getPageable().getPageable());
    }

    @Override
    public Prestamo get(Long id) {
        return this.prestamoRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Long id, PrestamoDto dto) {

        this.validate(dto); // Es valida que el prestem sigui correcte abans de modificar

        Prestamo prestamo;

        if (id == null) {
            prestamo = new Prestamo();
        } else {
            prestamo = this.get(id);
        }

        BeanUtils.copyProperties(dto, prestamo, "id", "game", "client");

        prestamo.setGame(this.gameService.get(dto.getGame().getId()));
        prestamo.setClient(this.clientService.get(dto.getClient().getId()));

        this.prestamoRepository.save(prestamo);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }

        this.prestamoRepository.deleteById(id);
    }

    private void validate(PrestamoDto dto) {
        long DIES_MAXIMS_PRESTEC = 14; // Dinamic per si es vol cambiar que sigui mes comode

        // Protegeix d'un NullPointerException
        if (dto.getGame() == null || dto.getGame().getId() == null || dto.getClient() == null || dto.getClient().getId() == null || dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new BadRequestException("Todos los campos del préstamo son obligatorios");
        }

        // Es permet un prestec d'un mateix dia
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        // Es compten el dia d'inici i de final
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;

        if (days > DIES_MAXIMS_PRESTEC) {
            throw new BadRequestException("El período de préstamo no puede superar los " + DIES_MAXIMS_PRESTEC + " días");
        }
    }
}