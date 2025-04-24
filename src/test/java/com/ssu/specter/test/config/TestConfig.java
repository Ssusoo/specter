package com.ssu.specter.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * <a href="https://velog.io/@dailylifecoding/Spring-BeanPostProcessor-Weird-Warning-Log">컴포넌트 스캔 방식으로 바꾼 이유</a>
 */
@TestConfiguration
@ComponentScan("com.ssu.specter.test.config.processor")
public class TestConfig {
}
