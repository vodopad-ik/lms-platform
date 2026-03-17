package me.learning.lmsplatform.repository;

public interface CourseProjection {
  Long getId();

  String getTitle();

  String getDescription();

  Double getPrice();

  Integer getDurationWeeks();

  Long getTeacherId();

  String getTeacherName();

  Long getCategoryId();

  String getCategoryName();
}
