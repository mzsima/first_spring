# 超入門 Userを登録して、検索する

## setup

- spring initializer からテンプレートを作成
https://start.spring.io/


application.yaml
```yaml
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.7'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    runtimeOnly 'com.h2database:h2'
}
```

## userの実装

- 注意

H2をDBに使っているので、userという文字が予約語らしくエラーになる。

```cli
Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Syntax error in SQL statement "create table [*]user ..."; expected "identifier"; SQL statement:
```

コレを回避するために`application.yaml`にこんな感じで、設定を記述

```application.yaml
spring:
  jpa:
    properties:
      hibernate:
        globally_quoted_identifiers: true
        globally_quoted_identifiers_skip_column_definitions: true
```


- entity の作成

```java 
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String password;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
```

- repository の作成

```java
package com.example.demo.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
```

- controller の作成

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository repository;

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

}
```

## 起動

### サーバー 起動

```console
gradle bootRun
```

### ユーザー登録

```console
curl -X POST http://localhost:8080/user -d '{"email": "aaa", "password": "bbbb"}' -H 'Content-Type: application/json'
```

### ユーザー取得

```console
curl http://localhost:8080/user?id=1
```
