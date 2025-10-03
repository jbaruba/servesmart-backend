// bovenin CategoryService.java
package com.jean.servesmart.restaurant.service.interfaces;


import com.jean.servesmart.restaurant.dto.CategoryRequest;
import com.jean.servesmart.restaurant.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
  List<CategoryResponse> list(String q);
  CategoryResponse create(CategoryRequest req);
  CategoryResponse update(Long id, CategoryRequest req);
  void delete(Long id);
}
