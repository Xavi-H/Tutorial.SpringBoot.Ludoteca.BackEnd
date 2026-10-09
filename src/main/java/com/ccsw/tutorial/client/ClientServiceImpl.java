package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xavi-H
 *
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Client get(Long id) {

        return this.clientRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() {

        return (List<Client>) this.clientRepository.findAll();
    }

    // TODO: Hacer que si ya existe no permita crearlo

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, ClientDto dto) throws Exception {
        Client client;

        if (id == null) {
            client = new Client();
        } else {
            client = this.get(id);
        }

        // Comprova si ja existeix un client amb aquest nom
        if (clientRepository.existsByName(dto.getName())) {
            throw new Exception("Ja existeix un client amb aquest nom");
        }
        // Si no existeix el guarda correctament
        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }

        this.clientRepository.deleteById(id);
    }

}
