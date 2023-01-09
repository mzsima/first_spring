package com.example.demo.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = {
    "com.example.demo.second.repository" }, entityManagerFactoryRef = "secondEntityManagerFactory", transactionManagerRef = "secondTransactionManager")
public class SecondDatasourceConfig {

  @Value("${app.datasource.second.packages-to-scan}")
  private String packagesToScan;

  @Bean(name = "secondDataSourceProperties")
  @ConfigurationProperties("app.datasource.second")
  public DataSourceProperties secondDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "secondDataSource")
  @ConfigurationProperties("app.datasource.second.configuration")
  public HikariDataSource secondDataSource(
      @Qualifier("secondDataSourceProperties") DataSourceProperties dataSourceProperties) {
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  @Bean(name = "secondJpaProperties")
  @ConfigurationProperties("app.jpa.second")
  public JpaProperties secondJpaProperties() {
    return new JpaProperties();
  }

  @Bean(name = "secondEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(
      @Qualifier("secondDataSource") DataSource dataSource,
      @Qualifier("secondJpaProperties") JpaProperties jpaProperties) {
    EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(jpaProperties);
    return builder.dataSource(dataSource)
        .packages(packagesToScan)
        .properties(jpaProperties.getProperties())
        .persistenceUnit("secondEntityManagerFactory")
        .build();
  }

  @Bean(name = "secondTransactionManager")
  public PlatformTransactionManager secondTransactionManager(
      @Qualifier("secondEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
    JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(jpaProperties);
    return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
  }

  private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
    return new HibernateJpaVendorAdapter();
  }
}
