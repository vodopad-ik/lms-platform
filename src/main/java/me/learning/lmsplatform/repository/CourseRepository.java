package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
