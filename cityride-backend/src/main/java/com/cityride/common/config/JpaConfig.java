package com.cityride.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Active l'audit JPA : remplissage automatique de createdAt / updatedAt (voir BaseEntity).
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
