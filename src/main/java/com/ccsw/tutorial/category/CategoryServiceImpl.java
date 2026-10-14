package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.common.exception.ConflictException;
import com.ccsw.tutorial.game.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Category get(Long id) {

        return this.categoryRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findAll() {

        return (List<Category>) this.categoryRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, CategoryDto dto) {

        Category category;

        if (id == null) {
            category = new Category();
        } else {
            category = this.get(id);
        }

        category.setName(dto.getName());

        this.categoryRepository.save(category);
    }

    @Autowired
    GameRepository gameRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }
        // Comprova abans de borrar si hi ha jocs que utilitzan la categoria
        if (this.gameRepository.existsByCategoryId(id)) {
            throw new ConflictException("No es pot eliminar la categoria perque té jocs asociats");
        }

        this.categoryRepository.deleteById(id);
    }

}
