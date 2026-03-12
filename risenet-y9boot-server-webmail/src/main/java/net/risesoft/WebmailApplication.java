package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.risesoft.y9.spring.boot.Y9Banner;

@SpringBootApplication
public class WebmailApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(WebmailApplication.class);
        springApplication.setBanner(new Y9Banner());
        springApplication.run(args);
    }
}
