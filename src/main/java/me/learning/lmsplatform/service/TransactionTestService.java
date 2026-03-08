package me.learning.lmsplatform.service;

import lombok.RequiredArgsConstructor;
import me.learning.lmsplatform.exception.SimulatedFailureException;
import me.learning.lmsplatform.model.Category;
import me.learning.lmsplatform.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionTestService {

    private final CategoryRepository categoryRepository;

    public void saveMultipleWithoutTransaction() {
        String uuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        String name = "NoTx - " + uuid;
        categoryRepository.save(Category.builder().name(name).build());
        throw new SimulatedFailureException(
                "Error after first save! First record should be in DB.");
    }

    @Transactional
    public void saveMultipleWithTransaction() {
        String uuid = java.util.UUID.randomUUID().toString().substring(0, 8);
        String name = "WithTx - " + uuid;
        categoryRepository.save(Category.builder().name(name).build());
        throw new SimulatedFailureException("Error! @Transactional should rollback everything.");
    }
}
