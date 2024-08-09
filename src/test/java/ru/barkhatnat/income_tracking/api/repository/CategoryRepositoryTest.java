package ru.barkhatnat.income_tracking.api.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.repositories.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    public void CategoryRepositoryTest_SaveOne_ReturnSavedCategory() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Category category = new Category("Test", Boolean.FALSE, user);
        Category savedCategory = categoryRepository.save(category);
        Assertions.assertThat(savedCategory).isNotNull();
        Assertions.assertThat(savedCategory.getTitle()).isEqualTo("Test");
        Assertions.assertThat(savedCategory.getCategoryType()).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void CategoryRepositoryTest_FindAll_ReturnAllSaved() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Category category1 = new Category("Category1", Boolean.TRUE, user);
        Category category2 = new Category("Category2", Boolean.FALSE, user);
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        List<Category> categories = categoryRepository.findAll();
        Assertions.assertThat(categories.size()).isEqualTo(2);
    }

    @Test
    public void CategoryRepositoryTest_FindById_ReturnExistingCategory() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Category category = new Category("Test", Boolean.FALSE, user);
        Category savedCategory = categoryRepository.save(category);
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());
        Assertions.assertThat(foundCategory).isPresent();
        Assertions.assertThat(foundCategory.get()).isEqualTo(savedCategory);
    }

    @Test
    public void CategoryRepositoryTest_FindById_ReturnEmptyWhenNotFound() {
        UUID uuid = UUID.randomUUID();
        Optional<Category> foundCategory = categoryRepository.findById(uuid); // Предположим, что ID 1 не существует
        Assertions.assertThat(foundCategory).isEmpty();
    }

    @Test
    public void CategoryRepositoryTest_FindByUserId_ReturnCategoriesOfUser() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Category category1 = new Category("Category1", Boolean.TRUE, user);
        Category category2 = new Category("Category2", Boolean.FALSE, user);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        List<Category> categories = categoryRepository.findCategoriesByUserId(user.getId());
        Assertions.assertThat(categories.size()).isEqualTo(2);
    }

    @Test
    public void CategoryRepositoryTest_Delete_RemoveExistingCategory() {
        User user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.save(user);
        Category category = new Category("Test", Boolean.FALSE, user);
        Category savedCategory = categoryRepository.save(category);
        categoryRepository.delete(savedCategory);
        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());
        Assertions.assertThat(foundCategory).isEmpty();
    }

    @Test
    public void CategoryRepositoryTest_FindDefault_ReturnDefaultCategories() {
        Category category1 = new Category("Category1", Boolean.TRUE, null);
        Category category2 = new Category("Category2", Boolean.FALSE, null);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        List<Category> categories = categoryRepository.findCategoriesByUserEmpty();
        Assertions.assertThat(categories.size()).isEqualTo(2);
    }
}
