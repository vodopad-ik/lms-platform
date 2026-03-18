package me.learning.lmsplatform.cache;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GenericQueryCache {

  private final ConcurrentHashMap<String, Page<?>> cache = new ConcurrentHashMap<>();

  public <T> Page<T> getOrLoad(String cacheKey, PageSupplier<T> supplier) {
    @SuppressWarnings("unchecked")
    Page<T> cached = (Page<T>) cache.get(cacheKey);
    if (cached != null) {
      return cached;
    }

    Page<T> loaded = supplier.load();
    cache.put(cacheKey, loaded);
    return loaded;
  }

  public void invalidateAll() {
    cache.clear();
  }

  @FunctionalInterface
  public interface PageSupplier<T> {
    Page<T> load();
  }
}
