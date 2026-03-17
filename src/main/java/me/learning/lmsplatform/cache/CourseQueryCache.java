package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.learning.lmsplatform.dto.CourseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CourseQueryCache {

  private final Map<CourseQueryCacheKey, Page<CourseDto>> index = new HashMap<>();

  public Page<CourseDto> getOrLoad(CourseQueryCacheKey key, Supplier<Page<CourseDto>> loader) {
    synchronized (index) {
      Page<CourseDto> cached = index.get(key);
      if (cached != null) {
        return cached;
      }
    }
    Page<CourseDto> loaded = loader.get();
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
