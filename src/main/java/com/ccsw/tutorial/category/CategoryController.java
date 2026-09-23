package com.ccsw.tutorial.category;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xavi-H
 */
@Tag(name = "Category", description = "API of Category")
@RequestMapping(value = "/category")
@RestController
@CrossOrigin(origins = "*")
public class CategoryController {

    /**
     * Método para probar el servicio
     */
    @RequestMapping(path = "", method = RequestMethod.GET)
    public String prueba() {

        return "Probando el Controller tutorial Xavi";
    }

    // TODO: Metodos: Listar, actualizar, guardar i borrar.
}