package ovh.sobiech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EstrogenApplication {
    public static void main(String[] args) {
        SpringApplication.run(EstrogenApplication.class, args);
    }

}
