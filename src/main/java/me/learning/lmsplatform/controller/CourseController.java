package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.dto.CourseDto;
import me.learning.lmsplatform.dto.CoursePatchDto;
import me.learning.lmsplatform.dto.LessonCreateDto;
import me.learning.lmsplatform.dto.LessonDto;
import me.learning.lmsplatform.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@Validated
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management API with advanced filtering and caching")
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  @Operation(summary = "Get all courses", description = "Retrieves a list of all courses with their teachers and categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<List<CourseDto>> getAllCourses() {
    return ResponseEntity.ok(courseService.getAllCourses());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get course by ID", description = "Retrieves a specific course by its ID with full details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Course found and returned"),
      @ApiResponse(responseCode = "404", description = "Course not found"),
      @ApiResponse(responseCode = "400", description = "Invalid course ID format")
  })
  public ResponseEntity<CourseDto> getCourseById(
      @Parameter(description = "Course ID") @PathVariable Long id) {
    return ResponseEntity.ok(courseService.getCourseById(id));
  }

  @GetMapping("/{courseId}/lessons")
  @Operation(summary = "Get course lessons", description = "Retrieves all lessons belonging to a specific course")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Course not found"),
      @ApiResponse(responseCode = "400", description = "Invalid course ID")
  })
  public ResponseEntity<List<LessonDto>> getCourseLessons(
      @Parameter(description = "Course ID") @PathVariable Long courseId) {
    return ResponseEntity.ok(courseService.getLessonsByCourseId(courseId));
  }

  @PostMapping("/{courseId}/lessons")
  @Operation(summary = "Add lesson to course", description = "Creates a new lesson and adds it to the specified course")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Lesson created and added successfully"),
      @ApiResponse(responseCode = "404", description = "Course not found"),
      @ApiResponse(responseCode = "400", description = "Invalid lesson data")
  })
  public ResponseEntity<LessonDto> addLessonToCourse(
      @Parameter(description = "Course ID") @PathVariable Long courseId, 
      @Valid @RequestBody LessonCreateDto lessonDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(courseService.addLessonToCourse(courseId, lessonDto));
  }

  @GetMapping("/search")
  @Operation(summary = "Get course by title", description = "Searches for a course by its exact title")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Course found and returned"),
      @ApiResponse(responseCode = "404", description = "Course not found"),
      @ApiResponse(responseCode = "400", description = "Title parameter is required")
  })
  public ResponseEntity<CourseDto> getCourseByTitle(
      @Parameter(description = "Course title to search for") @RequestParam String title) {
    return ResponseEntity.ok(courseService.getCourseByTitle(title));
  }

  @GetMapping("/filter")
  @Operation(summary = "Filter courses", description = "Filters courses by department, category, and price range with pagination. Supports both JPQL and Native query modes.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Courses filtered and returned successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
      @ApiResponse(responseCode = "500", description = "Internal server error during filtering")
  })
  public ResponseEntity<Page<CourseDto>> filterCourses(
      @Parameter(description = "Filter by teacher department") @RequestParam(required = false) String department,
      @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
      @Parameter(description = "Minimum price filter") @RequestParam(required = false) Double minPrice,
      @Parameter(description = "Maximum price filter") @RequestParam(required = false) Double maxPrice,
      @Parameter(description = "Pagination parameters") Pageable pageable) {
    return ResponseEntity.ok(courseService.searchCourses(
        department, category, minPrice, maxPrice, pageable, QueryMode.JPQL));
  }

  @GetMapping("/filter/native")
  @Operation(summary = "Filter courses (Native Query)", description = "Filters courses using native SQL queries. Same parameters as /filter but uses database-specific SQL.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Courses filtered and returned successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
      @ApiResponse(responseCode = "500", description = "Internal server error during filtering")
  })
  public ResponseEntity<Page<CourseDto>> filterCoursesNative(
      @Parameter(description = "Filter by teacher department") @RequestParam(required = false) String department,
      @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
      @Parameter(description = "Minimum price filter") @RequestParam(required = false) Double minPrice,
      @Parameter(description = "Maximum price filter") @RequestParam(required = false) Double maxPrice,
      @Parameter(description = "Pagination parameters") Pageable pageable) {
    return ResponseEntity.ok(courseService.searchCourses(
        department, category, minPrice, maxPrice, pageable, QueryMode.NATIVE));
  }

  @PostMapping
  @Operation(summary = "Create a new course", description = "Creates a new course with validation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Course created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Teacher or category not found")
  })
  public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(courseDto));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update a course",
      description = "Updates an existing course with validation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Course updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Course not found")
  })
  public ResponseEntity<CourseDto> updateCourse(
      @Parameter(description = "Course ID") @PathVariable Long id,
      @Valid @RequestBody CourseDto courseDto) {
    return ResponseEntity.ok(courseService.updateCourse(id, courseDto));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Partially update a course", description = "Updates specific fields of an existing course. Only provided fields will be updated.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Course updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "404", description = "Course not found")
  })
  public ResponseEntity<CourseDto> patchCourse(
      @Parameter(description = "Course ID") @PathVariable Long id, 
      @Valid @RequestBody CoursePatchDto patchDto) {
    return ResponseEntity.ok(courseService.patchCourse(id, patchDto));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a course", description = "Deletes a course by ID. This action is irreversible and will invalidate all related caches.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Course not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error during deletion")
  })
  public ResponseEntity<Void> deleteCourse(
      @Parameter(description = "Course ID to delete") @PathVariable Long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{courseId}/students/{studentId}")
  @Operation(summary = "Add student to course", description = "Enrolls a student in an existing course. Updates student-course relationship and invalidates relevant caches.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Student enrolled successfully"),
      @ApiResponse(responseCode = "404", description = "Course or student not found"),
      @ApiResponse(responseCode = "409", description = "Student already enrolled in this course"),
      @ApiResponse(responseCode = "500", description = "Internal server error during enrollment")
  })
  public ResponseEntity<CourseDto> addStudentToCourse(
      @Parameter(description = "Course ID") @PathVariable Long courseId, 
      @Parameter(description = "Student ID") @PathVariable Long studentId) {
    return ResponseEntity.ok(courseService.addStudentToCourse(courseId, studentId));
  }
}
