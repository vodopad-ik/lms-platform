package me.learning.lmsplatform.repository;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Override
    @EntityGraph(attributePaths = {"courses"})
    Optional<Student> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"courses"})
    List<Student> findAll();
}
