package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import me.learning.lmsplatform.cache.QueryMode;
import me.learning.lmsplatform.cache.TeacherQueryCache;
import me.learning.lmsplatform.dto.TeacherDto;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.mapper.TeacherMapper;
import me.learning.lmsplatform.model.Teacher;
import me.learning.lmsplatform.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private TeacherMapper teacherMapper;

  @Mock
  private TeacherQueryCache teacherQueryCache;

  @Mock
  private CacheInvalidationService cacheInvalidationService;

  @InjectMocks
  private TeacherService teacherService;

  @Test
  void getAllTeachers_shouldReturnAllTeachers() {
    Teacher teacher1 = new Teacher();
    teacher1.setId(1L);
    teacher1.setName("Teacher 1");

    Teacher teacher2 = new Teacher();
    teacher2.setId(2L);
    teacher2.setName("Teacher 2");

    when(teacherRepository.findAll()).thenReturn(List.of(teacher1, teacher2));
    when(teacherMapper.mapToDto(teacher1)).thenReturn(TeacherDto.builder().id(1L).name("Teacher 1").build());
    when(teacherMapper.mapToDto(teacher2)).thenReturn(TeacherDto.builder().id(2L).name("Teacher 2").build());

    List<TeacherDto> result = teacherService.getAllTeachers();

    assertEquals(2, result.size());
  }

  @Test
  void getTeacherById_whenExists_shouldReturnTeacher() {
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    teacher.setName("Test Teacher");

    when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
    when(teacherMapper.mapToDto(teacher)).thenReturn(TeacherDto.builder().id(1L).name("Test Teacher").build());

    TeacherDto result = teacherService.getTeacherById(1L);

    assertNotNull(result);
    assertEquals("Test Teacher", result.getName());
  }

  @Test
  void getTeacherById_whenNotExists_shouldThrowException() {
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> teacherService.getTeacherById(999L));
  }

  @Test
  void createTeacher_shouldSaveAndReturnTeacher() {
    TeacherDto dto = TeacherDto.builder().name("New Teacher").department("CS").build();
    Teacher entity = new Teacher();
    entity.setName("New Teacher");
    Teacher saved = new Teacher();
    saved.setId(1L);
    saved.setName("New Teacher");

    when(teacherMapper.mapToEntity(dto)).thenReturn(entity);
    when(teacherRepository.save(entity)).thenReturn(saved);
    when(teacherMapper.mapToDto(saved)).thenReturn(TeacherDto.builder().id(1L).name("New Teacher").build());

    TeacherDto result = teacherService.createTeacher(dto);

    assertNotNull(result.getId());
    verify(cacheInvalidationService, times(1)).onTeacherChanged();
  }

  @Test
  void updateTeacher_whenExists_shouldUpdateAndReturnTeacher() {
    TeacherDto dto = TeacherDto.builder().name("Updated").department("Math").build();
    Teacher existing = new Teacher();
    existing.setId(1L);
    existing.setName("Old");
    Teacher saved = new Teacher();
    saved.setId(1L);
    saved.setName("Updated");

    when(teacherRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(teacherRepository.save(existing)).thenReturn(saved);
    when(teacherMapper.mapToDto(saved)).thenReturn(TeacherDto.builder().id(1L).name("Updated").build());

    TeacherDto result = teacherService.updateTeacher(1L, dto);

    assertEquals("Updated", result.getName());
    verify(cacheInvalidationService, times(1)).onTeacherChanged();
  }

  @Test
  void updateTeacher_whenNotExists_shouldThrowException() {
    TeacherDto dto = TeacherDto.builder().name("Updated").build();
    when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> teacherService.updateTeacher(999L, dto));
  }

  @Test
  void deleteTeacher_shouldDeleteAndInvalidateCache() {
    teacherService.deleteTeacher(1L);

    verify(teacherRepository, times(1)).deleteById(1L);
    verify(cacheInvalidationService, times(1)).onTeacherChanged();
  }

  @Test
  void searchTeachers_withJpqlMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    teacher.setName("Test");
    Page<Teacher> page = new PageImpl<>(List.of(teacher), pageable, 1);

    when(teacherQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(teacherRepository.findWithFilters("test", "dept", "category", pageable)).thenReturn(page);
    when(teacherMapper.mapToDto(teacher)).thenReturn(TeacherDto.builder().id(1L).name("Test").build());

    Page<TeacherDto> result = teacherService.searchTeachers("test", "dept", "category", pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchTeachers_withNativeMode_shouldReturnResults() {
    Pageable pageable = PageRequest.of(0, 10);
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    teacher.setName("Test");
    Page<Teacher> page = new PageImpl<>(List.of(teacher), pageable, 1);

    when(teacherQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(teacherRepository.findWithFiltersNative("test", "dept", "category", pageable)).thenReturn(page);
    when(teacherMapper.mapToDto(teacher)).thenReturn(TeacherDto.builder().id(1L).name("Test").build());

    Page<TeacherDto> result = teacherService.searchTeachers("test", "dept", "category", pageable, QueryMode.NATIVE);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void searchTeachers_withNullFilters_shouldNormalizeToEmptyString() {
    Pageable pageable = PageRequest.of(0, 10);
    Teacher teacher = new Teacher();
    teacher.setId(1L);
    teacher.setName("Test");
    Page<Teacher> page = new PageImpl<>(List.of(teacher), pageable, 1);

    when(teacherQueryCache.getOrLoad(any(), any())).then(invocation -> {
      return invocation.getArgument(1, java.util.function.Supplier.class).get();
    });
    when(teacherRepository.findWithFilters("", "", "", pageable)).thenReturn(page);
    when(teacherMapper.mapToDto(teacher)).thenReturn(TeacherDto.builder().id(1L).name("Test").build());

    Page<TeacherDto> result = teacherService.searchTeachers(null, null, null, pageable, QueryMode.JPQL);

    assertEquals(1, result.getTotalElements());
  }
}
