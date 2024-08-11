package ru.barkhatnat.income_tracking.api.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.barkhatnat.income_tracking.DTO.CategoryDto;
import ru.barkhatnat.income_tracking.DTO.CategoryResponseDto;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.CategoryNotFoundException;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.service.CategoryServiceImpl;
import ru.barkhatnat.income_tracking.service.UserService;
import ru.barkhatnat.income_tracking.utils.CategoryMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserService userService;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void CategoryService_CreateCategory_ReturnCategory() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CategoryDto categoryDto = new CategoryDto("Test Category", Boolean.FALSE);
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category category = new Category(categoryId, categoryDto.title(), categoryDto.categoryType(), user);
        CategoryResponseDto expectedCategoryResponseDto = new CategoryResponseDto(categoryId, "Test Category", Boolean.FALSE);

        when(userService.findUser(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(expectedCategoryResponseDto);

        CategoryResponseDto actualCategoryResponseDto = categoryService.createCategory(categoryDto, userId);
        Assertions.assertThat(actualCategoryResponseDto).isEqualTo(expectedCategoryResponseDto);
    }

    @Test
    public void CategoryService_FindAllCategories_ReturnAllCategories() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category userCategory1 = new Category("userCategory1", Boolean.FALSE, user);
        Category userCategory2 = new Category("userCategory2", Boolean.FALSE, user);
        Category defaultCategory1 = new Category("defaultCategory1", Boolean.FALSE, null);
        Category defaultCategory2 = new Category("defaultCategory2", Boolean.FALSE, null);

        when(categoryRepository.findCategoriesByUserId(userId)).thenReturn(List.of(userCategory1, userCategory2));
        when(categoryRepository.findCategoriesByUserEmpty()).thenReturn(List.of(defaultCategory1, defaultCategory2));

        List<Category> categories = categoryService.findAllCategories(userId);
        Assertions.assertThat(categories).hasSize(4);
    }

    @Test
    public void CategoryService_FindCategoryById_ReturnCategory() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category category = new Category(categoryId, "userCategory1", Boolean.FALSE, user);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Optional<Category> actualCategory = categoryService.findCategory(categoryId);
        Assertions.assertThat(actualCategory).isPresent();
        Assertions.assertThat(actualCategory.get()).isEqualTo(category);
    }

    @Test
    public void CategoryService_UpdateCategoryById_ReturnUpdatedCategory() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String newTitle = "Updated Category";
        Boolean newCategoryType = true;
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category category = new Category(categoryId, "userCategory1", Boolean.FALSE, user);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.updateCategory(categoryId, newTitle, newCategoryType, userId);
        Optional<Category> updatedCategory = categoryRepository.findById(categoryId);
        Assertions.assertThat(updatedCategory).isPresent();
        Assertions.assertThat(updatedCategory.get().getTitle()).isEqualTo(newTitle);
        Assertions.assertThat(updatedCategory.get().getCategoryType()).isEqualTo(newCategoryType);
    }

    @Test
    public void CategoryService_UpdateCategoryById_ThrowForbiddenException() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        String newTitle = "Updated Category";
        Boolean newCategoryType = true;
        User otherUser = new User(otherUserId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category category = new Category(categoryId, "Old Category", false, otherUser);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        assertThrows(ForbiddenException.class,
                () -> categoryService.updateCategory(categoryId, newTitle, newCategoryType, userId));
    }

    @Test
    public void CategoryService_UpdateCategoryById_ThrowCategoryNotFoundException() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String newTitle = "Updated Category";
        Boolean newCategoryType = true;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, newTitle, newCategoryType, userId));
    }

    @Test
    public void CategoryService_DeleteCategory_CategoryIsDeleted() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category category = new Category(categoryId, "userCategory1", Boolean.FALSE, user);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId, userId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        Assertions.assertThat(categoryRepository.findById(categoryId)).isEmpty();
    }

    @Test
    public void CategoryService_DeleteCategory_ThrowCategoryNotFoundException() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId, userId));
    }

    @Test
    public void CategoryService_DeleteCategory_ThrowForbiddenException() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        User otherUser = new User(otherUserId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        Category category = new Category(categoryId, "userCategory1", Boolean.FALSE, otherUser);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(ForbiddenException.class,
                () -> categoryService.deleteCategory(categoryId, userId));
    }

    @Test
    public void testDeleteCategory_ForbiddenException() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Category category = new Category(categoryId, "defaultCategory", Boolean.FALSE, null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(ForbiddenException.class, () -> {
            categoryService.deleteCategory(categoryId, userId);
        });
    }

    @Test
    public void testDeleteCategory_DefaultCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");

        Category category = new Category(categoryId, "defaultCategory", Boolean.FALSE, user);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findCategoryByTitle("Unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            categoryService.deleteCategory(categoryId, userId);
        });
    }
}
