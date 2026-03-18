package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.learning.lmsplatform.dto.TeacherDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TeacherQueryCache {

  private final Map<TeacherQueryCacheKey, Page<TeacherDto>> index = new HashMap<>();

  public Page<TeacherDto> getOrLoad(
      TeacherQueryCacheKey key,
      Supplier<Page<TeacherDto>> loader) {
    synchronized (index) {
      Page<TeacherDto> cached = index.get(key);
      if (cached != null) {
        return cached;
      }
    }
    Page<TeacherDto> loaded = loader.get();
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
