package com.wandoo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableJpaRepositories
public class Application {

    public static void main(String[] args) {
        run(Application.class, args);
    }

}
