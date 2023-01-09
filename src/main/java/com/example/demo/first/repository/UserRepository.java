package com.example.demo.first.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.first.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
