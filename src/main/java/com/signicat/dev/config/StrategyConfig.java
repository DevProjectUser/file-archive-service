package com.signicat.dev.config;

import com.signicat.dev.enums.ArchiveType;
import com.signicat.dev.strategy.ArchivalStrategy;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class StrategyConfig {

    private final List<ArchivalStrategy> archivalStrategies;

    @Bean
    public Map<ArchiveType, ArchivalStrategy> archivingStrategyByType() {
        Map<ArchiveType, ArchivalStrategy> archivalByType = new EnumMap<>(ArchiveType.class);
        archivalStrategies.forEach(archivalStrategy -> archivalByType.put(archivalStrategy.getArchiveType(), archivalStrategy));
        return archivalByType;
    }
}
