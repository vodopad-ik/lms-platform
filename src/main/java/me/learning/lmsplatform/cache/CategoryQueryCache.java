package me.learning.lmsplatform.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import me.learning.lmsplatform.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CategoryQueryCache {

  private final Map<CategoryQueryCacheKey, Page<CategoryDto>> index = new HashMap<>();

  public Page<CategoryDto> getOrLoad(
      CategoryQueryCacheKey key,
      Supplier<Page<CategoryDto>> loader) {
    synchronized (index) {
      Page<CategoryDto> cached = index.get(key);
      if (cached != null) {
        return cached;
      }
    }
    Page<CategoryDto> loaded = loader.get();
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

