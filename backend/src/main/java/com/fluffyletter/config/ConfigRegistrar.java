package com.fluffyletter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FluffyProperties.class)
public class ConfigRegistrar {
}
