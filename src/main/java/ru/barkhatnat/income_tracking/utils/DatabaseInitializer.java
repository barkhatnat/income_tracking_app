package ru.barkhatnat.income_tracking.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final CategoryRepository categoryRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDefaultCategories() {
        addDefaultCategory("Unknown", false);
    }

    private void addDefaultCategory(String categoryTitle, Boolean categoryType) {
        if (categoryRepository.findCategoryByTitle(categoryTitle).isEmpty()) {
            Category category = new Category(categoryTitle, categoryType, null);
            categoryRepository.save(category);
        }
    }
}
