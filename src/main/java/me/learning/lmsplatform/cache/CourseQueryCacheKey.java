package me.learning.lmsplatform.cache;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;

public final class CourseQueryCacheKey {

  private final QueryMode queryMode;
  private final String teacherDepartment;
  private final String categoryName;
  private final Double minPrice;
  private final Double maxPrice;
  private final int page;
  private final int size;
  private final List<String> sortOrders;

  private CourseQueryCacheKey(
      QueryMode queryMode,
      String teacherDepartment,
      String categoryName,
      Double minPrice,
      Double maxPrice,
      int page,
      int size,
      List<String> sortOrders) {
    this.queryMode = Objects.requireNonNull(queryMode, "queryMode");
    this.teacherDepartment = teacherDepartment;
    this.categoryName = categoryName;
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.page = page;
    this.size = size;
    this.sortOrders = sortOrders == null ? List.of() : List.copyOf(sortOrders);
  }

  public static CourseQueryCacheKey from(
      QueryMode queryMode,
      String teacherDepartment,
      String categoryName,
      Double minPrice,
      Double maxPrice,
      Pageable pageable) {
    int page = pageable == null ? 0 : pageable.getPageNumber();
    int size = pageable == null ? 20 : pageable.getPageSize();
    return new CourseQueryCacheKey(
        queryMode,
        teacherDepartment,
        categoryName,
        minPrice,
        maxPrice,
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

  QueryMode getQueryMode() {
    return queryMode;
  }

  String getTeacherDepartment() {
    return teacherDepartment;
  }

  String getCategoryName() {
    return categoryName;
  }

  Double getMinPrice() {
    return minPrice;
  }

  Double getMaxPrice() {
    return maxPrice;
  }

  int getPage() {
    return page;
  }

  int getSize() {
    return size;
  }

  List<String> getSortOrders() {
    return sortOrders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CourseQueryCacheKey that)) {
      return false;
    }
    return page == that.page
        && size == that.size
        && queryMode == that.queryMode
        && Objects.equals(teacherDepartment, that.teacherDepartment)
        && Objects.equals(categoryName, that.categoryName)
        && Objects.equals(minPrice, that.minPrice)
        && Objects.equals(maxPrice, that.maxPrice)
        && Objects.equals(sortOrders, that.sortOrders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        queryMode,
        teacherDepartment,
        categoryName,
        minPrice,
        maxPrice,
        page,
        size,
        sortOrders);
  }
}
