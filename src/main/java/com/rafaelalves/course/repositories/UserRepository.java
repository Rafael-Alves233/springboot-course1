package com.rafaelalves.course.repositories;

import com.rafaelalves.course.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
