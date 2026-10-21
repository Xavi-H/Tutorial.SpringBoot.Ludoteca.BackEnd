package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.common.exception.ConflictException;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class PrestamoServiceImpl implements PrestamoService {

    private static final int DIES_MAXIMS_PRESTEC = 14;
    private static final int PRESTECS_MAXIMS_CLIENT_DIA = 2;

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

        this.validate(id, dto); // Es valida que el prestem sigui correcte abans de modificar

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


    // Validacions


    private void validate(Long id, PrestamoDto dto) {

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

        this.validateGameAvailable(id, dto);
        this.validateClientLimit(id, dto);
    }

    private void validateGameAvailable(Long id, PrestamoDto dto) {
        // Si un joc ja està prestat durant algun dia del rang
        boolean gameOcupat = this.prestamoRepository.findPrestmasGameRang(dto.getGame().getId(), dto.getStartDate(), dto.getEndDate()).stream().anyMatch(p -> !p.getId().equals(id));

        if (gameOcupat) {
            throw new ConflictException("El juego ya está prestado en alguno de los días indicados");
        }
    }

    private void validateClientLimit(Long id, PrestamoDto dto) {

        List<Prestamo> prestamsClient = this.prestamoRepository
                .findPrestamsClientRang(dto.getClient().getId(), dto.getStartDate(), dto.getEndDate()) // Obté els prestams del client en un rang
                .stream() // Converteix els prestams a elements sobre els cuals es poden filtrar
                .filter(p -> !p.getId().equals(id)) // Filtra si s'està editant un prestac (mateix id)
                .toList(); // Converteix a llista el resultat

        // Revisa cada día del rang (no pot haber-hi dos prestams asociats a aquest client en un dia si es vol crear un altre prestem durant aquell rang
        for (LocalDate day = dto.getStartDate(); !day.isAfter(dto.getEndDate()); day = day.plusDays(1)) {
            final LocalDate current = day;

            // Conta la cuantitat de prestams cada dia per comprovar si supera el maxim permes
            long prestamsTotals = prestamsClient.stream().filter(p -> !current.isBefore(p.getStartDate()) && !current.isAfter(p.getEndDate())).count();

            if (prestamsTotals >= PRESTECS_MAXIMS_CLIENT_DIA) {
                throw new ConflictException("El cliente ya tiene " + PRESTECS_MAXIMS_CLIENT_DIA + " juegos prestados el día " + current.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }
    }
}