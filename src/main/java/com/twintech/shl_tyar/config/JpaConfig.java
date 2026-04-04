package com.twintech.shl_tyar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration for enabling auditing support.
 * This enables @CreatedDate and @LastModifiedDate annotations.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
