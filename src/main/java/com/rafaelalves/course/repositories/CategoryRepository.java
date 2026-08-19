package com.rafaelalves.course.repositories;

import com.rafaelalves.course.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
