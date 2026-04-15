package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.learning.lmsplatform.exception.ResourceNotFoundException;
import me.learning.lmsplatform.service.AsyncTaskInfo;
import me.learning.lmsplatform.service.AsyncTaskService;
import me.learning.lmsplatform.service.AsyncTaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/async-tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Async tasks", description = "LMS statistics generation")
public class AsyncTaskController {

  private static final String TASK_ID = "taskId";
  private static final String STATUS = "status";
  private static final String RESULT = "result";
  private static final String TASK_NOT_FOUND = "Task with ID %s not found";
  private static final String TASK_NOT_COMPLETED = "Task not completed yet";
  private static final String NOT_FOUND = " not found";

  private final AsyncTaskService asyncTaskService;

  @PostMapping
  @Operation(summary = "Start LMS statistics generation")
  public ResponseEntity<Map<String, String>> start(
      @RequestParam(defaultValue = "10") int steps,
      @RequestParam(defaultValue = "100") long delayMs) {
    String taskId = asyncTaskService.startDemoBusinessOperation(steps, delayMs);
    return ResponseEntity.ok(Map.of(TASK_ID, taskId));
  }

  @PostMapping("/demo")
  @Operation(summary = "Generate LMS statistics (courses, students, teachers, lessons)")
  public ResponseEntity<Map<String, String>> startDemo() {
    String taskId = asyncTaskService.startDemoBusinessOperation(1, 0);
    return ResponseEntity.ok(Map.of(TASK_ID, taskId));
  }

  @GetMapping("/{taskId}/status")
  @Operation(summary = "Get task status only")
  public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String taskId) {
    AsyncTaskInfo info = asyncTaskService.getTask(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, taskId)));
    
    return ResponseEntity.ok(Map.of(
        TASK_ID, info.taskId(),
        STATUS, info.status(),
        "progress", info.progressPercent()
    ));
  }

  @GetMapping("/{taskId}/result")
  @Operation(summary = "Get task result when completed")
  public ResponseEntity<Map<String, Object>> getResult(@PathVariable String taskId) {
    AsyncTaskInfo info = asyncTaskService.getTask(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, taskId)));
    
    if (info.status() != AsyncTaskStatus.SUCCESS) {
      return ResponseEntity.ok(Map.of(
          TASK_ID, info.taskId(),
          STATUS, info.status(),
          RESULT, TASK_NOT_COMPLETED
      ));
    }
    
    return ResponseEntity.ok(Map.of(
        TASK_ID, info.taskId(),
        STATUS, info.status(),
        RESULT, info.result()
    ));
  }

  @GetMapping("/{taskId}")
  @Operation(summary = "Get full task info")
  public ResponseEntity<AsyncTaskInfo> get(@PathVariable String taskId) {
    AsyncTaskInfo info = asyncTaskService.getTask(taskId)
        .orElseThrow(() -> new ResourceNotFoundException(String.format(TASK_NOT_FOUND, taskId)));
    return ResponseEntity.ok(info);
  }
}
