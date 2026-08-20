package com.rafaelalves.course.repositories;

import com.rafaelalves.course.entities.OrderItem;
import com.rafaelalves.course.entities.pk.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

}
