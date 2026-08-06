package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.model.Category;

@UtilityClass
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category toEntity(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName().trim());
        return category;
    }

    public void updateEntity(Category category, NewCategoryDto dto) {
        category.setName(dto.getName().trim());
    }
}