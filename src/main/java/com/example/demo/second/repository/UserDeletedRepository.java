package com.example.demo.second.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.second.entity.UserDeleted;

public interface UserDeletedRepository extends JpaRepository<UserDeleted, Long> {
}
