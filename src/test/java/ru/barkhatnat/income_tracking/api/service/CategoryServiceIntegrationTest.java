package ru.barkhatnat.income_tracking.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.barkhatnat.income_tracking.entity.Account;
import ru.barkhatnat.income_tracking.entity.Category;
import ru.barkhatnat.income_tracking.entity.Operation;
import ru.barkhatnat.income_tracking.entity.User;
import ru.barkhatnat.income_tracking.exception.ForbiddenException;
import ru.barkhatnat.income_tracking.repositories.AccountRepository;
import ru.barkhatnat.income_tracking.repositories.CategoryRepository;
import ru.barkhatnat.income_tracking.repositories.OperationRepository;
import ru.barkhatnat.income_tracking.repositories.UserRepository;
import ru.barkhatnat.income_tracking.service.CategoryServiceImpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryServiceIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    OperationRepository operationRepository;
    private Category category;
    private Category defaultCategory;
    private Operation operation;
    private User user;
    @Autowired
    private CategoryServiceImpl categoryService;

    @BeforeEach
    public void setUp() {
        user = new User("username", "password", "email@email.com", Timestamp.from(Instant.now()), "USER");
        userRepository.saveAndFlush(user);
        Account account = new Account("Test Account", BigDecimal.valueOf(1000), user, Timestamp.from(Instant.now()));
        accountRepository.saveAndFlush(account);
        category = new Category("Category", Boolean.FALSE, user);
        categoryRepository.saveAndFlush(category);
        defaultCategory = new Category("Default Category", Boolean.FALSE, null);
        categoryRepository.saveAndFlush(defaultCategory);
        operation = new Operation(BigDecimal.valueOf(500), Timestamp.from(Instant.now()),
                category, account, "Test note", Timestamp.from(Instant.now()));
        operationRepository.saveAndFlush(operation);
    }

    @Test
    public void deleteCategory_CategoryReplacedByUnknownCategory() {
        UUID userId = user.getId();
        UUID operationId = operation.getId();
        UUID userCategoryId = category.getId();

        Optional<Category> unknownCategory = categoryRepository.findCategoryByTitle("Unknown");
        assertThat(unknownCategory).isPresent();

        categoryService.deleteCategory(userCategoryId, userId);
        Optional<Category> deletedCategory = categoryRepository.findById(userCategoryId);
        Optional<Operation> updatedOperation = operationRepository.findById(operationId);

        assertThat(deletedCategory).isEmpty();
        assertThat(updatedOperation).isPresent();
        assertThat(updatedOperation.get().getCategory().getTitle()).isEqualTo(unknownCategory.get().getTitle());
    }

    @Test
    public void deleteDefaultCategory_ThrowForbiddenException() {
        UUID userId = user.getId();
        UUID defaultCategoryId = defaultCategory.getId();
        assertThatThrownBy(() -> categoryService.deleteCategory(defaultCategoryId, userId)).isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Cannot delete default category");
    }
}
