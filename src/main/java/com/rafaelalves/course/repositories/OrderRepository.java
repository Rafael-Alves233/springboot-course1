package com.rafaelalves.course.repositories;

import com.rafaelalves.course.entities.Order;
import com.rafaelalves.course.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
