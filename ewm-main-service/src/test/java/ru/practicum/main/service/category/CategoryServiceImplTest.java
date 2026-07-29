package ru.practicum.main.service.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getCategoryByIdWhenExistsShouldReturnCategory() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Test");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(new CategoryDto(id, "Test"));

        CategoryDto result = categoryService.getCategoryById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getCategoryByIdWhenNotExistsShouldThrowNotFoundException() {
        Long id = 999L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(id));
    }

    @Test
    void createCategoryShouldSaveAndReturn() {
        NewCategoryDto dto = new NewCategoryDto("New Category");
        Category category = new Category();
        category.setName("New Category");
        Category saved = new Category();
        saved.setId(1L);
        saved.setName("New Category");

        when(categoryMapper.toEntity(dto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(new CategoryDto(1L, "New Category"));

        CategoryDto result = categoryService.createCategory(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Category", result.getName());
    }

    @Test
    void deleteCategoryWhenNoEventsShouldDelete() {
        Long id = 1L;
        when(eventRepository.existsByCategoryId(id)).thenReturn(false);
        doNothing().when(categoryRepository).deleteById(id);

        assertDoesNotThrow(() -> categoryService.deleteCategory(id));
        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteCategoryWhenHasEventsShouldThrowConflictException() {
        Long id = 1L;
        when(eventRepository.existsByCategoryId(id)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(id));
        verify(categoryRepository, never()).deleteById(id);
    }
}