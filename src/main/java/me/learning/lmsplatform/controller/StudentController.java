package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.StudentDto;
import me.learning.lmsplatform.mapper.StudentMapper;
import me.learning.lmsplatform.repository.StudentRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @GetMapping
    public List<StudentDto> getAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public StudentDto getStudent(@PathVariable Long id) {
        if (id == null) {
            return null;
        }
        return studentRepository.findById(id)
                .map(studentMapper::mapToDto)
                .orElse(null);
    }

    @PostMapping
    public StudentDto createStudent(@RequestBody StudentDto studentDto) {
        if (studentDto == null) {
            return null;
        }
        return studentMapper.mapToDto(studentRepository.save(
                studentMapper.mapToEntity(studentDto)));
    }

    @PutMapping("/{id}")
    public StudentDto updateStudent(@PathVariable Long id,
                                   @RequestBody StudentDto studentDetails) {
        if (id == null || studentDetails == null) {
            return null;
        }
        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(studentDetails.getName());
                    student.setEmail(studentDetails.getEmail());
                    student.setEnrollmentDate(studentDetails.getEnrollmentDate());
                    return studentMapper.mapToDto(studentRepository.save(student));
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        if (id != null) {
            studentRepository.deleteById(id);
        }
    }
}
