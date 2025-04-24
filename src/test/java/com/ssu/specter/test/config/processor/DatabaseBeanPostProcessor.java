package com.ssu.specter.test.config.processor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * hikari pool 무제한 생성 막기 위해..
 * <a href=https://stackoverflow.com/questions/70957822/spring-boot-integration-tests-with-local-postgres-database-creating-lots-of-hika>참조</a>
 */
@Component
public class DatabaseBeanPostProcessor implements BeanPostProcessor {
	private static final DataSource dataSource;
	static {
		final HikariDataSource hikariDataSource = new HikariDataSource();
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setMaximumPoolSize(30);
		hikariConfig.copyStateTo(hikariDataSource);
		hikariDataSource.setJdbcUrl("jdbc:h2:mem:test");
		hikariDataSource.setUsername("sa");
		hikariDataSource.setDriverClassName("org.h2.Driver");
		dataSource = hikariDataSource;
	}

	@SuppressWarnings("all")
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof DataSource) {
			return dataSource;
		}
		return bean;
	}

	@SuppressWarnings("all")
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
}
