package com.ssu.specter.test.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssu.specter.test.setup.DatabaseCleaner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JpaTestConfig {
	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public DatabaseCleaner databaseCleaner() {
		return new DatabaseCleaner();
	}
}
