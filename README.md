# 超入門 データソースをH2からMySQLに変更する

Spring boot で何か作ろう超入門。

## 以前の記事

この記事は、下記のlessonを見ている前提で書いています。

- [lesson1: 超入門 Userを登録して、検索する](https://github.com/mzsima/first_spring/tree/lesson1)


## 前回との違い

前回は、DBにh2を利用したため特にDBの起動などはしていなかったのですが、今回はローカルにDBを立ち上げてそれを使うように変更します。 

## DB準備

### Docker 
docker 使うからインストール。

### MySQLをdockerで起動

DockerでMySQLを起動しときます。

```console
docker run --name test-mysql -e MYSQL_DATABASE=db_example -e MYSQL_ROOT_PASSWORD=my-secret -p 3306:3306  -d mysql:8 mysqld --default-authentication-plugin=mysql_native_password
```

### MySQL起動確認

コンテナが動いているかは、コレ

```console
docker ps

CONTAINER ID   IMAGE     COMMAND                  CREATED         STATUS         PORTS                               NAMES
5d215eca2665   mysql:8   "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   3306/tcp, 0.0.0.0:3307->33060/tcp   test-mysql
```

MySQLが動いているかは、一回コンテナの中に入って mysqlのcliで確認

```console
docker exec -it test-mysql bash -l
```

コンテナの中に入ったら

```console
mysql -p

Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 12
```

って感じで、MySQLが動いていればOK.


## Spring設定追加

build.gradleのdependenciesにMySQLのコネクターを記述（h2は削除)

```gradle
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
  runtimeOnly 'com.mysql:mysql-connector-j'
}
```

application.yamlにDB接続情報を記述

ここが、今回の`肝`。 設定値がそれぞれ何を意味しているのか覚えてみよう。ここらへん -> [Data property](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.data.spring.datasource.url)

```application.yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_example
    username: root
    password: my-secret
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    show-sql: true
    hibernate: 
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

## Spring 起動

準備ができたので、起動。

```console
gradle bootRun
```

起動ログにこんな感じで、DDLがでる。

``` console
Hibernate: 
    
    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB
Hibernate: 
    
    insert into hibernate_sequence values ( 1 )
Hibernate: 
    
    create table user (
       id bigint not null,
        email varchar(255),
        password varchar(255),
        primary key (id)
    ) engine=InnoDB
```

## MySQLのテーブル確認

MySQLのコンテナの中に入って、mysqlのcliで確認

```console
docker exec -it test-mysql bash -l
```

```console
mysql -p
```

useコマンドでdatabaseを指定した後、show tablesでみてみよう。

```console
mysql>use db_example;

mysql> show tables;
+----------------------+
| Tables_in_db_example |
+----------------------+
| hibernate_sequence   |
| user                 |
+----------------------+
```

## APIで動作確認

lesson1で作った、APIを叩いて動作を確認

### ユーザー登録

```console
curl -X POST http://localhost:8080/user -d '{"email": "aaa", "password": "bbbb"}' -H 'Content-Type: application/json'

{"id":1,"email":"aaa","password":"bbbb"}
```

### ユーザー取得

```console
curl 'http://localhost:8080/user?id=1'

{"id":1,"email":"aaa","password":"bbbb"}
```

### MySQLのレコードを確認

```
mysql> select * from user;

+----+-------+----------+
| id | email | password |
+----+-------+----------+
|  1 | aaa   | bbbb     |
+----+-------+----------+
```

と言った、感じになってたらOK。


## もうちょっと深く学びたい人向け

- application.yaml の 次の値を変更して、`gradle bootRun`で起動した時のDBテーブルがどうなるか試してみよう。

```
hibernate: 
      ddl-auto: update
```

参考 Link - [Spring-Docs 17.9.1. Initialize a Database Using JPA](https://docs.spring.io/spring-boot/docs/2.6.14/reference/htmlsingle/#howto.data-initialization.using-jpa)
