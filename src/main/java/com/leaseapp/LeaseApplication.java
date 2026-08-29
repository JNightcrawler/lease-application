package com.leaseapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeaseApplication {

    static {
        Dotenv dotenv = Dotenv.load();
    }

    public static void main(String[] args) {
        SpringApplication.run(LeaseApplication.class, args);
    }
}
