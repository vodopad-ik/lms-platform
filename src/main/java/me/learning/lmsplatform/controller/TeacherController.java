package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.TeacherDto;
import me.learning.lmsplatform.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Teacher management API")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @Operation(summary = "Get all teachers", operationId = "teacherGetAll")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by id", operationId = "teacherGetById")
    public ResponseEntity<TeacherDto> getTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter teachers (JPQL)", operationId = "teacherFilterJpql")
    public ResponseEntity<Page<TeacherDto>> filterTeachers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String courseCategory,
            Pageable pageable) {
        return ResponseEntity.ok(teacherService.searchTeachers(
                name, department, courseCategory, pageable, QueryMode.JPQL));
    }

    @GetMapping("/filter/native")
    @Operation(summary = "Filter teachers (native query)", operationId = "teacherFilterNative")
    public ResponseEntity<Page<TeacherDto>> filterTeachersNative(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String courseCategory,
            Pageable pageable) {
        return ResponseEntity.ok(teacherService.searchTeachers(
                name, department, courseCategory, pageable, QueryMode.NATIVE));
    }

    @PostMapping
    @Operation(summary = "Create teacher", operationId = "teacherCreate")
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.createTeacher(teacherDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update teacher", operationId = "teacherUpdate")
    public ResponseEntity<TeacherDto> updateTeacher(
            @PathVariable Long id,
            @Valid @RequestBody TeacherDto teacherDto) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete teacher", operationId = "teacherDelete")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
