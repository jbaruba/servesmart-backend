package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

  @Query("""
         select m from MenuItem m
         where (:q is null or lower(m.name) like lower(concat('%', :q, '%')))
           and (:categoryId is null or m.category.id = :categoryId)
         order by m.name asc
         """)
  List<MenuItem> search(@Param("q") String q, @Param("categoryId") Long categoryId);

  List<MenuItem> findByCategoryId(Long categoryId);
}
