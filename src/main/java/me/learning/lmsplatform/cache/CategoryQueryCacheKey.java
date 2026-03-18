package me.learning.lmsplatform.cache;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;

public final class CategoryQueryCacheKey {

  private final QueryMode queryMode;
  private final String nameFilter;
  private final String teacherDepartment;
  private final int page;
  private final int size;
  private final List<String> sortOrders;

  private CategoryQueryCacheKey(
      QueryMode queryMode,
      String nameFilter,
      String teacherDepartment,
      int page,
      int size,
      List<String> sortOrders) {
    this.queryMode = Objects.requireNonNull(queryMode, "queryMode");
    this.nameFilter = nameFilter;
    this.teacherDepartment = teacherDepartment;
    this.page = page;
    this.size = size;
    this.sortOrders = sortOrders == null ? List.of() : List.copyOf(sortOrders);
  }

  public static CategoryQueryCacheKey from(
      QueryMode queryMode,
      String nameFilter,
      String teacherDepartment,
      Pageable pageable) {
    int page = pageable == null ? 0 : pageable.getPageNumber();
    int size = pageable == null ? 20 : pageable.getPageSize();
    return new CategoryQueryCacheKey(
        queryMode,
        nameFilter,
        teacherDepartment,
        page,
        size,
        buildSortOrders(pageable));
  }

  private static List<String> buildSortOrders(Pageable pageable) {
    if (pageable == null) {
      return List.of();
    }
    return pageable.getSort().stream()
        .map(order -> order.getProperty() + ":" + order.getDirection().name())
        .toList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CategoryQueryCacheKey that)) {
      return false;
    }
    return page == that.page
        && size == that.size
        && queryMode == that.queryMode
        && Objects.equals(nameFilter, that.nameFilter)
        && Objects.equals(teacherDepartment, that.teacherDepartment)
        && Objects.equals(sortOrders, that.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryMode, nameFilter, teacherDepartment, page, size, sortOrders);
  }
}

