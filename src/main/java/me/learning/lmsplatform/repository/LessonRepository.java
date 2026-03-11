package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
