package com.ssu.specter.test;

import com.ssu.specter.test.config.JpaTestConfig;
import com.ssu.specter.test.config.TestConfig;
import com.ssu.specter.test.config.TestProfile;
import com.ssu.specter.test.setup.DatabaseCleaner;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(TestProfile.TEST)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class, JpaTestConfig.class})
@Getter
public abstract class RepositoryTest {
	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	private EntityManager entityManager;
}
