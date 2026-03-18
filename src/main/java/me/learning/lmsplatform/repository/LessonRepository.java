package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Override
    @EntityGraph(attributePaths = {"course"})
    Optional<Lesson> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"course"})
    List<Lesson> findAll();

    @EntityGraph(attributePaths = {"course"})
    List<Lesson> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"course"})
    @Query("""
        SELECT l FROM Lesson l
        JOIN l.course c
        WHERE (:courseId IS NULL OR c.id = :courseId)
          AND (:courseTitle IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%')))
          AND (:titleFilter IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :titleFilter, '%')))
        """)
    Page<Lesson> findWithFilters(
        @Param("courseId") Long courseId,
        @Param("courseTitle") String courseTitle,
        @Param("titleFilter") String titleFilter,
        Pageable pageable);

    @Query(
        value = """
            SELECT l.* FROM lessons l
            JOIN courses c ON c.id = l.course_id
            WHERE (:courseId IS NULL OR c.id = :courseId)
              AND (:courseTitle IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%')))
              AND (:titleFilter IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :titleFilter, '%')))
            """,
        countQuery = """
            SELECT COUNT(l.id) FROM lessons l
            JOIN courses c ON c.id = l.course_id
            WHERE (:courseId IS NULL OR c.id = :courseId)
              AND (:courseTitle IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%')))
              AND (:titleFilter IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :titleFilter, '%')))
            """,
        nativeQuery = true)
    Page<Lesson> findWithFiltersNative(
        @Param("courseId") Long courseId,
        @Param("courseTitle") String courseTitle,
        @Param("titleFilter") String titleFilter,
        Pageable pageable);
}
