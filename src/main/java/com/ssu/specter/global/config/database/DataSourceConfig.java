package com.ssu.specter.global.config.database;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Properties;

/**
 * {@code @EnableTransactionManagement} : XML 의 <tx:annotation-driven/>와 동일, Spring 의 선언적 트랜잭션 처리 기능 활성화
 * 해당 어노테이션을 사용하면 DataSourceTransactionManager 로 구성되기 때문에 @Scheduled 가 동작하지 않는 이슈가
 * 발생한다. 그래서 트랜잭션 매니저를 JpaTransactionManage 로 구현한다
 * {@code @Primary} 를 사용해 우선적으로 등록할 트랜잭션 매니져 Bean 을 지정
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = "*.*.*.domains.*.repository"
)
@RequiredArgsConstructor
@Slf4j
public class DataSourceConfig {
	private final Environment env;
	private final JpaProperties jpaProperties;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource masterDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.slave")
	public DataSource slaveDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
	                                    @Qualifier("slaveDataSource") DataSource slaveDataSource) {
		var routingDataSource = new ReplicationRoutingDataSource();
		var dataSourceMap = new HashMap<>();
		dataSourceMap.put("master", masterDataSource);
		dataSourceMap.put("slave", slaveDataSource);
		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(masterDataSource);

		return routingDataSource;
	}

	@Primary
	@Bean
	public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
		return new LazyConnectionDataSourceProxy(routingDataSource);
	}

	/**
	 * LocalContainerEntityManagerFactoryBean
	 * EntityManager 를 생성하는 팩토리
	 * SessionFactoryBean 과 동일한 역할, Datasource 와 mapper 를 스캔할 .xml 경로를 지정하듯이
	 * datasource 와 엔티티가 저장된 폴더 경로를 매핑해주면 된다.
	 */
	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("dataSource") DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("*.*.*.domains.*.domain");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.putAll(jpaProperties.getProperties());
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
		properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
		return properties;
	}

	/**
	 * JpaTransactionManager : EntityManagerFactory 를 전달받아 JPA 에서 트랜잭션을 관리
	 */
	@Primary
	@Bean
	public JpaTransactionManager transactionManager(
			@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean mfBean) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(mfBean.getObject());
		return transactionManager;
	}
}
