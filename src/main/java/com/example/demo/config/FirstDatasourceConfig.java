package com.example.demo.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = { "com.example.demo.first.repository" }, entityManagerFactoryRef = "firstEntityManagerFactory", transactionManagerRef = "firstTransactionManager")
public class FirstDatasourceConfig {

  @Value("${app.datasource.first.packages-to-scan}")
  private String packagesToScan;

  @Bean
  @Primary
  @ConfigurationProperties("app.datasource.first")
  public DataSourceProperties firstDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  @ConfigurationProperties("app.datasource.first.configuration")
  public HikariDataSource firstDataSource(DataSourceProperties dataSourceProperties) {
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  @Bean
  @ConfigurationProperties("app.jpa.first")
  @Primary
  public JpaProperties firstJpaProperties() {
    return new JpaProperties();
  }

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {
    EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(jpaProperties);
    return builder.dataSource(dataSource)
        .packages(packagesToScan)
        .properties(jpaProperties.getProperties())
        .persistenceUnit("firstEntityManagerFactory")
        .build();
  }

  @Bean
  @Primary
  public PlatformTransactionManager firstTransactionManager(EntityManagerFactory entityManagerFactory) {
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
