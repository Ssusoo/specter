package com.ssu.specter.global.config.security;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.util.Locale;

/**
 * P6Spy 로그 Formatter
 */
public class P6spyPrettySqlFormatter implements MessageFormattingStrategy {

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
		sql = formatSql(category, sql);
		return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + sql;
	}

	// 쿼리로그 줄바꿈 처리
	private String formatSql(String category, String sql) {
		if (sql == null || sql.trim().isEmpty()) {
			return sql;
		}
		if (Category.STATEMENT.getName().equals(category) || Category.BATCH.getName().equals(category)) {
			String temporarySql = sql.trim().toLowerCase(Locale.ROOT);
			if (temporarySql.startsWith("create") || temporarySql.startsWith("alter") || temporarySql.startsWith("comment")) {
				sql = FormatStyle.DDL.getFormatter().format(sql);
			} else {
				sql = FormatStyle.BASIC.getFormatter().format(sql);
			}
			sql = "|\nSql(P6Spy sql, Hibernate format):" + sql;
		}
		return sql;
	}
}
