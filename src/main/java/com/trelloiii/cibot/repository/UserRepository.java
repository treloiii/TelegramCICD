package com.trelloiii.cibot.repository;

import com.trelloiii.cibot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
