package com.back.p67260811;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class P67260811Application {

    public static void main(String[] args) {
        SpringApplication.run(P67260811Application.class, args);
    }

}
