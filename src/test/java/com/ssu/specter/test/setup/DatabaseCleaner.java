package com.ssu.specter.test.setup;

import com.ssu.specter.test.config.TestProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(TestProfile.TEST)
@RequiredArgsConstructor
@Component
@SuppressWarnings("all")
public class DatabaseCleaner {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void truncateAll() {
		var tables = jdbcTemplate.queryForList(
				"SELECT Concat('TRUNCATE TABLE ', TABLE_NAME, ' RESTART IDENTITY;') AS q " +
						"FROM INFORMATION_SCHEMA.TABLES " +
						"WHERE TABLE_SCHEMA = 'PUBLIC' " +
						"AND (TABLE_NAME LIKE 'SSU_%')",
				String.class);
		execute(tables);
	}

	private void execute(List<String> tables) {
		jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE"); // h2
		tables.forEach(v -> jdbcTemplate.execute(v));
		jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE"); // h2
	}
}
