package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("select c from Category c " +
         "where (:q is null or lower(c.name) like lower(concat('%', :q, '%')))")
  List<Category> search(@Param("q") String q);
}
