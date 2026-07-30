package ru.practicum.main.service.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCategory_shouldSaveToDatabase() {
        NewCategoryDto dto = new NewCategoryDto("Integration Test");

        CategoryDto result = categoryService.createCategory(dto);

        assertNotNull(result.getId());
        assertEquals("Integration Test", result.getName());

        Category saved = categoryRepository.findById(result.getId()).orElse(null);
        assertNotNull(saved);
        assertEquals("Integration Test", saved.getName());
    }

    @Test
    void getCategoryById_whenNotExists_shouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(999L));
    }
}