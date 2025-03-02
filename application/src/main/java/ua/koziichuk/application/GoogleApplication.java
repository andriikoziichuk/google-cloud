package ua.koziichuk.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ua.koziichuk.application",
        "ua.koziichuk.calendar"
})
public class GoogleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoogleApplication.class, args);
    }
}
