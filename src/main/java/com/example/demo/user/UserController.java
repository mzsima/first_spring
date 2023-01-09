package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.first.entity.User;
import com.example.demo.first.repository.UserRepository;
import com.example.demo.second.entity.UserDeleted;
import com.example.demo.second.repository.UserDeletedRepository;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  UserRepository repository;

  @Autowired
  UserDeletedRepository userDeletedRepository;

  @GetMapping
  public User get(@RequestParam(value = "id") Long id) {
    var user = repository.findById(id);
    return user.get();
  }

  @PostMapping
  public User create(@RequestBody User user) {
    var res = repository.save(user);
    return res;
  }

  @DeleteMapping
  public void delete(@RequestParam(value = "id") Long id) {
    var user = repository.findById(id).get();
    repository.deleteById(user.getId());
    backup(user);
  }

  void backup(User user) {
    var userDeleted = new UserDeleted();
    userDeleted.setId(user.getId());
    userDeleted.setEmail(user.getEmail());
    userDeleted.setPassword(user.getPassword());
    userDeletedRepository.save(userDeleted);
  }
}
