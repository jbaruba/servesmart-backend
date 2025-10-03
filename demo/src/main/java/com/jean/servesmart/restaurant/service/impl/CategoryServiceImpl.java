package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.CategoryRequest;
import com.jean.servesmart.restaurant.dto.CategoryResponse;
import com.jean.servesmart.restaurant.model.Category;
import com.jean.servesmart.restaurant.repository.CategoryRepository;
import com.jean.servesmart.restaurant.service.interfaces.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repo;

  public CategoryServiceImpl(CategoryRepository repo) {
    this.repo = repo;
  }

  private CategoryResponse toDto(Category c) {
    return new CategoryResponse(c.getId(), c.getName());
  }

  @Override
  public List<CategoryResponse> list(String q) {
    return repo.search(q).stream().map(this::toDto).toList();
  }


  @Override
  public CategoryResponse create(CategoryRequest req) {
    var c = new Category();
    c.setName(req.getName());
    return toDto(repo.save(c));
  }

  @Override
  public CategoryResponse update(Long id, CategoryRequest req) {
    var c = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    c.setName(req.getName());
    return toDto(repo.save(c));
  }

  @Override
  public void delete(Long id) {
    repo.deleteById(id);
  }
}
