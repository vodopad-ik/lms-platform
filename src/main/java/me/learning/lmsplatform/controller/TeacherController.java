package me.learning.lmsplatform.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.dto.TeacherDto;
import me.learning.lmsplatform.mapper.TeacherMapper;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @GetMapping
    public List<TeacherDto> getAll() {
        return teacherRepository.findAll().stream()
                .map(teacherMapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public TeacherDto getTeacher(@PathVariable Long id) {
        if (id == null) {
            return null;
        }
        return teacherRepository.findById(id)
                .map(teacherMapper::mapToDto)
                .orElse(null);
    }

    @PostMapping
    public TeacherDto createTeacher(@RequestBody TeacherDto teacherDto) {
        if (teacherDto == null) {
            return null;
        }
        return teacherMapper.mapToDto(teacherRepository.save(
                teacherMapper.mapToEntity(teacherDto)));
    }

    @PutMapping("/{id}")
    public TeacherDto updateTeacher(@PathVariable Long id,
                                   @RequestBody TeacherDto teacherDetails) {
        if (id == null || teacherDetails == null) {
            return null;
        }
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacher.setName(teacherDetails.getName());
                    teacher.setEmail(teacherDetails.getEmail());
                    teacher.setDepartment(teacherDetails.getDepartment());
                    teacher.setExperienceYears(teacherDetails.getExperienceYears());
                    return teacherMapper.mapToDto(teacherRepository.save(teacher));
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteTeacher(@PathVariable Long id) {
        if (id != null) {
            teacherRepository.deleteById(id);
        }
    }
}
