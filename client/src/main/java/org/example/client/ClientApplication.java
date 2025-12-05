package org.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import java.net.UnknownHostException;

@SpringBootApplication
@ComponentScan({"org.example.client", "org.example.shared"})
@EnableConfigurationProperties({Config.class})
public class ClientApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(ClientApplication.class, args);
    }

}
