package ru.practicum.main.service.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getCategoryById_whenExists_shouldReturnCategory() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Test");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategoryById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test", result.getName());
    }

    @Test
    void getCategoryById_whenNotExists_shouldThrowNotFoundException() {
        Long id = 999L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(id));
    }

    @Test
    void createCategory_shouldSaveAndReturn() {
        NewCategoryDto dto = new NewCategoryDto("New Category");
        Category category = new Category();
        category.setName("New Category");
        Category saved = new Category();
        saved.setId(1L);
        saved.setName("New Category");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDto result = categoryService.createCategory(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Category", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_whenNoEvents_shouldDelete() {
        Long id = 1L;
        when(eventRepository.existsByCategoryId(id)).thenReturn(false);
        doNothing().when(categoryRepository).deleteById(id);

        assertDoesNotThrow(() -> categoryService.deleteCategory(id));
        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteCategory_whenHasEvents_shouldThrowConflictException() {
        Long id = 1L;
        when(eventRepository.existsByCategoryId(id)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(id));
        verify(categoryRepository, never()).deleteById(id);
    }
}