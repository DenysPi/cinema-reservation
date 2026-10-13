package com.tonnom.cinema.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tonnom.cinema.user.model.User;

public interface UserRepository extends JpaRepository<User,Long> {

}
