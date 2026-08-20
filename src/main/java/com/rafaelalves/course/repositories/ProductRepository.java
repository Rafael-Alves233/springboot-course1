package com.rafaelalves.course.repositories;

import com.rafaelalves.course.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
