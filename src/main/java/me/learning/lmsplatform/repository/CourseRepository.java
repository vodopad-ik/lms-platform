package me.learning.lmsplatform.repository;

import java.util.Optional;
import me.learning.lmsplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  Optional<Course> findByTitle(String title);
}
