package vuinfinitecart.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VuInfiniteCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(VuInfiniteCartApplication.class, args);
    }
}
