package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  @EntityGraph(attributePaths = {"teacher", "category"})
  List<Course> findAll();

  @EntityGraph(attributePaths = {"teacher", "category"})
  Optional<Course> findByTitle(String title);

  @EntityGraph(attributePaths = {"teacher", "category"})
  Optional<Course> findById(Long id);

  @Query("SELECT c FROM Course c JOIN FETCH c.lessons WHERE c.id = :id")
  Optional<Course> findCourseWithLessons(Long id);

  @EntityGraph(attributePaths = {"teacher", "category"})
  @Query(
      "SELECT c FROM Course c JOIN c.teacher t JOIN c.category cat "
          + "WHERE (:department IS NULL OR t.department = :department) "
          + "AND (:categoryName IS NULL OR cat.name = :categoryName) "
          + "AND (:minPrice IS NULL OR c.price >= :minPrice) "
          + "AND (:maxPrice IS NULL OR c.price <= :maxPrice)")
  Page<Course> findWithFilters(
      @Param("department") String department,
      @Param("categoryName") String categoryName,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      Pageable pageable);

  @Query(value = "SELECT c.id, c.title, c.description, c.price, "
          + "c.duration_weeks AS durationWeeks, c.teacher_id AS teacherId, "
          + "t.name AS teacherName, c.category_id AS categoryId, "
          + "cat.name AS categoryName "
          + "FROM courses c "
          + "JOIN teachers t ON c.teacher_id = t.id "
          + "JOIN categories cat ON c.category_id = cat.id "
          + "WHERE (:department IS NULL OR t.department = :department) "
          + "AND (:categoryName IS NULL OR cat.name = :categoryName) "
          + "AND (:minPrice IS NULL OR c.price >= :minPrice) "
          + "AND (:maxPrice IS NULL OR c.price <= :maxPrice)",
      countQuery = "SELECT COUNT(c.id) "
          + "FROM courses c "
          + "JOIN teachers t ON c.teacher_id = t.id "
          + "JOIN categories cat ON c.category_id = cat.id "
          + "WHERE (:department IS NULL OR t.department = :department) "
          + "AND (:categoryName IS NULL OR cat.name = :categoryName) "
          + "AND (:minPrice IS NULL OR c.price >= :minPrice) "
          + "AND (:maxPrice IS NULL OR c.price <= :maxPrice)",
      nativeQuery = true)
  Page<CourseProjection> findWithFiltersNative(
      @Param("department") String department,
      @Param("categoryName") String categoryName,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      Pageable pageable);
}
