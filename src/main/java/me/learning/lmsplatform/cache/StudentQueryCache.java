package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.learning.lmsplatform.dto.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class StudentQueryCache {

  private final Map<StudentQueryCacheKey, Page<StudentDto>> index = new HashMap<>();

  public Page<StudentDto> getOrLoad(
      StudentQueryCacheKey key,
      Supplier<Page<StudentDto>> loader) {
    synchronized (index) {
      Page<StudentDto> cached = index.get(key);
      if (cached != null) {
        return cached;
      }
    }
    Page<StudentDto> loaded = loader.get();
    synchronized (index) {
      index.put(key, loaded);
    }
    return loaded;
  }

  public void invalidateAll() {
    synchronized (index) {
      index.clear();
    }
  }
}
