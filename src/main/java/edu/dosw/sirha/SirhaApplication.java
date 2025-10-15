package edu.dosw.sirha;

import edu.dosw.sirha.config.DotenvApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SirhaApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SirhaApplication.class);
        app.addInitializers(new DotenvApplicationContextInitializer());
        app.run(args);
    }
}