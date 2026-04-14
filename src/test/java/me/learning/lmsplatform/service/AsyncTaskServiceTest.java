package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncTaskServiceTest {

  @Mock
  private AsyncTaskRunner asyncTaskRunner;

  private AsyncTaskService asyncTaskService;

  @BeforeEach
  void setUp() {
    asyncTaskService = new AsyncTaskService(asyncTaskRunner);
  }

  @Test
  void startDemoBusinessOperation_shouldCreateTaskWithPendingStatus() {
    String taskId = asyncTaskService.startDemoBusinessOperation(5, 10);

    assertNotNull(taskId);
    Optional<AsyncTaskInfo> taskOpt = asyncTaskService.getTask(taskId);
    assertTrue(taskOpt.isPresent());

    AsyncTaskInfo task = taskOpt.get();
    assertEquals(taskId, task.taskId());
    assertEquals(AsyncTaskStatus.PENDING, task.status());
    assertEquals(0, task.progressPercent());
    assertNotNull(task.createdAt());
  }

  @Test
  void getTask_shouldReturnEmptyForUnknownTaskId() {
    Optional<AsyncTaskInfo> result = asyncTaskService.getTask("unknown-id");
    assertTrue(result.isEmpty());
  }

  @Test
  void update_shouldUpdateTaskStatusAndProgress() {
    String taskId = asyncTaskService.startDemoBusinessOperation(5, 10);

    asyncTaskService.update(taskId, AsyncTaskStatus.PROCESSING, 50, null, null, null);

    Optional<AsyncTaskInfo> taskOpt = asyncTaskService.getTask(taskId);
    assertTrue(taskOpt.isPresent());
    assertEquals(AsyncTaskStatus.PROCESSING, taskOpt.get().status());
    assertEquals(50, taskOpt.get().progressPercent());
  }

  @Test
  void update_shouldSetResultOnSuccess() {
    String taskId = asyncTaskService.startDemoBusinessOperation(5, 10);

    asyncTaskService.update(taskId, AsyncTaskStatus.SUCCESS, 100, null, null, "Done");

    Optional<AsyncTaskInfo> taskOpt = asyncTaskService.getTask(taskId);
    assertTrue(taskOpt.isPresent());
    assertEquals(AsyncTaskStatus.SUCCESS, taskOpt.get().status());
    assertEquals("Done", taskOpt.get().result());
    assertEquals(100, taskOpt.get().progressPercent());
  }

  @Test
  void update_shouldSetErrorOnFailure() {
    String taskId = asyncTaskService.startDemoBusinessOperation(5, 10);

    asyncTaskService.update(taskId, AsyncTaskStatus.FAILED, 0, null, null, "Error occurred");

    Optional<AsyncTaskInfo> taskOpt = asyncTaskService.getTask(taskId);
    assertTrue(taskOpt.isPresent());
    assertEquals(AsyncTaskStatus.FAILED, taskOpt.get().status());
    assertEquals("Error occurred", taskOpt.get().error());
  }

  @Test
  void update_shouldClampProgressToValidRange() {
    String taskId = asyncTaskService.startDemoBusinessOperation(5, 10);

    asyncTaskService.update(taskId, AsyncTaskStatus.PROCESSING, 150, null, null, null);
    Optional<AsyncTaskInfo> taskOpt = asyncTaskService.getTask(taskId);
    assertEquals(100, taskOpt.get().progressPercent());

    asyncTaskService.update(taskId, AsyncTaskStatus.PROCESSING, -10, null, null, null);
    taskOpt = asyncTaskService.getTask(taskId);
    assertEquals(0, taskOpt.get().progressPercent());
  }
}
