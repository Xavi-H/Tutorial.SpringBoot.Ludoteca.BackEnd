package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void findAllShouldReturnAllClients() {

        List<Client> list = new ArrayList<>();
        list.add(mock(Client.class));

        when(clientRepository.findAll()).thenReturn(list);

        List<Client> clients = clientService.findAll();

        assertNotNull(clients);
        assertEquals(1, clients.size());
    }

    public static final String CLIENT_NAME = "CAT1";

    @Test
    public void saveNotExistsClientIdShouldInsert() throws Exception {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        ArgumentCaptor<Client> client = ArgumentCaptor.forClass(Client.class);

        clientService.save(null, clientDto);

        verify(clientRepository).save(client.capture());

        assertEquals(CLIENT_NAME, client.getValue().getName());
    }

    public static final Long EXISTS_CATEGORY_ID = 1L;

    @Test
    public void saveExistsClientIdShouldUpdate() throws Exception {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(client));

        clientService.save(EXISTS_CATEGORY_ID, clientDto);

        verify(clientRepository).save(client);
    }

    @Test
    public void deleteExistsClientIdShouldDelete() throws Exception {

        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(client));

        clientService.delete(EXISTS_CATEGORY_ID);

        verify(clientRepository).deleteById(EXISTS_CATEGORY_ID);
    }

    public static final Long NOT_EXISTS_CATEGORY_ID = 0L;

    @Test
    public void getExistsClientIdShouldReturnClient() {

        Client client = mock(Client.class);
        when(client.getId()).thenReturn(EXISTS_CATEGORY_ID);
        when(clientRepository.findById(EXISTS_CATEGORY_ID)).thenReturn(Optional.of(client));

        Client clientResponse = clientService.get(EXISTS_CATEGORY_ID);

        assertNotNull(clientResponse);
        assertEquals(EXISTS_CATEGORY_ID, client.getId());
    }

    @Test
    public void getNotExistsClientIdShouldReturnNull() {

        when(clientRepository.findById(NOT_EXISTS_CATEGORY_ID)).thenReturn(Optional.empty());

        Client client = clientService.get(NOT_EXISTS_CATEGORY_ID);

        assertNull(client);
    }

    @Test
    void noHaDeixarCrearClientSiElNomJaExisteix() throws Exception {

        // Simular que ja existeix un client amb aquest nom
        ClientDto dto = new ClientDto();
        dto.setName("Anna");

        when(clientRepository.existsByName("Anna")).thenReturn(true);

        // Comprovar que es llança una excepció
        assertThrows(Exception.class, () -> {
            clientService.save(null, dto);
        });

        // Comprovar que no s'ha guardat cap client
        verify(clientRepository, never()).save(any(Client.class));
    }

}



