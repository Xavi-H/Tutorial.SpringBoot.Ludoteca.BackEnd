package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.prestamo.model.PrestamoDto;
import com.ccsw.tutorial.prestamo.model.PrestamoSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate // Desde Spring Boot 4, @SpringBootTest ya no registra TestRestTemplate automáticamente, hay que activarlo con esta linea
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PrestamoIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/prestamo";

    private static final int TOTAL_PRESTAMOS = 5;
    private static final long NOT_EXISTS_ID = 99L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<PrestamoDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<PrestamoDto>>() {
    };

    private static LocalDate oct(int day) {
        return LocalDate.of(2026, 10, day);
    }

    private static LocalDate nov(int day) {
        return LocalDate.of(2026, 11, day);
    }

    private ResponsePage<PrestamoDto> find(PageableRequest pageable, Long idGame, Long idClient, LocalDate date) {

        PrestamoSearchDto searchDto = new PrestamoSearchDto();
        searchDto.setPageable(pageable);
        searchDto.setIdGame(idGame);
        searchDto.setIdClient(idClient);
        searchDto.setDate(date);

        ResponseEntity<ResponsePage<PrestamoDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        return response.getBody();
    }

    private ResponsePage<PrestamoDto> find(Long idGame, Long idClient, LocalDate date) {
        return find(new PageableRequest(0, 10), idGame, idClient, date);
    }

    private List<Long> ids(ResponsePage<PrestamoDto> page) {
        return page.getContent().stream().map(PrestamoDto::getId).sorted().toList();
    }

    @SuppressWarnings("rawtypes")
    private ResponseEntity<Map> save(Long id, Long gameId, Long clientId, LocalDate startDate, LocalDate endDate) {

        GameDto game = new GameDto();
        game.setId(gameId);

        ClientDto client = new ClientDto();
        client.setId(clientId);

        PrestamoDto dto = new PrestamoDto();
        dto.setGame(game);
        dto.setClient(client);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        String url = LOCALHOST + port + SERVICE_PATH + (id == null ? "" : "/" + id);

        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), Map.class);
    }

    @SuppressWarnings("rawtypes")
    private String message(ResponseEntity<Map> response) {

        assertNotNull(response.getBody());
        return String.valueOf(response.getBody().get("message"));
    }

    @Test
    public void findWithoutFiltersShouldReturnAllPrestamos() {

        ResponsePage<PrestamoDto> page = find(null, null, null);

        assertEquals(TOTAL_PRESTAMOS, page.getTotalElements());
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), ids(page));
    }

    @Test
    public void findFirstPageShouldRespectPageSize() {

        ResponsePage<PrestamoDto> page = find(new PageableRequest(0, 3), null, null, null);

        assertEquals(TOTAL_PRESTAMOS, page.getTotalElements());
        assertEquals(3, page.getContent().size());
    }

    @Test
    public void findSecondPageShouldReturnRemainingPrestamos() {

        ResponsePage<PrestamoDto> page = find(new PageableRequest(1, 3), null, null, null);

        assertEquals(TOTAL_PRESTAMOS, page.getTotalElements());
        assertEquals(2, page.getContent().size());
    }

    @Test
    public void findByClientShouldReturnOnlyThatClient() {

        ResponsePage<PrestamoDto> page = find(null, 3L, null);

        assertEquals(List.of(3L, 4L), ids(page));
        assertEquals(2, page.getTotalElements());
    }

    @Test
    public void findByGameShouldReturnOnlyThatGame() {

        assertEquals(List.of(1L, 4L), ids(find(1L, null, null)));
    }

    @Test
    public void findByDateShouldIncludeTheLastDayOfAPrestamo() {

        // El préstamo 1 termina el 10-10 y el 4 empieza el 11-10
        assertEquals(List.of(1L, 2L, 3L), ids(find(null, null, oct(10))));
    }

    @Test
    public void findByDateShouldIncludeTheFirstDayOfAPrestamo() {

        assertEquals(List.of(2L, 3L, 4L), ids(find(null, null, oct(11))));
    }

    @Test
    public void findByDateWithoutPrestamosShouldReturnEmpty() {

        ResponsePage<PrestamoDto> page = find(null, null, oct(25));

        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }

    @Test
    public void findWithSeveralFiltersShouldCombineThem() {

        assertEquals(List.of(1L), ids(find(1L, null, oct(10))));
        assertEquals(List.of(4L), ids(find(1L, 3L, oct(11))));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithoutIdShouldCreateNewPrestamo() {

        ResponseEntity<Map> response = save(null, 2L, 2L, nov(1), nov(5));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TOTAL_PRESTAMOS + 1, find(null, null, null).getTotalElements());
        assertEquals(List.of(6L), ids(find(2L, 2L, null)));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void modifyWithExistIdShouldModifyPrestamo() {

        // El préstamo 1 pasa del juego 1 al juego 4 (libre esos días)
        ResponseEntity<Map> response = save(1L, 4L, 1L, oct(1), oct(10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TOTAL_PRESTAMOS, find(null, null, null).getTotalElements());
        assertEquals(List.of(1L, 5L), ids(find(4L, null, null)));
        assertEquals(List.of(4L), ids(find(1L, null, null)));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void modifyWithoutChangesShouldNotConflictWithItself() {

        ResponseEntity<Map> response = save(1L, 1L, 1L, oct(1), oct(10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void modifyWithNotExistIdShouldInternalError() {

        ResponseEntity<Map> response = save(NOT_EXISTS_ID, 2L, 2L, nov(1), nov(5));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithEndBeforeStartShouldReturnBadRequest() {

        ResponseEntity<Map> response = save(null, 2L, 2L, nov(10), nov(5));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(message(response).contains("fecha de fin"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithFifteenDaysShouldReturnBadRequest() {

        ResponseEntity<Map> response = save(null, 2L, 2L, nov(1), nov(15));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(message(response).contains("14"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithFourteenDaysShouldCreate() {

        ResponseEntity<Map> response = save(null, 2L, 2L, nov(1), nov(14));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithGameAlreadyLentShouldReturnConflict() {

        // El juego 1 está prestado del 01-10 al 10-10 (préstamo 1)
        ResponseEntity<Map> response = save(null, 1L, 2L, oct(5), oct(9));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(message(response).contains("prestado"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithGameShouldCountLastDayAndAllowTheNextOne() {

        // El préstamo 4 (juego 1) termina el 18-10: ese día sigue ocupado y el 19 ya está libre
        assertEquals(HttpStatus.CONFLICT, save(null, 1L, 2L, oct(18), oct(20)).getStatusCode());
        assertEquals(HttpStatus.OK, save(null, 1L, 2L, oct(19), oct(22)).getStatusCode());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveWithClientAtDailyLimitShouldReturnConflict() {

        // El cliente 1 ya tiene los préstamos 1 y 2 del 05-10 al 10-10; el juego 4 está libre esos días
        ResponseEntity<Map> response = save(null, 4L, 1L, oct(6), oct(8));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(message(response).contains("2 juegos"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void saveOverlappingTwoLoansOnDifferentDaysShouldCreate() {

        // Cliente 2: A (días 1-3) y B (días 4-6) no coinciden nunca en el mismo día
        assertEquals(HttpStatus.OK, save(null, 2L, 2L, nov(1), nov(3)).getStatusCode());
        assertEquals(HttpStatus.OK, save(null, 3L, 2L, nov(4), nov(6)).getStatusCode());

        // C (días 2-5) toca A y B, pero cada día solo hay uno: es válido
        assertEquals(HttpStatus.OK, save(null, 4L, 2L, nov(2), nov(5)).getStatusCode());

        // El día 3 ya coinciden A y C: un cuarto préstamo ese día sería el tercero
        ResponseEntity<Map> response = save(null, 1L, 2L, nov(3), nov(3));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(message(response).contains("2 juegos"));
    }

    @Test
    public void deleteWithExistsIdShouldDeletePrestamo() {

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/5", HttpMethod.DELETE, null, Void.class);

        ResponsePage<PrestamoDto> page = find(null, null, null);

        assertEquals(TOTAL_PRESTAMOS - 1, page.getTotalElements());
        assertEquals(List.of(1L, 2L, 3L, 4L), ids(page));
    }

    @Test
    public void deleteWithNotExistsIdShouldInternalError() {

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NOT_EXISTS_ID, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}