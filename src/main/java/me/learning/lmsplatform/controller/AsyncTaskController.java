package me.learning.lmsplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "Async tasks", description = "Async business operation demo")
public class AsyncTaskController {

  private final AsyncTaskService asyncTaskService;

  @PostMapping
  @Operation(summary = "Start async business operation")
  public ResponseEntity<Map<String, String>> start(
      @RequestParam(defaultValue = "10") int steps,
      @RequestParam(defaultValue = "100") long delayMs) {
    String taskId = asyncTaskService.startDemoBusinessOperation(steps, delayMs);
    return ResponseEntity.ok(Map.of("taskId", taskId));
  }

  @PostMapping("/demo")
  @Operation(summary = "Start 10-second demo task without parameters")
  public ResponseEntity<Map<String, String>> startDemo() {
    // 10 steps, 1000ms delay = 10 seconds total
    String taskId = asyncTaskService.startDemoBusinessOperation(10, 1000);
    return ResponseEntity.ok(Map.of("taskId", taskId));
  }

  @GetMapping("/{taskId}/status")
  @Operation(summary = "Get task status only")
  public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String taskId) {
    AsyncTaskInfo info = asyncTaskService.getTask(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found"));
    
    return ResponseEntity.ok(Map.of(
        "taskId", info.taskId(),
        "status", info.status(),
        "progress", info.progressPercent()
    ));
  }

  @GetMapping("/{taskId}/result")
  @Operation(summary = "Get task result when completed")
  public ResponseEntity<Map<String, Object>> getResult(@PathVariable String taskId) {
    AsyncTaskInfo info = asyncTaskService.getTask(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found"));
    
    if (info.status() != AsyncTaskStatus.SUCCESS) {
      return ResponseEntity.ok(Map.of(
          "taskId", info.taskId(),
          "status", info.status(),
          "result", "Task not completed yet"
      ));
    }
    
    return ResponseEntity.ok(Map.of(
        "taskId", info.taskId(),
        "status", info.status(),
        "result", info.result()
    ));
  }

  @GetMapping("/{taskId}")
  @Operation(summary = "Get full task info")
  public ResponseEntity<AsyncTaskInfo> get(@PathVariable String taskId) {
    AsyncTaskInfo info = asyncTaskService.getTask(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found"));
    return ResponseEntity.ok(info);
  }
}
