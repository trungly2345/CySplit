package manytoone.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class WebSocketSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketSpringbootApplication.class, args);
    }


}
