package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Teacher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Override
    @EntityGraph(attributePaths = {"courses"})
    Optional<Teacher> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"courses"})
    List<Teacher> findAll();
}
