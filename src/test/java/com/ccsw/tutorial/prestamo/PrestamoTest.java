package com.ccsw.tutorial.prestamo;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.exception.BadRequestException;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.prestamo.model.Prestamo;
import com.ccsw.tutorial.prestamo.model.PrestamoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrestamoTest {

    public static final Long EXISTS_PRESTAMO_ID = 1L;
    public static final Long NOT_EXISTS_PRESTAMO_ID = 0L;
    public static final Long GAME_ID = 2L;
    public static final Long CLIENT_ID = 3L;

    private static final LocalDate START = LocalDate.of(2026, 11, 1);

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private GameService gameService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    private PrestamoDto buildDto(LocalDate startDate, LocalDate endDate) {

        GameDto game = new GameDto();
        game.setId(GAME_ID);

        ClientDto client = new ClientDto();
        client.setId(CLIENT_ID);

        PrestamoDto dto = new PrestamoDto();
        dto.setGame(game);
        dto.setClient(client);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        return dto;
    }

    private Prestamo buildPrestamo(Long id, LocalDate startDate, LocalDate endDate) {

        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setStartDate(startDate);
        prestamo.setEndDate(endDate);

        return prestamo;
    }

    @Test
    public void getExistsPrestamoIdShouldReturnPrestamo() {

        Prestamo prestamo = mock(Prestamo.class);
        when(prestamoRepository.findById(EXISTS_PRESTAMO_ID)).thenReturn(Optional.of(prestamo));

        assertNotNull(prestamoService.get(EXISTS_PRESTAMO_ID));
    }

    @Test
    public void getNotExistsPrestamoIdShouldReturnNull() {

        when(prestamoRepository.findById(NOT_EXISTS_PRESTAMO_ID)).thenReturn(Optional.empty());

        assertNull(prestamoService.get(NOT_EXISTS_PRESTAMO_ID));
    }

    @Test
    public void saveWithoutIdShouldInsert() {

        Game game = mock(Game.class);
        Client client = mock(Client.class);
        when(gameService.get(GAME_ID)).thenReturn(game);
        when(clientService.get(CLIENT_ID)).thenReturn(client);

        prestamoService.save(null, buildDto(START, START.plusDays(4)));

        ArgumentCaptor<Prestamo> captor = ArgumentCaptor.forClass(Prestamo.class);
        verify(prestamoRepository).save(captor.capture());

        assertSame(game, captor.getValue().getGame());
        assertSame(client, captor.getValue().getClient());
        assertEquals(START, captor.getValue().getStartDate());
        assertEquals(START.plusDays(4), captor.getValue().getEndDate());
    }

    @Test
    public void saveExistsIdShouldUpdate() {

        Prestamo existing = buildPrestamo(EXISTS_PRESTAMO_ID, START, START.plusDays(2));
        when(prestamoRepository.findById(EXISTS_PRESTAMO_ID)).thenReturn(Optional.of(existing));

        prestamoService.save(EXISTS_PRESTAMO_ID, buildDto(START.plusDays(5), START.plusDays(8)));

        verify(prestamoRepository).save(existing);
        assertEquals(START.plusDays(5), existing.getStartDate());
        assertEquals(START.plusDays(8), existing.getEndDate());
    }

    @Test
    public void saveWithoutClientShouldThrowBadRequest() {

        PrestamoDto dto = buildDto(START, START.plusDays(1));
        dto.setClient(null);

        assertThrows(BadRequestException.class, () -> prestamoService.save(null, dto));
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    public void saveWithEndBeforeStartShouldThrowBadRequest() {

        PrestamoDto dto = buildDto(START.plusDays(3), START);

        assertThrows(BadRequestException.class, () -> prestamoService.save(null, dto));
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    public void saveWithSameStartAndEndShouldInsert() {

        assertDoesNotThrow(() -> prestamoService.save(null, buildDto(START, START)));

        verify(prestamoRepository).save(any(Prestamo.class));
    }

    @Test
    public void saveWithFourteenDaysShouldInsert() {

        // Se cuentan ambos extremos: del día 1 al día 14 son 14 días
        assertDoesNotThrow(() -> prestamoService.save(null, buildDto(START, START.plusDays(13))));

        verify(prestamoRepository).save(any(Prestamo.class));
    }

    @Test
    public void saveWithFifteenDaysShouldThrowBadRequest() {

        PrestamoDto dto = buildDto(START, START.plusDays(14));

        assertThrows(BadRequestException.class, () -> prestamoService.save(null, dto));
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    public void saveWithGameAlreadyLentShouldThrowConflict() {

        LocalDate end = START.plusDays(4);
        Prestamo other = buildPrestamo(99L, START.plusDays(2), START.plusDays(6));
        when(prestamoRepository.findPrestmasGameRang(GAME_ID, START, end)).thenReturn(List.of(other));

        assertThrows(ConflictException.class, () -> prestamoService.save(null, buildDto(START, end)));
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    public void saveEditingSamePrestamoShouldNotConflictWithItself() {

        // Al editar, la consulta devuelve el propio préstamo (en BD aún tiene sus fechas antiguas):
        // no debe contarse contra sí mismo ni para el juego ni para el cliente
        LocalDate end = START.plusDays(4);
        Prestamo existing = buildPrestamo(EXISTS_PRESTAMO_ID, START, end);
        when(prestamoRepository.findById(EXISTS_PRESTAMO_ID)).thenReturn(Optional.of(existing));
        when(prestamoRepository.findPrestmasGameRang(GAME_ID, START, end)).thenReturn(List.of(existing));
        when(prestamoRepository.findPrestamsClientRang(CLIENT_ID, START, end)).thenReturn(List.of(existing));

        assertDoesNotThrow(() -> prestamoService.save(EXISTS_PRESTAMO_ID, buildDto(START, end)));

        verify(prestamoRepository).save(existing);
    }

    @Test
    public void saveWithClientAtDailyLimitShouldThrowConflict() {

        LocalDate end = START.plusDays(4);
        List<Prestamo> others = List.of(buildPrestamo(10L, START, end), buildPrestamo(11L, START, end));
        when(prestamoRepository.findPrestamsClientRang(CLIENT_ID, START, end)).thenReturn(others);

        assertThrows(ConflictException.class, () -> prestamoService.save(null, buildDto(START, end)));
        verify(prestamoRepository, never()).save(any(Prestamo.class));
    }

    @Test
    public void saveOverlappingTwoLoansOnDifferentDaysShouldInsert() {

        // El cliente tiene un préstamo los días 1-3 y otro los días 4-6. El nuevo (días 2-5) se solapa
        // con los dos, pero ningún día coinciden más de uno, así que es válido (hay que contar día a día)
        Prestamo first = buildPrestamo(10L, START, START.plusDays(2));
        Prestamo second = buildPrestamo(11L, START.plusDays(3), START.plusDays(5));
        LocalDate newStart = START.plusDays(1);
        LocalDate newEnd = START.plusDays(4);
        when(prestamoRepository.findPrestamsClientRang(CLIENT_ID, newStart, newEnd)).thenReturn(List.of(first, second));

        assertDoesNotThrow(() -> prestamoService.save(null, buildDto(newStart, newEnd)));

        verify(prestamoRepository).save(any(Prestamo.class));
    }

    @Test
    public void deleteExistsPrestamoIdShouldDelete() throws Exception {

        Prestamo prestamo = mock(Prestamo.class);
        when(prestamoRepository.findById(EXISTS_PRESTAMO_ID)).thenReturn(Optional.of(prestamo));

        prestamoService.delete(EXISTS_PRESTAMO_ID);

        verify(prestamoRepository).deleteById(EXISTS_PRESTAMO_ID);
    }

    @Test
    public void deleteNotExistsPrestamoIdShouldThrowException() {

        when(prestamoRepository.findById(NOT_EXISTS_PRESTAMO_ID)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> prestamoService.delete(NOT_EXISTS_PRESTAMO_ID));
        verify(prestamoRepository, never()).deleteById(any());
    }
}