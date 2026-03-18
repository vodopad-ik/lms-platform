package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Override
    @EntityGraph(attributePaths = { "courses" })
    Optional<Student> findById(Long id);

    @Override
    @EntityGraph(attributePaths = { "courses" })
    List<Student> findAll();

    @EntityGraph(attributePaths = { "courses" })
    @Query("SELECT s FROM Student s WHERE "
            + "(:nameFilter IS NULL OR "
            + "LOWER(s.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))) "
            + "AND (:emailFilter IS NULL OR "
            + "LOWER(s.email) LIKE LOWER(CONCAT('%', :emailFilter, '%'))) "
            + "AND (:courseTitle IS NULL OR EXISTS ("
            + "SELECT 1 FROM Course c "
            + "JOIN c.students st "
            + "WHERE st = s "
            + "AND LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%'))"
            + "))")
    Page<Student> findWithFilters(
            @Param("nameFilter") String nameFilter,
            @Param("emailFilter") String emailFilter,
            @Param("courseTitle") String courseTitle,
            Pageable pageable);

    @Query(value = "SELECT s.* FROM students s "
            + "WHERE (:nameFilter IS NULL OR "
            + "LOWER(s.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))) "
            + "AND (:emailFilter IS NULL OR "
            + "LOWER(s.email) LIKE LOWER(CONCAT('%', :emailFilter, '%'))) "
            + "AND (:courseTitle IS NULL OR EXISTS ("
            + "SELECT 1 FROM course_students cs "
            + "JOIN courses c ON c.id = cs.course_id "
            + "WHERE cs.student_id = s.id "
            + "AND LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%'))"
            + "))",
        countQuery = "SELECT COUNT(s.id) FROM students s "
                    + "WHERE (:nameFilter IS NULL OR "
                    + "LOWER(s.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))) "
                    + "AND (:emailFilter IS NULL OR "
                    + "LOWER(s.email) LIKE LOWER(CONCAT('%', :emailFilter, '%'))) "
                    + "AND (:courseTitle IS NULL OR EXISTS ("
                    + "SELECT 1 FROM course_students cs "
                    + "JOIN courses c ON c.id = cs.course_id "
                    + "WHERE cs.student_id = s.id "
                    + "AND LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%'))"
                    + "))",
        nativeQuery = true)
    Page<Student> findWithFiltersNative(
            @Param("nameFilter") String nameFilter,
            @Param("emailFilter") String emailFilter,
            @Param("courseTitle") String courseTitle,
            Pageable pageable);
}
