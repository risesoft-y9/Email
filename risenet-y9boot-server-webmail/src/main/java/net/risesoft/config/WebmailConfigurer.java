package net.risesoft.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(Y9Properties.class)
public class WebmailConfigurer implements WebMvcConfigurer {

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

}
