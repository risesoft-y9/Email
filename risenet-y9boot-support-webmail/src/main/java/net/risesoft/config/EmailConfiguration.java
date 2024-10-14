package net.risesoft.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.filter.EmailAddressFilter;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;

@Configuration
@EnableConfigurationProperties({Y9Properties.class, Y9WebMailProperties.class})
public class EmailConfiguration implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<EmailAddressFilter> checkUserLoginFilter() {
        FilterRegistrationBean<EmailAddressFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new EmailAddressFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(50);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }
}
