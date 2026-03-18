package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Override
    @EntityGraph(attributePaths = { "courses" })
    Optional<Teacher> findById(Long id);

    @Override
    @EntityGraph(attributePaths = { "courses" })
    List<Teacher> findAll();

    @EntityGraph(attributePaths = { "courses" })
    @Query("SELECT t FROM Teacher t WHERE "
            + "(:nameFilter IS NULL OR "
            + "LOWER(t.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))) "
            + "AND (:departmentFilter IS NULL OR "
            + "LOWER(t.department) LIKE LOWER(CONCAT('%', :departmentFilter, '%'))) "
            + "AND (:courseCategory IS NULL OR EXISTS ("
            + "SELECT 1 FROM Course c "
            + "JOIN c.category cat "
            + "WHERE c.teacher = t "
            + "AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :courseCategory, '%'))"
            + "))")
    Page<Teacher> findWithFilters(
            @Param("nameFilter") String nameFilter,
            @Param("departmentFilter") String departmentFilter,
            @Param("courseCategory") String courseCategory,
            Pageable pageable);

    @Query(value = "SELECT t.* FROM teachers t "
            + "WHERE (:nameFilter IS NULL OR "
            + "LOWER(t.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))) "
            + "AND (:departmentFilter IS NULL OR "
            + "LOWER(t.department) LIKE "
            + "LOWER(CONCAT('%', :departmentFilter, '%'))) "
            + "AND (:courseCategory IS NULL OR EXISTS ("
            + "SELECT 1 FROM courses c "
            + "JOIN categories cat ON cat.id = c.category_id "
            + "WHERE c.teacher_id = t.id "
            + "AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :courseCategory, '%'))"
            + "))",
        countQuery = "SELECT COUNT(t.id) FROM teachers t "
                    + "WHERE (:nameFilter IS NULL OR "
                    + "LOWER(t.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))) "
                    + "AND (:departmentFilter IS NULL OR "
                    + "LOWER(t.department) LIKE "
                    + "LOWER(CONCAT('%', :departmentFilter, '%'))) "
                    + "AND (:courseCategory IS NULL OR EXISTS ("
                    + "SELECT 1 FROM courses c "
                    + "JOIN categories cat ON cat.id = c.category_id "
                    + "WHERE c.teacher_id = t.id "
                    + "AND LOWER(cat.name) LIKE LOWER(CONCAT('%', :courseCategory, '%'))"
                    + "))",
        nativeQuery = true)
    Page<Teacher> findWithFiltersNative(
            @Param("nameFilter") String nameFilter,
            @Param("departmentFilter") String departmentFilter,
            @Param("courseCategory") String courseCategory,
            Pageable pageable);
}
