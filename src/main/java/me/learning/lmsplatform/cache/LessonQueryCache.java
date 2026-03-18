package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.learning.lmsplatform.dto.LessonDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class LessonQueryCache {

  private final Map<LessonQueryCacheKey, Page<LessonDto>> index = new HashMap<>();

  public Page<LessonDto> getOrLoad(
      LessonQueryCacheKey key,
      Supplier<Page<LessonDto>> loader) {
    synchronized (index) {
      Page<LessonDto> cached = index.get(key);
      if (cached != null) {
        return cached;
      }
    }
    Page<LessonDto> loaded = loader.get();
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
