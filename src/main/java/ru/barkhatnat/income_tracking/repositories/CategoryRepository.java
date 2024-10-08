package ru.barkhatnat.income_tracking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.barkhatnat.income_tracking.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.user.id IS NULL")
    List<Category> findCategoriesByUserEmpty();

    List<Category> findCategoriesByUserId(UUID userId);

    Optional<Category> findCategoryByTitle(String title);
}
