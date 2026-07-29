package ru.practicum.main.repository.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void saveCategoryShouldGenerateId() {
        Category category = new Category();
        category.setName("Test Category");

        Category saved = categoryRepository.save(category);

        assertNotNull(saved.getId());
        assertEquals("Test Category", saved.getName());
    }

    @Test
    void findByIdShouldReturnCategory() {
        Category category = new Category();
        category.setName("Test Category");
        Category saved = categoryRepository.save(category);

        Category found = categoryRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test Category", found.getName());
    }

    @Test
    void deleteCategoryShouldRemove() {
        Category category = new Category();
        category.setName("Test Category");
        Category saved = categoryRepository.save(category);

        categoryRepository.deleteById(saved.getId());

        assertTrue(categoryRepository.findById(saved.getId()).isEmpty());
    }
}