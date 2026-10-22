package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.prestamo.PrestamoRepository;
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

    @Autowired
    PrestamoRepository prestamoRepository;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, ClientDto dto) throws Exception {
        Client client;

        if (id == null) {
            client = new Client();
            // Comprova si ja existeix un client amb aquest nom
            if (clientRepository.existsByName(dto.getName())) {
                throw new ConflictException("Ja existeix un client amb aquest nom");
            }

        } else {
            client = this.get(id);
        }

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

        // No permetre borrar el client si te prestams asociats
        if (this.prestamoRepository.existsByClientId(id)) {
            throw new ConflictException("No se puede eliminar el cliente porque tiene préstamos asociados");
        }

        this.clientRepository.deleteById(id);
    }

}
