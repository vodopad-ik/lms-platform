package me.learning.lmsplatform.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import me.learning.lmsplatform.exception.SimulatedFailureException;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionTestServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private TransactionTestService transactionTestService;

  @Test
  void saveMultipleWithoutTransaction_shouldSaveThenThrow() {
    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      Category cat = invocation.getArgument(0);
      cat.setId(1L);
      return cat;
    });

    assertThrows(SimulatedFailureException.class, () -> {
      transactionTestService.saveMultipleWithoutTransaction();
    });

    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  @Test
  void saveMultipleWithTransaction_shouldSaveThenThrow() {
    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      Category cat = invocation.getArgument(0);
      cat.setId(1L);
      return cat;
    });

    assertThrows(SimulatedFailureException.class, () -> {
      transactionTestService.saveMultipleWithTransaction();
    });

    verify(categoryRepository, times(1)).save(any(Category.class));
  }
}
