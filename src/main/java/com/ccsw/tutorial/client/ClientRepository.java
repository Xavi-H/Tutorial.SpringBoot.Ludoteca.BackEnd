package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import org.springframework.data.repository.CrudRepository;

/**
 * @author ccsw
 *
 */
public interface ClientRepository extends CrudRepository<Client, Long> {

    /**
     * Spring genera l'implementació d'aquest metode automàticament
     * Comprova fent una consulta automàticament si ja hi ha un client amb el nom que rep
     * @param name El nom que comprovarà si existeix
     * @author Xavi-H
     */
    boolean existsByName(String name);
}
