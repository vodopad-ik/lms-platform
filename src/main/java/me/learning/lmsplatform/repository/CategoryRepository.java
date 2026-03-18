package me.learning.lmsplatform.repository;

import java.util.List;
import me.learning.lmsplatform.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    List<Category> findAll();

    @Query("""
        SELECT DISTINCT cat FROM Category cat
        LEFT JOIN cat.courses c
        LEFT JOIN c.teacher t
        WHERE (:nameFilter IS NULL OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :nameFilter, '%')))
          AND (:teacherDepartment IS NULL OR LOWER(t.department)
            LIKE LOWER(CONCAT('%', :teacherDepartment, '%')))
        """)
    Page<Category> findWithFilters(
        @Param("nameFilter") String nameFilter,
        @Param("teacherDepartment") String teacherDepartment,
        Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT cat.* FROM categories cat
            LEFT JOIN courses c ON c.category_id = cat.id
            LEFT JOIN teachers t ON t.id = c.teacher_id
            WHERE (:nameFilter IS NULL OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :nameFilter, '%')))
              AND (:teacherDepartment IS NULL OR LOWER(t.department)
                LIKE LOWER(CONCAT('%', :teacherDepartment, '%')))
            """,
        countQuery = """
            SELECT COUNT(DISTINCT cat.id) FROM categories cat
            LEFT JOIN courses c ON c.category_id = cat.id
            LEFT JOIN teachers t ON t.id = c.teacher_id
            WHERE (:nameFilter IS NULL OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :nameFilter, '%')))
              AND (:teacherDepartment IS NULL OR LOWER(t.department)
                LIKE LOWER(CONCAT('%', :teacherDepartment, '%')))
            """,
        nativeQuery = true)
    Page<Category> findWithFiltersNative(
        @Param("nameFilter") String nameFilter,
        @Param("teacherDepartment") String teacherDepartment,
        Pageable pageable);
}
