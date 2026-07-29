package ru.practicum.main.service.category;

import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long id);

    CategoryDto createCategory(NewCategoryDto dto);

    CategoryDto updateCategory(Long id, CategoryDto dto);

    void deleteCategory(Long id);
}
